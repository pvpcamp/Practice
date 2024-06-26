package camp.pvp.practice.games.impl;

import camp.pvp.practice.games.tasks.TeleportFix;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.PostGameInventory;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.utils.PlayerUtils;
import camp.pvp.practice.utils.TimeUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class FreeForAll extends Game {

    public FreeForAll(Practice plugin, UUID uuid) {
        super(plugin, uuid);
    }

    @Override
    public void eliminate(Player player, boolean leftGame) {
        super.eliminate(player, leftGame);
        if (this.getAlive().size() < 2) {
            this.end();
        }
    }

    @Override
    public List<String> getScoreboard(GameProfile profile) {
        List<String> lines = new ArrayList<>();
        GameParticipant self = getAlive().get(profile.getUuid());

        switch(getState()) {
            case STARTING:
                lines.add("&6Kit: &f" + getKit().getGameKit().getDisplayName());
                lines.add("&6Arena: &f" + getArena().getDisplayName());
                lines.add("&6Players: &f" + this.getAlive().size());
                break;
            case ACTIVE:
                int ping = PlayerUtils.getPing(self.getPlayer());
                lines.add("&6Duration: &f" + TimeUtil.get(new Date(), getStarted()));
                lines.add(" ");

                lines.add("&6Your Ping: &f" + ping + " ms");
                lines.add("&6Players Left: &f" + this.getAlive().size() + "/" + this.getParticipants().size());
                break;
            case ENDED:
                lines.add("&6&lYou win!");
                lines.add("&6Duration: &f&n" + TimeUtil.get(getEnded(), getStarted()));
                break;
        }
        return lines;
    }

    @Override
    public List<String> getSpectatorScoreboard(GameProfile profile) {
        List<String> lines = new ArrayList<>();

        lines.add("&6Players:");

        if(getAlive().size() < 7) {
            for (GameParticipant participant : getParticipants().values()) {
                if (participant.isAlive()) {
                    lines.add(" &f" + participant.getName() + " &c" + Math.round(participant.getPlayer().getHealth()) + " ❤");
                }
            }
        } else {
            lines.add("&6Players Left: &f" + this.getAlive().size() + "/" + this.getParticipants().size());
        }

        lines.add(" ");

        switch(getState()) {
            case STARTING:
                lines.add("&6Kit: &f" + getKit().getGameKit().getDisplayName());
                lines.add("&6Arena: &f" + getArena().getDisplayName());
                break;
            case ACTIVE:
                lines.add("&6Duration: &f" + TimeUtil.get(new Date(), getStarted()));
                break;
            case ENDED:
                lines.add("&6Duration: &f&n" + TimeUtil.get(getEnded(), getStarted()));
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

        Map<String, ArenaPosition> positions = arena.getPositions();
        ArenaPosition spawn = positions.get("center");

        if(spawn != null) {

            this.setState(State.STARTING);

            StringBuilder stringBuilder = new StringBuilder();
            List<Player> players = new ArrayList<>(this.getAlivePlayers());
            int s = 0;
            while(s != this.getAlive().size()) {
                Player p = players.get(0);
                stringBuilder.append(ChatColor.WHITE + p.getName());

                players.remove(p);
                s++;
                if(s == this.getAlivePlayers().size()) {
                    stringBuilder.append(ChatColor.GRAY + ".");
                } else {
                    stringBuilder.append(ChatColor.GRAY + ", ");
                }
            }

            for(Player p : this.getAllPlayers()) {
                p.sendMessage(" ");
                p.sendMessage(Colors.get("&6&lMatch starting in 5 seconds."));
                p.sendMessage(Colors.get(" &7● &6Mode: &fFree for All"));
                p.sendMessage(Colors.get(" &7● &6Kit: &f" + getKit().getGameKit().getDisplayName()));
                p.sendMessage(Colors.get(" &7● &6Map: &f" + Colors.get(getArena().getDisplayName())));
                p.sendMessage(Colors.get(" &7● &6Participants: &f" + stringBuilder));
                p.sendMessage(" ");
            }

            for (Map.Entry<UUID, GameParticipant> entry : this.getParticipants().entrySet()) {
                GameParticipant participant = entry.getValue();
                Player p = Bukkit.getPlayer(entry.getKey());

                if (p != null) {
                    p.teleport(spawn.getLocation());

                    participant.getProfile().givePlayerItems();
                }
            }

            getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();

            Bukkit.getScheduler().runTaskLater(getPlugin(), new TeleportFix(this), 1);
            startingTimer(5);

        } else {
            for(Player p : getAlivePlayers()) {
                GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(p.getUniqueId());
                p.sendMessage(ChatColor.RED + "The arena " + arena.getName() + " does not have valid spawn points, please notify a staff member.");
                profile.setGame(null);
                profile.playerUpdate(true);
            }
        }
    }

    @Override
    public void end() {
        this.setEnded(new Date());
        this.setState(State.ENDED);

        if(getStarted() == null) {
            setStarted(new Date());
            getStartingTimer().cancel();
        }

        for(Party party : this.getParties()) {
            party.setGame(null);
        }

        for(GameParticipant participant : getAlive().values()) {
            Player player = participant.getPlayer();
            PostGameInventory pgi = new PostGameInventory(UUID.randomUUID(), participant, player.getInventory().getContents(), player.getInventory().getArmorContents());
            participant.setPostGameInventory(pgi);
            getPlugin().getGameManager().getPostGameInventories().put(pgi.getUuid(), pgi);
        }

        GameParticipant winnerParticipant = null;

        for(Map.Entry<UUID, GameParticipant> entry : this.getParticipants().entrySet()) {
            GameParticipant participant = entry.getValue();
            Player player = Bukkit.getPlayer(entry.getKey());

            boolean alive = participant.isAlive();
            if(alive) {
                winnerParticipant = participant;
                break;
            }
        }

        List<TextComponent> components = new ArrayList<>();
        for(GameParticipant p : this.getParticipants().values()) {
            TextComponent text = new TextComponent(ChatColor.WHITE + p.getName());
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/postgameinventory " + p.getPostGameInventory().getUuid().toString()));
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to view " + ChatColor.WHITE + p.getName() + "'s " + ChatColor.GREEN + "inventory.").create()));
            components.add(text);
        }

        // Match Summary Message
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" ");
        stringBuilder.append(Colors.get("&6&lMatch ended."));
        stringBuilder.append(Colors.get("\n &7● &6Winner: &f" + winnerParticipant.getName()));

        TextComponent text = new TextComponent(ChatColor.GRAY + " ● " + ChatColor.GOLD + "Inventories: ");
        final int size = components.size();
        int x = 0;
        while(x != size) {
            TextComponent t = components.get(0);
            text.addExtra(t);
            components.remove(t);
            x++;
            if (x == size) {
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
        return "FFA";
    }
}
