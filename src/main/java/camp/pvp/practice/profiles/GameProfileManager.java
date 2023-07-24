package camp.pvp.practice.profiles;

import camp.pvp.practice.Practice;
import camp.pvp.NetworkHelper;
import camp.pvp.mongo.MongoCollectionResult;
import camp.pvp.mongo.MongoManager;
import camp.pvp.mongo.MongoUpdate;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class GameProfileManager {

    private Practice plugin;
    private Logger logger;
    private @Getter Map<UUID, GameProfile> loadedProfiles;

    private @Getter MongoManager mongoManager;
    private String profilesCollection;
    public GameProfileManager(Practice plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.loadedProfiles = new HashMap<>();

        FileConfiguration config = plugin.getConfig();

        this.mongoManager = new MongoManager(plugin, config.getString("networking.mongo.uri"), config.getString("networking.mongo.database"));
        this.profilesCollection = config.getString("networking.mongo.profiles_collection");

        this.logger.info("Started GameProfileManager.");
    }

    public GameProfile find(UUID uuid, boolean store) {
        GameProfile profile = loadedProfiles.get(uuid);
        if(profile == null) {
            profile = importFromDatabase(uuid, store);
            if(store) {
                loadedProfiles.put(uuid, profile);
            }
        }

        return profile;
    }

    public GameProfile find(String name, boolean store) {
        final GameProfile[] profile = {null};
        mongoManager.getCollection(false, profilesCollection, new MongoCollectionResult() {
            @Override
            public void call(MongoCollection<Document> mongoCollection) {
                Document doc = mongoCollection.find(Filters.regex("name", "(?i)" + name)).first();
                if(doc != null) {
                    profile[0] = importFromDatabase(doc.get("_id", UUID.class), store);
                }
            }
        });

        return profile[0];
    }

    public GameProfile.State getState(UUID uuid) {
        return getState(find(uuid, true));
    }

    public GameProfile.State getState(GameProfile profile) {
        return GameProfile.State.LOBBY;
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

        mongoManager.massUpdate(false, mu);
        this.loadedProfiles.put(player.getUniqueId(), profile);
        return profile;
    }

    public GameProfile importFromDatabase(UUID uuid, boolean store) {
        final GameProfile[] profile = {null};
        mongoManager.getDocument(false, profilesCollection, uuid, document -> {
            if(document != null) {
                profile[0] = new GameProfile(uuid);
                profile[0].documentImport(document);
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

    public void shutdown() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            exportToDatabase(player.getUniqueId(), false, false);
        }

        plugin.getLogger().info("All player profiles have been exported to the database.");
    }
}
