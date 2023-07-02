package camp.pvp.profiles;

import camp.pvp.NetworkHelper;
import camp.pvp.Practice;
import camp.pvp.mongo.MongoManager;
import camp.pvp.mongo.MongoResult;
import camp.pvp.mongo.MongoUpdate;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class GameProfileManager {

    private Practice plugin;
    private Logger logger;
    private Map<UUID, GameProfile> loadedProfiles;

    private MongoManager mongoManager;
    public GameProfileManager(Practice plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.loadedProfiles = new HashMap<>();

        this.mongoManager = NetworkHelper.getInstance().getMongoManager();

        this.logger.info("Starting GameProfileManager...");
    }

    public GameProfile find(UUID uuid, boolean store) {
        GameProfile profile = loadedProfiles.get(uuid);
        if(profile == null) {
            logger.info("Did not find profile for " + uuid.toString());
            profile = importFromDatabase(uuid, store);
            if(store) {
                loadedProfiles.put(uuid, profile);
            }
        } else {
            logger.info("Found profile " + profile.getUuid().toString());
        }

        return profile;
    }

    public GameProfile.State getState(UUID uuid) {
        return getState(find(uuid, true));
    }

    public GameProfile.State getState(GameProfile profile) {
        return GameProfile.State.LOBBY;
    }

    public GameProfile create(Player player) {
        GameProfile profile = new GameProfile(player.getUniqueId());

        profile.setName(player.getName());
        logger.info("Player name: " + profile.getName());

        MongoUpdate mu = new MongoUpdate("practice_profiles", profile.getUuid());
        mu.setUpdate(profile.export());

        mongoManager.massUpdate(false, mu);
        this.loadedProfiles.put(player.getUniqueId(), profile);
        return profile;
    }

    public GameProfile importFromDatabase(UUID uuid, boolean store) {
        final GameProfile[] profile = {null};
        mongoManager.getDocument(false, "practice_profiles", uuid, document -> {
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
            MongoUpdate mu = new MongoUpdate("practice_profiles", profile.getUuid());
            mu.setUpdate(profile.export());
            mongoManager.massUpdate(async, mu);
        }

        if(!store) {
            loadedProfiles.remove(uuid);
        }
    }

    public void shutdown() {

    }
}
