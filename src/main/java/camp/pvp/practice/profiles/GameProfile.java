package camp.pvp.practice.profiles;

import camp.pvp.practice.cosmetics.DeathAnimation;
import camp.pvp.practice.games.tournaments.Tournament;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.interactables.InteractableItem;
import camp.pvp.practice.interactables.InteractableItems;
import camp.pvp.practice.kits.CustomDuelKit;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.parties.PartyInvite;
import camp.pvp.practice.profiles.stats.ProfileELO;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.utils.ItemBuilder;
import camp.pvp.practice.utils.PlayerUtils;
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
        LOBBY, LOBBY_QUEUE, LOBBY_PARTY, LOBBY_TOURNAMENT, KIT_EDITOR, IN_GAME, SPECTATING
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
    private boolean spectatorVisibility, lobbyVisibility, comboMessages, tournamentNotifications, showSidebar,
                    sidebarInGame, sidebarShowDuration, sidebarShowCps, sidebarShowLines, sidebarShowPing,
                    staffMode, buildMode, debugMode;
    private DeathAnimation deathAnimation;

    private Game game;
    private GameQueue previousQueue;
    private Tournament tournament;
    private Map<UUID, DuelRequest> duelRequests;

    private List<Date> clicks;

    private Party party;
    private Map<UUID, PartyInvite> partyInvites;

    private DuelKit editingKit;
    private CustomDuelKit editingCustomKit;
    private Map<DuelKit, Map<Integer, CustomDuelKit>> customDuelKits;

    private ProfileELO profileElo;

    public GameProfile(UUID uuid) {
        this.uuid = uuid;

        this.deathAnimation = DeathAnimation.DEFAULT;

        this.partyInvites = new HashMap<>();
        this.duelRequests = new HashMap<>();
        this.clicks = new ArrayList<>();

        this.customDuelKits = new HashMap<>();

        this.time = Time.DAY;
        this.lobbyVisibility = true;
        this.spectatorVisibility = true;
        this.comboMessages = true;
        this.tournamentNotifications = true;
        this.showSidebar = true;

        this.sidebarInGame = true;
        this.sidebarShowDuration = true;
        this.sidebarShowLines = true;
        this.sidebarShowPing = true;

        this.profileElo = new ProfileELO(uuid);

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
        } else if(tournament != null) {
            return State.LOBBY_TOURNAMENT;
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

    public void givePlayerItems() {
        Player player = getPlayer();
        if(player != null) {
            PlayerUtils.reset(player);

            PlayerInventory pi = player.getInventory();
            for(InteractableItems i : InteractableItems.getInteractableItems(this)) {
                InteractableItem ii = i.getItem();
                pi.setItem(ii.getSlot(), ii.getItem().clone());
            }

            int slot = 0;
            switch(this.getState()) {
                case LOBBY_TOURNAMENT:
                    slot = 1;
                    break;
                case LOBBY:
                case LOBBY_PARTY:
                    slot = 2;
                    break;
                case SPECTATING:
                case IN_GAME:
                case LOBBY_QUEUE:
                    slot = 0;
                    break;
            }

            player.getInventory().setHeldItemSlot(slot);

            if(game != null && game.getAlive().get(this.getUuid()) != null) {
                DuelKit kit = game.getKit();
                Map<Integer, CustomDuelKit> customKits = getCustomDuelKits().get(kit);
                if(customKits == null || customKits.isEmpty()) {
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

    public void playerUpdate(boolean updateLocation) {
        Player player = getPlayer();
        if(player != null) {
            updatePlayerVisibility();
            givePlayerItems();
//            setEditing(null);
//            setRenaming(null);

            player.setPlayerTime(this.getTime().getTime(), false);

            Location location = null;
            State state = getState();
            boolean check = false;
            switch (state) {
                case LOBBY_QUEUE:
                case LOBBY_PARTY:
                case LOBBY_TOURNAMENT:
                case LOBBY:
                    if (player.hasPermission("practice.lobby.fly")) {
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

            if(updateLocation) {
                if (check) {
                    if (location == null) {
                        player.sendMessage(ChatColor.RED + "Location for " + getState().toString().toLowerCase() + " could not be found.");
                    } else {
                        player.teleport(location);
                    }
                }
            }
        }

    }

    public void updatePlayerVisibility() {
        Player player = getPlayer();
        GameProfileManager gpm = Practice.instance.getGameProfileManager();
        if(player != null) {
            if(game != null) {
                if(game.seeEveryone()) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if(game.getAllPlayers().contains(p)) {
                            player.showPlayer(p);
                        } else {
                            player.hidePlayer(p);
                        }
                    }
                } else {
                    if(game.getCurrentPlayersPlaying().contains(player)) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if(!game.getCurrentPlayersPlaying().contains(p)) {
                                player.hidePlayer(p);
                            } else {
                                player.showPlayer(p);
                            }
                        }
                    } else {
                        boolean seeSpectators = this.isSpectatorVisibility();
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            GameProfile profile = gpm.getLoadedProfiles().get(p.getUniqueId());
                            boolean spectating = game.getSpectators().containsKey(p.getUniqueId());
                            boolean playing = game.getCurrentPlayersPlaying().contains(p);
                            boolean hide = false;
                            if(playing) {

                            } else {
                                if(spectating) {
                                    if (seeSpectators) {
                                        if (profile.isStaffMode() && !player.hasPermission("practice.staff")) {
                                            hide = true;
                                        }
                                    } else {
                                        hide = true;
                                    }
                                } else {
                                    hide = true;
                                }
                            }

                            if(hide) {
                                player.hidePlayer(p);
                            } else {
                                player.showPlayer(p);
                            }
                        }
                    }
                }
            } else {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    GameProfile profile = gpm.getLoadedProfiles().get(p.getUniqueId());
                    if((profile.getGame() != null && profile.getGame().getSpectators().get(p.getUniqueId()) != null) || !this.isLobbyVisibility() || this.getState().equals(State.KIT_EDITOR)) {
                        player.hidePlayer(p);
                    } else {
                        if(profile.isStaffMode() && !player.hasPermission("practice.staff")) {
                            player.hidePlayer(p);
                        } else {
                            player.showPlayer(p);
                        }
                    }
                }
            }
        }
    }

    public void addClick() {
        getClicks().add(new Date());
    }

    public int getCps() {
        Date now = new Date();

        int i = 0;
        for(Date d : new ArrayList<>(getClicks())) {
            if (now.getTime() - d.getTime() < 1000) {
                i++;
            } else {
                getClicks().remove(d);
            }
        }

        return i;
    }

    public void documentImport(Document document) {
        this.name = document.getString("name");
        this.buildMode = document.getBoolean("build_mode");
        this.debugMode = document.getBoolean("debug_mode");
        this.staffMode = document.getBoolean("staff_mode");
        this.spectatorVisibility = document.getBoolean("spectator_visibility");
        this.lobbyVisibility = document.getBoolean("lobby_visibility");
        this.tournamentNotifications = document.getBoolean("tournament_notifications");
        this.comboMessages = document.getBoolean("combo_messages");
        this.time = Time.valueOf(document.getString("player_time"));
        this.deathAnimation = DeathAnimation.valueOf(document.getString("death_animation"));
        this.showSidebar = document.getBoolean("show_sidebar");
        this.sidebarInGame = document.getBoolean("sidebar_in_game");
        this.sidebarShowCps = document.getBoolean("sidebar_show_cps");
        this.sidebarShowDuration = document.getBoolean("sidebar_show_duration");
        this.sidebarShowLines = document.getBoolean("sidebar_show_lines");
        this.sidebarShowPing = document.getBoolean("sidebar_show_ping");

        // Get serialized kits from document, and turn them back into CustomDuelKits.
        Object serializedKits = document.get("custom_duel_kits");

        Map<String, Map<String, Map<String, Object>>> ck = (Map<String, Map<String, Map<String, Object>>>) serializedKits;

        for(Map.Entry<String, Map<String, Map<String, Object>>> kitEntry : ck.entrySet()) {

            DuelKit kit = DuelKit.valueOf(kitEntry.getKey());

            for(Map.Entry<String, Map<String, Object>> customKitEntry : kitEntry.getValue().entrySet()) {

                int i = Integer.parseInt(customKitEntry.getKey());

                Map<String, Object> map = customKitEntry.getValue();

                CustomDuelKit customDuelKit = new CustomDuelKit(kit, i, true);
                getCustomDuelKits().get(kit).put(i, customDuelKit);

                customDuelKit.importFromMap(map);
            }
        }
    }

    public Map<String, Object> export() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("player_time", time.toString());
        values.put("build_mode", buildMode);
        values.put("debug_mode", debugMode);
        values.put("staff_mode", staffMode);
        values.put("spectator_visibility", spectatorVisibility);
        values.put("lobby_visibility", lobbyVisibility);
        values.put("tournament_notifications", tournamentNotifications);
        values.put("death_animation", deathAnimation.name());
        values.put("combo_messages", comboMessages);
        values.put("show_sidebar", isShowSidebar());
        values.put("sidebar_in_game", isSidebarInGame());
        values.put("sidebar_show_cps", isSidebarShowCps());
        values.put("sidebar_show_duration", isSidebarShowDuration());
        values.put("sidebar_show_lines", isSidebarShowLines());
        values.put("sidebar_show_ping", isSidebarShowPing());

        // Convert CustomDuelKits to serialized form for DB storage.
        Map<String, Map<String, Map<String, Object>>> ck = new HashMap<>();
        for(Map.Entry<DuelKit, Map<Integer, CustomDuelKit>> kitEntry : getCustomDuelKits().entrySet()) {
            DuelKit kit = kitEntry.getKey();
            for(Map.Entry<Integer, CustomDuelKit> customKitEntry : kitEntry.getValue().entrySet()) {
                CustomDuelKit cdk = customKitEntry.getValue();
                ck.computeIfAbsent(kit.toString(), v -> new HashMap<>());
                ck.get(kit.toString()).put(String.valueOf(cdk.getSlot()), cdk.exportItems());
            }
        }

        values.put("custom_duel_kits", ck);

        return values;
    }
}
