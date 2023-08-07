package camp.pvp.practice.games.impl.teams;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.practice.games.GameInventory;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.GameTeam;
import camp.pvp.practice.games.impl.teams.TeamDuel;
import camp.pvp.practice.games.tasks.StartingTask;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.parties.PartyMember;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.PlayerUtils;
import camp.pvp.practice.utils.TimeUtil;
import org.apache.commons.lang.time.DateUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class HCFTeams extends TeamDuel {

    public HCFTeams(Practice plugin, UUID uuid) {
        super(plugin, uuid);
    }

    @Override
    public void start() {
        List<Arena> list = new ArrayList<>();
        if(getArena() == null) {
            for(Arena a : getPlugin().getArenaManager().getArenas()) {
                if(a.isEnabled()) {
                    if(a.getType().equals(Arena.Type.HCF_TEAMFIGHT)) {
                        list.add(a);
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
                profile.playerUpdate(true);
            }
            return;
        }

        this.setKit(DuelKit.NO_DEBUFF);

        for(Party party : this.getParties()) {
            party.setGame(this);
        }

        this.setState(State.STARTING);

        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        sb.append("\n&6&lTeam duel starting in 5 seconds.");
        sb.append("\n &7● &6Map: &f" + arena.getDisplayName());

        ArenaPosition blueSpawn = arena.getPositions().get("spawn1");
        List<GameParticipant> blueParticipants = new ArrayList<>(getBlue().getParticipants().values());
        sb.append("\n &7● &9Blue Team: &f");
        int blueCount = 0;
        while (blueCount != getBlue().getParticipants().size()) {
            GameParticipant participant = blueParticipants.get(0);
            GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(participant.getUuid());
            Player player = participant.getPlayer();
            sb.append(ChatColor.WHITE + participant.getName());

            blueParticipants.remove(participant);
            blueCount++;
            if (blueCount == getBlue().getParticipants().size()) {
                sb.append("&7.");
            } else {
                sb.append("&7, ");
            }

            player.teleport(blueSpawn.getLocation());

            participant.setKitApplied(true);
            participant.getAppliedHcfKit().apply(player);
        }

        ArenaPosition redSpawn = arena.getPositions().get("spawn2");
        List<GameParticipant> redParticipants = new ArrayList<>(getRed().getParticipants().values());
        sb.append("\n &7● &cRed Team: &f");
        int redCount = 0;
        while (redCount != getRed().getParticipants().size()) {
            GameParticipant participant = redParticipants.get(0);
            GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(participant.getUuid());
            Player player = participant.getPlayer();
            sb.append(ChatColor.WHITE + participant.getName());

            redParticipants.remove(participant);
            redCount++;
            if (redCount == getRed().getParticipants().size()) {
                sb.append("&7.");
            } else {
                sb.append("&7, ");
            }

            player.teleport(redSpawn.getLocation());

            participant.setKitApplied(true);
            participant.getAppliedHcfKit().apply(player);
        }

        sb.append("\n ");

        this.announce(sb.toString());

        getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();

        startLcTeammateUpdater();

        this.startingTimer = new StartingTask(this, 5).runTaskTimer(this.getPlugin(), 20, 20);
    }

    @Override
    public void handleHit(Player victim, Player attacker, EntityDamageByEntityEvent event) {
        super.handleHit(victim, attacker, event);
        event.setDamage(event.getDamage() * 0.75D);
    }

    @Override
    public List<String> getScoreboard(GameProfile profile) {
        List<String> lines = new ArrayList<>();

        GameParticipant self = getAlive().get(profile.getUuid());

        GameTeam friendlyTeam = self.getTeam();
        GameTeam enemyTeam = friendlyTeam.getColor().equals(GameTeam.Color.BLUE) ? getBlue() : getRed();

        boolean showInGame = profile.isSidebarInGame(),
                showCps = profile.isSidebarShowCps(),
                showDuration = profile.isSidebarShowDuration(),
                showPing = profile.isSidebarShowPing();

        switch(getState()) {
            case STARTING:
                lines.add("&6Arena: &f" + arena.getDisplayName());
                lines.add("");
                lines.add("&9Blue Team " + (friendlyTeam.getColor().equals(GameTeam.Color.BLUE) ? "(You)" : "") + ": &f" + getBlue().getAliveParticipants().size() + "/" + getBlue().getParticipants().size());
                lines.add("&cRed Team " + (friendlyTeam.getColor().equals(GameTeam.Color.RED) ? "(You)" : "") + ": &f" + getRed().getAliveParticipants().size() + "/" + getRed().getParticipants().size());
                break;
            case ACTIVE:
                if(!showInGame) {
                    return null;
                }

                int ping = 0;
                ping = PlayerUtils.getPing(self.getPlayer());

                boolean addSpace = false;
                if(showDuration) {
                    addSpace = true;
                    lines.add("&6Duration: &f" + TimeUtil.get(new Date(), getStarted()));
                }

                if(showPing) {
                    addSpace = true;
                    lines.add("&6Your Ping: &f" + ping + " ms");
                }

                if(showCps) {
                    addSpace = true;
                    lines.add("&6Your CPS: &f" + profile.getCps());
                }

                if(self.getAppliedHcfKit() != null) {
                    switch(self.getAppliedHcfKit()) {
                        case BARD:
                        case ARCHER:
                            lines.add("&6Energy: &f" + self.getEnergy());
                            addSpace = true;
                    }
                }

                if(self.isArcherTagged()) {
                    addSpace = true;
                    lines.add("&cArcher Tag: &f" + TimeUnit.MILLISECONDS.toSeconds(self.getLastArcherTag().getTime() - new Date().getTime()));
                }

                if(addSpace) {
                    lines.add("");
                }

                lines.add(friendlyTeam.getColor().getChatColor() + "Alive Teammates:");

                int i = 0;
                for(GameParticipant participant : friendlyTeam.getAliveParticipants().values()) {
                    if(i < 5) {
                        Player player = participant.getPlayer();
                        lines.add((participant.isArcherTagged() ? " &c&o" : " &f") + participant.getName() + " &c" + Math.round(player.getHealth()) + " ❤");
                    } else {
                        lines.add("&f...");
                    }

                    i++;
                }

                lines.add(" ");


                lines.add("&9Blue Team " + (friendlyTeam.getColor().equals(GameTeam.Color.BLUE) ? "(You)" : "") + ": &f" + getBlue().getAliveParticipants().size() + "/" + getBlue().getParticipants().size());
                lines.add("&cRed Team " + (friendlyTeam.getColor().equals(GameTeam.Color.RED) ? "(You)" : "") + ": &f" + getRed().getAliveParticipants().size() + "/" + getRed().getParticipants().size());
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

        switch(getState()) {
            case STARTING:
                lines.add("&6Kit: &f" + kit.getDisplayName());
                lines.add("&6Arena: &f" + arena.getDisplayName());
                lines.add(" ");
                lines.add("&9Blue Team: &f" + getBlue().getAliveParticipants().size() + "/" + getBlue().getParticipants().size());
                lines.add("&cRed Team: &f" + getRed().getAliveParticipants().size() + "/" + getRed().getParticipants().size());
                break;
            case ACTIVE:
                lines.add("&6Duration: &f" + TimeUtil.get(new Date(), getStarted()));
                lines.add(" ");
                lines.add("&9Blue Team: &f" + getBlue().getAliveParticipants().size() + "/" + getBlue().getParticipants().size());
                lines.add("&cRed Team: &f" + getRed().getAliveParticipants().size() + "/" + getRed().getParticipants().size());

                break;
            case ENDED:
                lines.add("&6Duration: &f&n" + TimeUtil.get(getEnded(), getStarted()));
                lines.add(" ");
                lines.add("&9Blue Team: &f" + getBlue().getAliveParticipants().size() + "/" + getBlue().getParticipants().size());
                lines.add("&cRed Team: &f" + getRed().getAliveParticipants().size() + "/" + getRed().getParticipants().size());

                break;
        }

        return lines;
    }
}
