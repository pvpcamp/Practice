package camp.pvp.profiles;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import camp.pvp.interactables.InteractableItem;
import camp.pvp.interactables.InteractableItems;
import camp.pvp.utils.PlayerUtils;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

@Getter @Setter
public class GameProfile {

    public enum State {
        LOBBY, LOBBY_QUEUE, LOBBY_PARTY, KIT_EDITOR, IN_GAME, SPECTATING
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
    private final UUID uuid;
    private String name;
    private Time time;
    private boolean buildMode;

    private Game game;

    public GameProfile(UUID uuid) {
        this.uuid = uuid;

        this.time = Time.DAY;
        this.buildMode = false;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public State getState() {
        if(game != null) {
            if(game.getAlive().get(this.uuid) != null) {
                return State.IN_GAME;
            } else {
                return State.SPECTATING;
            }
        } else {
            return State.LOBBY;
        }
    }

    public List<InteractableItem> getPlayerItems() {
        State state = this.getState();
        List<InteractableItem> items = new ArrayList<>();
        for(InteractableItems i : InteractableItems.values()) {
            if(state.equals(i.getState())) {
                items.add(i.getItem());
            }
        }

        return items;
    }

    public void givePlayerItems() {
        Player player = getPlayer();
        if(player != null) {
            PlayerUtils.reset(player);

            PlayerInventory pi = player.getInventory();
            for(InteractableItem i : getPlayerItems()) {
                pi.setItem(i.getSlot(), i.getItem().clone());
            }
        }
    }

    public void playerUpdate() {
        Player player = getPlayer();
        if(player != null) {
            updatePlayerVisibility();
            givePlayerItems();
//            setEditing(null);
//            setRenaming(null);

            Location location = null;
            State state = getState();
            boolean check = false;
            switch(state) {
                case LOBBY:
                    location = Practice.instance.getLobbyLocation();
                    check = true;
                    break;
                case KIT_EDITOR:
                    location = Practice.instance.getKitEditorLocation();
                    check = true;
                    break;
            }

            if(check) {
                if(location == null) {
                    player.sendMessage(ChatColor.RED + "Location for " + getState().toString().toLowerCase() + " could not be found.");
                } else {
                    player.teleport(location);
                }
            }
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
