package camp.pvp.practice.profiles;

import camp.pvp.practice.Practice;
import camp.pvp.mongo.MongoCollectionResult;
import camp.pvp.mongo.MongoManager;
import camp.pvp.mongo.MongoUpdate;
import camp.pvp.practice.profiles.stats.ProfileELO;
import camp.pvp.practice.profiles.leaderboard.LeaderboardUpdater;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class GameProfileManager {

    private Practice plugin;
    private Logger logger;
    private @Getter Map<UUID, GameProfile> loadedProfiles;

    private @Getter MongoManager mongoManager;
    private @Getter String profilesCollection, eloCollection;

    private BukkitTask leaderboardUpdaterTask;
    private @Getter LeaderboardUpdater leaderboardUpdater;
    public GameProfileManager(Practice plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.loadedProfiles = new HashMap<>();

        FileConfiguration config = plugin.getConfig();

        this.mongoManager = new MongoManager(plugin, config.getString("networking.mongo.uri"), config.getString("networking.mongo.database"));
        this.profilesCollection = config.getString("networking.mongo.profiles_collection");
        this.eloCollection = config.getString("networking.mongo.elo_collection");

        this.leaderboardUpdater = new LeaderboardUpdater(this);
        this.leaderboardUpdaterTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, leaderboardUpdater, 0, 2400);

        this.logger.info("Started GameProfileManager.");
    }

    public GameProfile find(UUID uuid, boolean store) {
        GameProfile profile = loadedProfiles.get(uuid);
        if(profile == null) {
            profile = importFromDatabase(uuid, false, store);

            if(profile != null) {
                ProfileELO elo = importElo(uuid, false);
                if (elo == null) {
                    elo = new ProfileELO(uuid);
                    elo.setName(profile.getName());
                    profile.setProfileElo(elo);
                    exportElo(elo, true);
                }

                if(store) {
                    loadedProfiles.put(uuid, profile);
                }
            }
        }

        return profile;
    }

    public GameProfile find(String name, boolean store) {

        if(!name.matches("^[a-zA-Z0-9_]{1,16}$")) {
            return null;
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getName().equalsIgnoreCase(name)) {
                return getLoadedProfiles().get(player.getUniqueId());
            }
        }

        final GameProfile[] profile = {null};
        mongoManager.getCollection(false, profilesCollection, new MongoCollectionResult() {
            @Override
            public void call(MongoCollection<Document> mongoCollection) {
                Document doc = mongoCollection.find(Filters.regex("name", "(?i)" + name)).first();
                if(doc != null) {
                    GameProfile p = new GameProfile(doc.get("_id", UUID.class));
                    p.importFromDocument(doc);

                    ProfileELO elo = importElo(p.getUuid(), false);
                    if(elo == null) {
                        elo = new ProfileELO(p.getUuid());
                        elo.setName(p.getName());
                        p.setProfileElo(elo);
                        exportElo(elo, true);
                    }

                    if(store) {
                        getLoadedProfiles().put(p.getUuid(), p);
                    }
                    profile[0] = p;
                }
            }
        });

        return profile[0];
    }

    public void updateGlobalPlayerVisibility() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            GameProfile profile = getLoadedProfiles().get(player.getUniqueId());
            if(profile != null) {
                profile.updatePlayerVisibility();
            }
        }
    }

    public GameProfile create(Player player) {
        GameProfile profile = new GameProfile(player.getUniqueId());

        profile.setName(player.getName());

        MongoUpdate mu = new MongoUpdate(profilesCollection, profile.getUuid());
        mu.setUpdate(profile.export());


        ProfileELO profileELO = new ProfileELO(player.getUniqueId());
        profileELO.setName(player.getName());
        exportElo(profileELO, true);

        mongoManager.massUpdate(false, mu);
        this.loadedProfiles.put(player.getUniqueId(), profile);
        return profile;
    }

    public GameProfile importFromDatabase(UUID uuid, boolean async, boolean store) {
        final GameProfile[] profile = {null};
        mongoManager.getDocument(async, profilesCollection, uuid, document -> {
            if(document != null) {
                profile[0] = new GameProfile(uuid);
                profile[0].importFromDocument(document);
                importElo(uuid, async);

                if(store) {
                    loadedProfiles.put(uuid, profile[0]);
                }
            }
        });

        return profile[0];
    }

    public void exportToDatabase(UUID uuid, boolean async, boolean store) {
        GameProfile profile = loadedProfiles.get(uuid);
        if(profile != null) {
            MongoUpdate mu = new MongoUpdate(profilesCollection, profile.getUuid());
            mu.setUpdate(profile.export());
            mongoManager.massUpdate(async, mu);
        }

        if(!store) {
            loadedProfiles.remove(uuid);
        }
    }

    public ProfileELO importElo(UUID uuid, boolean async) {
        final ProfileELO[] elo = {null};
        mongoManager.getDocument(async, eloCollection, uuid, document -> {
            if(document != null) {
                elo[0] = new ProfileELO(uuid);
                elo[0].importFromDocument(document);
                GameProfile profile = getLoadedProfiles().get(uuid);
                if(profile != null) {
                    profile.setProfileElo(elo[0]);
                }
            }
        });

        return elo[0];
    }

    public void exportElo(ProfileELO elo, boolean async) {
        MongoUpdate mu = new MongoUpdate(eloCollection, elo.getUuid());
        mu.setUpdate(elo.export());
        mongoManager.massUpdate(async, mu);
    }

    public void shutdown() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            exportToDatabase(player.getUniqueId(), false, false);
        }

        plugin.getLogger().info("All player profiles have been exported to the database.");
    }
}
