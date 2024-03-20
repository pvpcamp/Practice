package camp.pvp.practice.games.impl.teams;

import camp.pvp.practice.games.GameTeam;
import camp.pvp.practice.games.tasks.TeleportFix;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.PostGameInventory;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.utils.PlayerUtils;
import camp.pvp.practice.utils.TimeUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamDuel extends TeamGame {

    public TeamDuel(Practice plugin, UUID uuid) {
        super(plugin, uuid);
    }

    @Override
    public List<String> getScoreboard(GameProfile profile) {
        List<String> lines = new ArrayList<>();

        GameParticipant self = getParticipants().get(profile.getUuid());
        GameTeam friendlyTeam = self.getTeam();

        boolean showInGame = profile.isSidebarInGame(),
                showCps = profile.isSidebarShowCps(),
                showDuration = profile.isSidebarShowDuration(),
                showPing = profile.isSidebarShowPing();

        if(!getState().equals(State.ENDED)) {
            String blue = "&9B &fBlue: ";
            String red = "&cR &fRed: ";

            if (getKit().isBedwars()) {
                blue = blue + "&9&l" + (getBlue().isRespawn() ? "✔" : getBlue().getCurrentParticipants().size());
                red = red + "&c&l" + (getRed().isRespawn() ? "✔" : getRed().getCurrentParticipants().size());
            } else {
                blue = blue + "&9&l" + getBlue().getAliveParticipants().size() + "/" + getBlue().getParticipants().size();
                red = red + "&c&l" + getRed().getAliveParticipants().size() + "/" + getRed().getParticipants().size();
            }

            blue = blue + (friendlyTeam.getColor().equals(GameTeam.Color.BLUE) ? " &7YOU" : "");
            red = red + (friendlyTeam.getColor().equals(GameTeam.Color.RED) ? " &7YOU" : "");

            lines.add(blue);
            lines.add(red);
        }

        switch(getState()) {
            case STARTING:
                lines.add(" ");
                lines.add("&6Kit: &f" + getKit().getGameKit().getDisplayName());
                lines.add("&6Arena: &f" + getArena().getDisplayName());
                break;
            case ACTIVE:
                if(!showInGame) {
                    return null;
                }

                lines.add(" ");

                List<GameParticipant> teammateParticipants = new ArrayList<>(friendlyTeam.getAliveParticipants().values());
                teammateParticipants.remove(self);

                if(teammateParticipants.isEmpty()) {
                    lines.add("&c&oNo Teammates Alive!");
                } else {

                    List<Player> teammates = new ArrayList<>();
                    for (GameParticipant participant : friendlyTeam.getAliveParticipants().values()) {
                        if(!participant.getUuid().equals(profile.getUuid())) teammates.add(participant.getPlayer());
                    }

                    lines.add(friendlyTeam.getColor().getChatColor() + "Alive Teammates &7(" + teammates.size() + "):");

                    teammates.sort(Comparator.comparingDouble(Player::getHealth));

                    for (int x = 0; x < 5; x++) {
                        if (teammates.size() == x) break;
                        if (x == 4) {
                            lines.add("&7...");
                            break;
                        }

                        Player p = teammates.get(x);
                        double health = p.getHealth();
                        lines.add((health < 4 ? "&c&l>" : "&7") + "> &f" + p.getName() + " &c" + Math.round(p.getHealth()) + " ❤");
                    }
                }

                if(showPing || showCps || showDuration) {
                    lines.add(" ");
                }

                int ping = 0;
                ping = PlayerUtils.getPing(self.getPlayer());

                if(showDuration) {
                    lines.add("&6Duration: &f" + TimeUtil.get(new Date(), getStarted()));
                }

                if(showPing) {
                    lines.add("&6Ping: &f" + ping + " ms");
                }

                if(showCps) {
                    lines.add("&6CPS: &f" + profile.getCps());
                }

                break;
            case ENDED:
                lines.add("&6&lYour Team Wins!");

                if(showDuration) {
                    lines.add("&6Duration: &f&n" + TimeUtil.get(getEnded(), getStarted()));
                }

                break;
        }
        return lines;
    }

    @Override
    public List<String> getSpectatorScoreboard(GameProfile profile) {
        List<String> lines = new ArrayList<>();

        boolean showDuration = profile.isSidebarShowDuration();

        String blue = "&9B &fBlue: ";
        String red = "&cR &fRed: ";

        if (getKit().isBedwars()) {
            lines.add(blue + "&9&l" + (getBlue().isRespawn() ? "✓" : getBlue().getCurrentParticipants().size()));
            lines.add(red + "&c&l" + (getRed().isRespawn() ? "✓" : getRed().getCurrentParticipants().size()));
        } else {
            lines.add(blue + "&9&l" + getBlue().getAliveParticipants().size() + "/" + getBlue().getParticipants().size());
            lines.add(red + "&c&l" + getRed().getAliveParticipants().size() + "/" + getRed().getParticipants().size());
        }

        switch(getState()) {
            case STARTING:
                lines.add(" ");
                lines.add("&6Kit: &f" + getKit().getGameKit().getDisplayName());
                lines.add("&6Arena: &f" + getArena().getDisplayName());
                break;
            case ACTIVE:
                if(showDuration) {
                    lines.add(" ");
                    lines.add("&6Duration: &f" + TimeUtil.get(new Date(), getStarted()));
                }

                break;
            case ENDED:
                if(showDuration) {
                    lines.add(" ");
                    lines.add("&6Duration: &f&n" + TimeUtil.get(getEnded(), getStarted()));
                }
                break;
        }

        return lines;
    }

    @Override
    public void initialize() {
        setArena(getPlugin().getArenaManager().selectRandomArena(getKit()));

        if(getArena() == null) {
            for(Player p : getAlivePlayers()) {
                GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(p.getUniqueId());
                p.sendMessage(ChatColor.RED + "There are no arenas currently available for the ladder selected. Please notify a staff member.");
                profile.setGame(null);
                profile.playerUpdate(true);
            }
            return;
        }

        Arena arena = getArena();

        arena.prepare();

        for(Party party : this.getParties()) {
            party.setGame(this);
        }

        if(getKit().isRespawn()) {
            getBlue().setRespawn(true);
            getRed().setRespawn(true);
        }

        this.setState(State.STARTING);

        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        sb.append("\n&6&lTeam Duel starting in 5 seconds.");
        sb.append("\n &7● &6Kit: &f" + getKit().getGameKit().getDisplayName());
        sb.append("\n &7● &6Map: &f" + arena.getDisplayName());

        ArenaPosition blueSpawn = arena.getPositions().get("spawn1");
        List<GameParticipant> blueParticipants = new ArrayList<>(getBlue().getParticipants().values());
        sb.append("\n &7● &9Blue Team: &f");
        int blueCount = 0;
        for(GameParticipant participant : getBlue().getParticipants().values()) {
            Player player = participant.getPlayer();
            sb.append(ChatColor.WHITE + participant.getName());

            blueParticipants.remove(participant);
            blueCount++;
            if (blueCount == getBlue().getParticipants().size()) {
                sb.append("&7.");
            } else {
                sb.append("&7, ");
            }

            participant.setSpawnLocation(blueSpawn.getLocation());
            player.teleport(blueSpawn.getLocation());
            participant.getProfile().givePlayerItems();
        }

        ArenaPosition redSpawn = arena.getPositions().get("spawn2");
        List<GameParticipant> redParticipants = new ArrayList<>(getRed().getParticipants().values());
        sb.append("\n &7● &cRed Team: &f");
        int redCount = 0;

        for(GameParticipant participant : getRed().getParticipants().values()) {
            Player player = participant.getPlayer();
            sb.append(ChatColor.WHITE + participant.getName());

            redParticipants.remove(participant);
            redCount++;
            if (redCount == getRed().getParticipants().size()) {
                sb.append("&7.");
            } else {
                sb.append("&7, ");
            }

            participant.setSpawnLocation(redSpawn.getLocation());
            player.teleport(redSpawn.getLocation());
            participant.getProfile().givePlayerItems();
        }

        sb.append("\n ");

        this.announce(sb.toString());

        getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();

        Bukkit.getScheduler().runTaskLater(getPlugin(), new TeleportFix(this), 1);
        startingTimer(3);
    }

    @Override
    public void end() {
        if(this.getState().equals(State.ENDED)) {
            return;
        }

        this.setEnded(new Date());
        this.setState(State.ENDED);

        if(getStarted() == null) {
            setStarted(new Date());
            getStartingTimer().cancel();
        }

        for(Party party : this.getParties()) {
            party.setGame(null);
        }

        GameTeam winningTeam;
        if(getBlue().isEliminated()) {
            winningTeam = getRed();
        } else {
            winningTeam = getBlue();
        }

        for(GameParticipant p : winningTeam.getParticipants().values()) {
            Player player = p.getPlayer();
            if(player != null) {
                sendLunarWinTitle(player);
            }
        }

        for(GameParticipant participant : getAlive().values()) {
            Player player = participant.getPlayer();
            PostGameInventory pgi = new PostGameInventory(UUID.randomUUID(), participant, player.getInventory().getContents(), player.getInventory().getArmorContents());
            participant.setPostGameInventory(pgi);
            getPlugin().getGameManager().addInventory(pgi);
        }

        List<TextComponent> components = new ArrayList<>();
        List<GameParticipant> sortedParticipants = new ArrayList<>(this.getParticipants().values());
        sortedParticipants.sort(Comparator.comparing(GameParticipant::getName));

        for(GameParticipant p : sortedParticipants) {
            TextComponent text = new TextComponent(ChatColor.WHITE + p.getName());
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/postgameinventory " + p.getPostGameInventory().getUuid().toString()));
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to view " + ChatColor.WHITE + p.getName() + "'s " + ChatColor.GREEN + "inventory.").create()));
            components.add(text);
        }

        // Match Summary Message
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" ");
        stringBuilder.append(Colors.get("&6&lMatch ended."));
        if(winningTeam != null) {
            stringBuilder.append(Colors.get("\n &7● &6Winning Team: " + winningTeam.getColor().getChatColor() + winningTeam.getColor().getName()));
        }

        TextComponent text = new TextComponent(ChatColor.GRAY + " ● " + ChatColor.GOLD + "Inventories: ");
        final int size = components.size();
        int x = 0;
        while(x != size) {
            TextComponent t = components.get(0);
            text.addExtra(t);
            components.remove(t);
            x++;
            if(x == size) {
                text.addExtra(new TextComponent(ChatColor.GRAY + "."));
            } else {
                text.addExtra(new TextComponent(ChatColor.GRAY + ", "));
            }
        }

        for(Player player : this.getAllPlayers()) {
            player.sendMessage(" ");
            player.sendMessage(stringBuilder.toString());
            player.spigot().sendMessage(text);
            player.sendMessage(" ");
        }

        cleanup(3);
    }

    @Override
    public GameParticipant createParticipant(Player player) {
        return new GameParticipant(player.getUniqueId(), player.getName());
    }

    @Override
    public String getScoreboardTitle() {
        return "Teams";
    }
}
