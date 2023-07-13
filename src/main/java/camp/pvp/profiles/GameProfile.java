package camp.pvp.profiles;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import camp.pvp.games.PostGameInventory;
import camp.pvp.interactables.InteractableItem;
import camp.pvp.interactables.InteractableItems;
import camp.pvp.kits.CustomDuelKit;
import camp.pvp.kits.DuelKit;
import camp.pvp.parties.Party;
import camp.pvp.utils.ItemBuilder;
import camp.pvp.utils.PlayerUtils;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

@Getter @Setter
public class GameProfile {

    public enum State {
        LOBBY, LOBBY_QUEUE, LOBBY_PARTY, KIT_EDITOR, IN_GAME, SPECTATING
    }

    public enum Time {
        DAY, SUNSET, NIGHT;

        public long getTime() {
            switch(this) {
                case DAY: return 6000;
                case SUNSET: return 13000;
                case NIGHT: return 18000;
                default: return 1337;
            }

        }
    }

    // Stored DB values.
    private final UUID uuid;
    private String name;
    private Time time;
    private boolean buildMode, debugMode, spectatorVisibility, lobbyVisibility;

    private Game game;
    private Map<UUID, DuelRequest> duelRequests;

    private Party party;

    private DuelKit editingKit;
    private CustomDuelKit editingCustomKit;
    private Map<DuelKit, Map<Integer, CustomDuelKit>> customDuelKits;

    public GameProfile(UUID uuid) {
        this.uuid = uuid;

        this.duelRequests = new HashMap<>();
        this.customDuelKits = new HashMap<>();

        this.time = Time.DAY;
        this.buildMode = false;
        this.lobbyVisibility = true;
        this.spectatorVisibility = true;

        for(DuelKit kit : DuelKit.values()) {
            if(kit.isEditable()) {
                customDuelKits.put(kit, new HashMap<>());
            }
        }
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
        } else if(Practice.instance.getGameQueueManager().getQueue(uuid) != null) {
            return State.LOBBY_QUEUE;
        } else if(editingKit != null){
            return State.KIT_EDITOR;
        } else if(party != null){
            return State.LOBBY_PARTY;
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

            if(game != null && game.getAlive().get(this.getUuid()) != null) {
                DuelKit kit = game.getKit();
                Map<Integer, CustomDuelKit> customKits = getCustomDuelKits().get(kit);
                if(customKits.isEmpty()) {
                    game.getParticipants().get(uuid).setKitApplied(true);
                    kit.apply(player);
                    return;
                }

                for(Map.Entry<Integer, CustomDuelKit> entry : customKits.entrySet()) {
                    String name = entry.getValue().getName();
                    ItemStack i = new ItemBuilder(Material.ENCHANTED_BOOK, name).create();
                    pi.setItem(entry.getKey() - 1, i);
                }

                ItemStack defaultKit = new ItemBuilder(Material.BOOK, "&aDefault Kit").create();
                pi.setItem(8, defaultKit);
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
                case LOBBY_QUEUE:
                case LOBBY_PARTY:
                case LOBBY:
                    if(player.hasPermission("practice.lobby.fly")) {
                        player.setAllowFlight(true);
                    }
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
                            if(player.canSee(p)) {
                                player.hidePlayer(p);
                            }
                        } else {
                            if(!player.canSee(p)) {
                                player.showPlayer(p);
                            }
                        }
                    }
                } else {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        boolean b = this.isSpectatorVisibility() ? game.getAllPlayers().contains(p) : game.getCurrentPlaying().contains(p);
                        if (b) {
                            if(!player.canSee(p)) {
                                player.showPlayer(p);
                            }
                        } else {
                            if(player.canSee(p)) {
                                player.hidePlayer(p);
                            }
                        }
                    }
                }
            } else {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    GameProfile profile = gpm.getLoadedProfiles().get(p.getUniqueId());
                    if((profile.getGame() != null && profile.getGame().getSpectators().get(p.getUniqueId()) != null) || !this.isLobbyVisibility()) {
                        if(player.canSee(p)) {
                            player.hidePlayer(p);
                        }
                    } else {
                        if(!player.canSee(p)) {
                            player.showPlayer(p);
                        }
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
