package camp.pvp.practice.profiles;

import camp.pvp.practice.Practice;
import camp.pvp.mongo.MongoManager;
import camp.pvp.practice.profiles.stats.MatchRecord;
import camp.pvp.practice.profiles.leaderboard.LeaderboardUpdater;
import camp.pvp.practice.profiles.stats.ProfileStatistics;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class GameProfileManager {

    private Practice plugin;
    private Logger logger;
    private @Getter Map<UUID, GameProfile> loadedProfiles;
    private @Getter Map<UUID, MatchRecord> matchRecords;

    private @Getter MongoManager mongoManager;
    private @Getter String profilesCollectionName, statisticsCollectionName, matchRecordsCollectionName;

    private BukkitTask leaderboardUpdaterTask, playerVisibilityUpdaterTask;
    private @Getter LeaderboardUpdater leaderboardUpdater;
    public GameProfileManager(Practice plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.loadedProfiles = new HashMap<>();
        this.matchRecords = new HashMap<>();

        this.logger.info("Initialized GameProfileManager.");

        FileConfiguration config = plugin.getConfig();

        this.mongoManager = new MongoManager(plugin, config.getString("networking.mongo.uri"), config.getString("networking.mongo.database"));
        this.profilesCollectionName = config.getString("networking.mongo.profiles_collection");
        this.statisticsCollectionName = config.getString("networking.mongo.statistics_collection");
        this.matchRecordsCollectionName = config.getString("networking.mongo.match_records_collection");

        this.leaderboardUpdater = new LeaderboardUpdater(this);
        this.leaderboardUpdaterTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, leaderboardUpdater, 0, 20 * 120);
        this.playerVisibilityUpdaterTask = Bukkit.getScheduler().runTaskTimer(plugin, new PlayerVisibilityUpdater(this), 0, 1);
    }

    public CompletableFuture<GameProfile> findAsync(String name) {
        if(!name.matches("^[a-zA-Z0-9_]{1,16}$")) {
            return CompletableFuture.supplyAsync(() -> null);
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getName().equalsIgnoreCase(name)) {
                return CompletableFuture.supplyAsync(() -> getLoadedProfiles().get(player.getUniqueId()));
            }
        }

        CompletableFuture<GameProfile> profileFuture = CompletableFuture.supplyAsync(() -> {

            Document doc = getProfilesCollection().find(Filters.regex("name", "(?i)" + name)).first();
            if(doc != null) {
                GameProfile p = new GameProfile(doc.get("_id", UUID.class));
                p.importFromDocument(doc);

                MongoCollection<Document> statisticsCollection = getStatisticsCollection();

                statisticsCollection.find(Filters.eq("_id", p.getUuid())).forEach(document -> {
                    ProfileStatistics profileStatistics = new ProfileStatistics(document.get("_id", UUID.class), document.get("name", p.getName()));
                    profileStatistics.importFromDocument(document);
                    p.setProfileStatistics(profileStatistics);
                });

                if(p.getProfileStatistics() == null) {
                    ProfileStatistics profileStatistics = new ProfileStatistics(p.getUuid(), p.getName());
                    p.setProfileStatistics(profileStatistics);

                    Document document = statisticsCollection.find(new Document("_id", profileStatistics.getUuid())).first();
                    if(document == null) {
                        statisticsCollection.insertOne(new Document("_id", profileStatistics.getUuid()));
                    }

                    profileStatistics.export().forEach((key, value) -> statisticsCollection.updateOne(Filters.eq("_id", profileStatistics.getUuid()), Updates.set(key, value)));
                }

                p.setLastLoadFromDatabase(System.currentTimeMillis());

                getLoadedProfiles().put(p.getUuid(), p);
                return p;
            } else {
                return null;
            }
        });

        profileFuture.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        return profileFuture;
    }

    public CompletableFuture<GameProfile> findAsync(UUID uuid) {
        if(Bukkit.getPlayer(uuid) != null) return CompletableFuture.supplyAsync(() -> getLoadedProfiles().get(uuid));

        CompletableFuture<GameProfile> profileFuture = CompletableFuture.supplyAsync(() -> {

            Document doc = getProfilesCollection().find(new Document("_id", uuid)).first();
            if(doc != null) {
                GameProfile p = new GameProfile(uuid);
                p.importFromDocument(doc);

                MongoCollection<Document> statisticsCollection = getStatisticsCollection();
                statisticsCollection.find(Filters.eq("_id", p.getUuid())).forEach(document -> {
                    ProfileStatistics profileStatistics = new ProfileStatistics(document.get("_id", UUID.class), document.get("name", p.getName()));
                    profileStatistics.importFromDocument(document);
                    p.setProfileStatistics(profileStatistics);
                });

                if(p.getProfileStatistics() == null) {
                    ProfileStatistics profileStatistics = new ProfileStatistics(p.getUuid(), p.getName());
                    p.setProfileStatistics(profileStatistics);

                    Document document = statisticsCollection.find(new Document("_id", profileStatistics.getUuid())).first();
                    if(document == null) {
                        statisticsCollection.insertOne(new Document("_id", profileStatistics.getUuid()));
                    }

                    profileStatistics.export().forEach((key, value) -> statisticsCollection.updateOne(Filters.eq("_id", profileStatistics.getUuid()), Updates.set(key, value)));
                }

                MongoCollection<Document> matchRecordsCollection = getMatchRecordsCollection();
                matchRecordsCollection.find(Filters.or(Filters.eq("winner", p.getUuid()), Filters.eq("loser", p.getUuid()))).forEach(document -> {
                    MatchRecord record = new MatchRecord(document.get("_id", UUID.class));
                    record.importFromDocument(document);
                    matchRecords.put(record.getUuid(), record);
                });

                p.setLastLoadFromDatabase(System.currentTimeMillis());

                getLoadedProfiles().put(p.getUuid(), p);
                return p;
            } else {
                return null;
            }
        });

        profileFuture.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        return profileFuture;
    }

    public GameProfile getLoadedProfile(UUID uuid) {
        GameProfile profile = getLoadedProfiles().get(uuid);
        return profile == null ? null : (profile.isValid() ? profile : null);
    }

    public GameProfile getLoadedProfile(String name) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getName().equalsIgnoreCase(name)) {
                GameProfile profile = getLoadedProfiles().get(player.getUniqueId());
                return profile.isValid() ? profile : null;
            }
        }

        return null;
    }

    public void updateGlobalPlayerVisibility() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            GameProfile profile = getLoadedProfiles().get(player.getUniqueId());
            if(profile != null) {
                profile.updatePlayerVisibility();
            }
        }
    }

    public void refreshLobbyItems() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            GameProfile profile = getLoadedProfiles().get(player.getUniqueId());
            if(profile != null) {
                if(profile.getState().equals(GameProfile.State.LOBBY)) {
                    profile.givePlayerItems(false);
                }
            }
        }
    }

    public void preLogin(UUID uuid, String name) {
        GameProfile profile = null;

        MongoCollection<Document> profilesCollection = getProfilesCollection();
        Document doc = profilesCollection.find().filter(new Document("_id", uuid)).first();
        if(doc != null) {
            profile = new GameProfile(uuid);
            profile.importFromDocument(doc);
        }

        if(profile == null) {
            profile = new GameProfile(uuid);
            profile.setName(name);

            Document document = profilesCollection.find(new Document("_id", uuid)).first();
            if(document == null) {
                profilesCollection.insertOne(new Document("_id", uuid));
            }

            profile.export().forEach((key, value) -> profilesCollection.updateOne(Filters.eq("_id", uuid), Updates.set(key, value)));
        }

        final GameProfile fProfile = profile;

        fProfile.setLastLoadFromDatabase(System.currentTimeMillis());

        getStatisticsCollection().find(Filters.eq("_id", uuid)).forEach(document -> {
            ProfileStatistics profileStatistics = new ProfileStatistics(document.get("_id", UUID.class), name);
            profileStatistics.importFromDocument(document);
            fProfile.setProfileStatistics(profileStatistics);
        });

        if(fProfile.getProfileStatistics() == null) {
            ProfileStatistics profileStatistics = new ProfileStatistics(uuid, name);
            fProfile.setProfileStatistics(profileStatistics);

            exportStatistics(profileStatistics, true);
        }

        getMatchRecordsCollection().find(Filters.or(Filters.eq("winner", uuid), Filters.eq("loser", uuid))).forEach(document -> {
            MatchRecord record = new MatchRecord(document.get("_id", UUID.class));
            record.importFromDocument(document);
            matchRecords.put(record.getUuid(), record);
        });

        getLoadedProfiles().put(uuid, fProfile);
    }

    public void logOff(UUID uuid) {
        GameProfile profile = loadedProfiles.get(uuid);
        if(profile == null) return;

        exportToDatabase(uuid, true);
        exportStatistics(profile.getProfileStatistics(), true);
    }

    public void exportToDatabase(UUID uuid, boolean async) {
        GameProfile profile = loadedProfiles.get(uuid);
        if(profile == null) return;

        if(async) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                profile.export().forEach((key, value) -> getProfilesCollection().updateOne(Filters.eq("_id", uuid), Updates.set(key, value)));
            });
        } else {
            profile.export().forEach((key, value) -> getProfilesCollection().updateOne(Filters.eq("_id", uuid), Updates.set(key, value)));
        }
    }

    public void exportStatistics(ProfileStatistics statistics, boolean async) {
        if(async) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                MongoCollection<Document> statisticsCollection = getStatisticsCollection();
                Document doc = statisticsCollection.find().filter(Filters.eq("_id", statistics.getUuid())).first();
                if(doc == null) {
                    statisticsCollection.insertOne(new Document("_id", statistics.getUuid()));
                }

                statistics.export().forEach((key, value) -> statisticsCollection.updateOne(Filters.eq("_id", statistics.getUuid()), Updates.set(key, value)));
            });
        } else {

            MongoCollection<Document> statisticsCollection = getStatisticsCollection();
            Document doc = statisticsCollection.find().filter(Filters.eq("_id", statistics.getUuid())).first();
            if(doc == null) {
                statisticsCollection.insertOne(new Document("_id", statistics.getUuid()));
            }

            statistics.export().forEach((key, value) -> statisticsCollection.updateOne(Filters.eq("_id", statistics.getUuid()), Updates.set(key, value)));
        }
    }

    public void exportMatchRecord(MatchRecord record) {
        matchRecords.put(record.getUuid(), record);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            MongoCollection<Document> matchRecordsCollection = getMatchRecordsCollection();
            Document doc = matchRecordsCollection.find().filter(Filters.eq("_id", record.getUuid())).first();
            if(doc == null) {
                matchRecordsCollection.insertOne(new Document("_id", record.getUuid()));
            }

            record.export().forEach((key, value) -> matchRecordsCollection.updateOne(Filters.eq("_id", record.getUuid()), Updates.set(key, value)));
        });
    }

    public MongoCollection<Document> getProfilesCollection() {
        return mongoManager.getDatabase().getCollection(profilesCollectionName);
    }

    public MongoCollection<Document> getStatisticsCollection() {
        return mongoManager.getDatabase().getCollection(statisticsCollectionName);
    }

    public MongoCollection<Document> getMatchRecordsCollection() {
        return mongoManager.getDatabase().getCollection(matchRecordsCollectionName);
    }

    public void shutdown() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            GameProfile profile = loadedProfiles.get(player.getUniqueId());
            profile.getProfileStatistics().export().forEach((key, value) -> getStatisticsCollection().updateOne(Filters.eq("_id", player.getUniqueId()), Updates.set(key, value)));
            profile.export().forEach((key, value) -> getProfilesCollection().updateOne(Filters.eq("_id", player.getUniqueId()), Updates.set(key, value)));
        }

        plugin.getLogger().info("All player profiles have been exported to the database.");
    }
}
