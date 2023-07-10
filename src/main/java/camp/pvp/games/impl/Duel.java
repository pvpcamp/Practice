package camp.pvp.games.impl;

import camp.pvp.Practice;
import camp.pvp.arenas.Arena;
import camp.pvp.arenas.ArenaPosition;
import camp.pvp.cooldowns.PlayerCooldown;
import camp.pvp.games.Game;
import camp.pvp.games.GameInventory;
import camp.pvp.games.GameParticipant;
import camp.pvp.games.PostGameInventory;
import camp.pvp.profiles.GameProfile;
import camp.pvp.profiles.GameProfileManager;
import camp.pvp.queue.GameQueue;
import camp.pvp.utils.Colors;
import camp.pvp.utils.PlayerUtils;
import camp.pvp.utils.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Duel extends Game {

    public @Setter @Getter GameQueue.Type queueType;

    public Duel(Practice plugin, UUID uuid) {
        super(plugin, uuid);
    }

    @Override
    public void init() {

    }

    @Override
    public void start() {

        List<Arena> list = new ArrayList<>();
        if(getArena() == null) {
            for(Arena a : getPlugin().getArenaManager().getArenas()) {
                if(a.isEnabled()) {
                    if (kit.isBuild()) {
                        if (a.getType().equals(Arena.Type.DUEL_BUILD)) {
                            list.add(a);
                        }
                    } else {
                        if(a.getType().equals(Arena.Type.DUEL)) {
                            list.add(a);
                        }
                    }
                }
            }

            Collections.shuffle(list);
            this.setArena(list.get(0));
        }

        if(arena == null) {
            for(Player p : getAlivePlayers()) {
                GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(p.getUniqueId());
                p.sendMessage(ChatColor.RED + "There are no arenas currently available for the ladder selected. Please notify a staff member.");
                profile.setGame(null);
                profile.playerUpdate();
            }
            return;
        }

        Map<Player, Location> locations = new HashMap<>();

        Map<String, ArenaPosition> positions = arena.getPositions();
        ArenaPosition pos1 = positions.get("spawn1"), pos2 = positions.get("spawn2");

        if(pos1 != null && pos2 != null) {

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
                p.sendMessage(Colors.get("&6&lDuel starting in 5 seconds."));
                p.sendMessage(Colors.get(" &7● &6Mode: &f" + this.queueType.toString()));
                p.sendMessage(Colors.get(" &7● &6Map: &f" + Colors.get(getArena().getDisplayName())));
                p.sendMessage(Colors.get(" &7● &6Participants: &f" + stringBuilder));
                p.sendMessage(" ");
            }

            int position = 1;
            for (Map.Entry<UUID, GameParticipant> entry : this.getParticipants().entrySet()) {
                GameParticipant participant = entry.getValue();
                Player p = Bukkit.getPlayer(entry.getKey());
                Location location = null;
                if (position == 1) {
                    location = pos1.getLocation();
                }

                if (position == 2) {
                    location = pos2.getLocation();
                }

                if (p != null) {
                    locations.put(p, location);
                    p.teleport(locations.get(p));

                    // TODO: Custom Kits
                    this.getKit().apply(p);
                }
                position++;
            }

            getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();

            this.startingTimer = new BukkitRunnable() {
                int i = 5;
                public void run() {
                    if (i == 0) {
                        for(Player p : Duel.this.getAlivePlayers()) {
                            if(p != null) {
                                p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1, 1);
                                p.sendMessage(ChatColor.GREEN + "The game has started, good luck!");
                            }
                        }

                        Duel duel = Duel.this;
                        duel.setStarted(new Date());
                        duel.setState(State.ACTIVE);

                        this.cancel();
                    } else {
                        if (i > 0) {
                            for (Player p : Duel.this.getAllPlayers()) {
                                p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
                                p.sendMessage(ChatColor.GREEN.toString() + i + "...");
                            }
                        }

                        i -= 1;
                    }
                }
            }.runTaskTimer(this.getPlugin(), 20, 20);
        } else {
            for(Player p : getAlivePlayers()) {
                GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(p.getUniqueId());
                p.sendMessage(ChatColor.RED + "The arena " + arena.getName() + " does not have valid spawn points, please notify a staff member.");
                profile.setGame(null);
                profile.playerUpdate();
            }
        }
    }

    @Override
    public void end() {
        GameProfileManager gpm = getPlugin().getGameProfileManager();
        this.setEnded(new Date());
        this.setState(State.ENDED);

        GameParticipant winnerParticipant = null, loserParticipant = null;
        GameProfile winnerProfile = null, loserProfile = null;

        for(Map.Entry<UUID, GameParticipant> entry : this.getParticipants().entrySet()) {
            GameParticipant participant = entry.getValue();
            Player player = Bukkit.getPlayer(entry.getKey());
            PlayerInventory inventory = player.getInventory();

            PostGameInventory pgi;
            UUID uuid = UUID.randomUUID();

            boolean alive = participant.isAlive();
            if(alive) {
                winnerParticipant = participant;
                winnerProfile = gpm.getLoadedProfiles().get(entry.getKey());

                pgi = new PostGameInventory(uuid, participant, inventory.getContents(), inventory.getArmorContents());
                winnerParticipant.setPostGameInventory(pgi);
            } else {
                loserParticipant = participant;
                loserProfile = gpm.getLoadedProfiles().get(entry.getKey());

                pgi = new PostGameInventory(uuid, participant, inventory.getContents(), inventory.getArmorContents());
                loserParticipant.setPostGameInventory(pgi);
            }

            getPlugin().getGameManager().addInventory(pgi);
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

        TextComponent text = new TextComponent(ChatColor.GRAY + " ● " + ChatColor.AQUA + "Inventories: ");
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

        // TODO: Unranked and Ranked Stats + Ranked ELO
//        switch (this.getType()) {
//            case UNRANKED:
//                winnerProfile.setUnrankedWins(winnerProfile.getUnrankedWins() + 1);
//                loserProfile.setUnrankedLosses(loserProfile.getUnrankedLosses() + 1);
//
//                break;
//            case RANKED:
//                winnerProfile.setRankedWins(winnerProfile.getRankedWins() + 1);
//                loserProfile.setRankedLosses(loserProfile.getRankedLosses() + 1);
//
//                double winnerElo = winnerProfile.getKitElo(this.getKit());
//                double loserElo = loserProfile.getKitElo(this.getKit());
//
//                // before it was 10                            ||
//                double multiplier = Math.pow(1 / (1 + Math.pow(24, (winnerElo - loserElo) / 400)), 2);
//
//                double difference = 50 * multiplier;
//                int newWinnerElo = (int) Math.round(winnerElo + difference);
//                int newLoserElo = (int) Math.round(loserElo - difference);
//
//                winnerProfile.setKitElo(this.getKit(), newWinnerElo);
//                loserProfile.setKitElo(this.getKit(), newLoserElo);
//
//                stringBuilder.append(Colors.get("\n &7● &bELO Changes: &f" + winnerProfile.getName() + " &7- &f" + newWinnerElo + " ELO &7(+" + Math.round(difference) + ")" +
//                        "&7, &f" + loserProfile.getName() + " &7- &f" + newLoserElo + " ELO &7(-" + Math.round(difference) + ")"));
//                break;
//            case TOURNAMENT:
//                if(Practice.instance.getTournament() != null) {
//                    Practice.instance.getTournament().leave(Bukkit.getPlayer(loser));
//                }
//                break;
//        }

        for(Player player : this.getAllPlayers()) {
            player.sendMessage(" ");
            player.sendMessage(stringBuilder.toString());
            player.spigot().sendMessage(text);
            player.sendMessage(" ");
        }

        this.endingTimer = Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            for(Map.Entry<UUID, GameParticipant> entry : this.getParticipants().entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());
                GameParticipant participant = entry.getValue();
                GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(entry.getKey());

                if(player != null) {
                    if(entry.getValue().isAlive()) {

                        for(PlayerCooldown cooldown : participant.getCooldowns().values()) {
                            cooldown.remove();
                        }

                        profile.setGame(null);
                        profile.playerUpdate();
                    }
                }
            }
            for(UUID uuid : this.getSpectators().keySet()) {
                Player player = Bukkit.getPlayer(uuid);
                this.spectateEnd(player);
            }

            this.clearEntities();

            // TODO: Replace built blocks from build duel.

//            for(Block block : this.getPlacedBlocks()) {
//                block.setType(Material.AIR);
//            }
//
//            for(BrokenBlock block : this.getBrokenBlocks()) {
//                Block b = block.getBlock();
//                b.setType(block.getMaterial());
//                b.setData(block.getData());
//            }

//            if(this.getKit().getType().equals(Kit.Type.BUILD)) {
//                this.getArena().setInUse(false);
//            }

            this.setState(State.ENDED);
        }, 60);
    }

    @Override
    public void eliminate(Player player) {
        super.eliminate(player);
        if(this.getAlive().size() < 2) {
            this.end();
        }
    }

    @Override
    public void forceEnd() {
        this.announce("&c&lThis match has been forcefully ended by the server.");

        if(getStartingTimer() != null) {
            getStartingTimer().cancel();
        }

        if(getEndingTimer() != null) {
            getEndingTimer().cancel();
        }

        for(Map.Entry<UUID, GameParticipant> entry : this.getParticipants().entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            GameParticipant participant = entry.getValue();
            GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(entry.getKey());

            if(player != null) {
                if(entry.getValue().isAlive()) {

                    for(PlayerCooldown cooldown : participant.getCooldowns().values()) {
                        cooldown.remove();
                    }

                    profile.setGame(null);
                    profile.playerUpdate();
                }
            }
        }
        for(UUID uuid : this.getSpectators().keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            this.spectateEnd(player);
        }

        this.clearEntities();
        this.setEnded(new Date());
        this.setState(State.ENDED);
    }

    @Override
    public List<String> getScoreboard(GameProfile profile) {
        List<String> lines = new ArrayList<>();
        GameParticipant self = getAlive().get(profile.getUuid());
        GameParticipant opponent = null;
        for(GameParticipant p : getAlive().values()) {
            if(p.getUuid() != self.getUuid()) {
                opponent = p;
                break;
            }
        }

        if(opponent == null) {
            lines.add("&cError!");
            return lines;
        }

        switch(getState()) {
            case STARTING:
                lines.add("&6Kit: &f" + kit.getDisplayName());
                lines.add("&6Arena: &f" + arena.getDisplayName());
                lines.add("&6Opponent: &f" + opponent.getName());
                break;
            case ACTIVE:
                lines.add("&6Duration: &f" + TimeUtil.get(new Date(), getStarted()));
                lines.add("&6Ping: &e" + PlayerUtils.getPing(self.getPlayer()) + " ms&7/&c" + PlayerUtils.getPing(opponent.getPlayer()) + " ms");
            case ENDED:
                lines.add("&6Duration: " + TimeUtil.get(getStarted(), getEnded()));
        }
        return lines;
    }

    @Override
    public List<String> getSpectatorScoreboard(GameProfile profile) {
        return null;
    }
}
