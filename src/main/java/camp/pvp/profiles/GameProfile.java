package camp.pvp.profiles;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameProfile {

    public enum State {
        LOBBY, KIT_EDITOR, IN_GAME, IN_GAME_WAITING, SPECTATING
    }

    public enum Time {
        SUNRISE, DAY, SUNSET, NIGHT;

        public long getTime() {
            switch(this) {
                case SUNRISE: return 0;
                case DAY: return 6000;
                case SUNSET: return 12000;
                case NIGHT: return 18000;
                default: return 1337;
            }

        }
    }

    // Stored DB values.
    private @Getter final UUID uuid;
    private @Getter @Setter String name;
    private @Getter @Setter Time time;

    private @Getter @Setter Game game;

    public GameProfile(UUID uuid) {
        this.uuid = uuid;

        this.time = Time.DAY;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void playerItems() {
        Player player = getPlayer();
        if(player != null) {
            playerReset();
        }
    }

    public void playerUpdate() {
        Player player = getPlayer();
        if(player != null) {
            updatePlayerVisibility();
            playerItems();
//            setEditing(null);
//            setRenaming(null);
//
//            switch(getState()) {
//                case LOBBY:
//                case PARTY:
//                case TOURNAMENT:
//                case QUEUE:
//                    Location location = PracticeModule.INSTANCE.getLobby();
//                    if (location != null) {
//                        player.teleport(PracticeModule.INSTANCE.getLobby());
//                    } else {
//                        player.sendMessage(ChatColor.RED + "You could not be teleported to the lobby. Please notify staff!");
//                    }
//            }
        }
    }

    public void updatePlayerVisibility() {
        Player player = getPlayer();
        GameProfileManager gpm = Practice.instance.getGameProfileManager();
        if(player != null) {
            if(game != null) {
                if(game.getCurrentPlaying().contains(player)) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
//                        !game.seeEveryone() &&
                        if(!game.getCurrentPlaying().contains(p)) {
                            player.hidePlayer(p);
                        } else {
                            player.showPlayer(p);
                        }
                    }
                } else {
                    for (Player p : Bukkit.getOnlinePlayers()) {
//                        boolean b = getSettings().isSpectatorVisibility() ? occupation.getAllPlayers().contains(p) : occupation.getCurrentPlaying().contains(p);
                        boolean b = game.getCurrentPlaying().contains(p);
                        if (b) {
                            player.showPlayer(p);
                        } else {
                            player.hidePlayer(p);
                        }
                    }
                }
            } else {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    GameProfile profile = gpm.find(p.getUniqueId(), true);
                    if(profile.getGame() != null && profile.getGame().getSpectators().get(p.getUniqueId()) != null) {
                        player.hidePlayer(p);
                    } else {
                        player.showPlayer(p);
                    }
                }
            }
        }
    }

    public void documentImport(Document document) {
        this.name = document.getString("name");
    }

    public Map<String, Object> export() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("time", time.toString());

        return values;
    }
}
