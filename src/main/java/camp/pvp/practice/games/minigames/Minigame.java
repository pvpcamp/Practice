package camp.pvp.practice.games.minigames;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.minigames.tag.TNTTagMinigame;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.profiles.GameProfile;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.*;


public abstract class Minigame extends Game {

    @Getter @Setter private Type type;
    @Getter @Setter private GameParticipant winner;
    @Getter @Setter private Party party;

    protected Minigame(Practice plugin, UUID uuid) {
        super(plugin, uuid);
    }

    public abstract GameParticipant determineWinner();

    @Override
    public String getScoreboardTitle() {
        return getType().getScoreboardTitle();
    }

    @Override
    public void end() {
        if(getState() == State.ENDED) return;

        determineWinner();

        if(getWinner() != null) {
            sendLunarWinTitle(getWinner().getPlayer());
        }

        setEnded(new Date());
        setState(State.ENDED);

        if(getStarted() == null) {
            setStarted(new Date());
            getStartingTimer().cancel();
        }

        for(GameParticipant p : getCurrentPlaying().values()) {
            if(p.getRespawnTask() != null) p.getRespawnTask().cancel();
            p.setLivingState(GameParticipant.LivingState.ALIVE);
        }

        sendEndingMessage();

        cleanup(3);
    }

    @Override
    public void sendStartingMessage() {
        List<GameParticipant> sortedParticipants = new ArrayList<>(this.getAlive().values());
        sortedParticipants.sort(Comparator.comparing(GameParticipant::getName));

        StringBuilder sb = new StringBuilder();

        int s = 0;
        while(s != this.getAlive().size()) {
            GameParticipant participant = sortedParticipants.get(0);
            sb.append(ChatColor.WHITE + participant.getName());

            sortedParticipants.remove(participant);
            s++;
            if(s == this.getAlive().size()) {
                sb.append(ChatColor.GRAY + ".");
            } else {
                sb.append(ChatColor.GRAY + ", ");
            }
        }

        String startingMessage = """
                
                &6&lMinigame starting in 5 seconds.
                 &7● &6Minigame: &f%s
                 &7● &6Arena: &f%s
                 &7● &6Participants: &f%s
                 \n
                 """.formatted(getType().toString(), getArena().getDisplayName(), sb);

        this.announce(startingMessage);
    }

    @Override
    public void sendEndingMessage() {
        StringBuilder topPlayers = new StringBuilder();
        List<GameParticipant> sortedParticipants = new ArrayList<>(this.getParticipants().values());
        sortedParticipants.sort((p1, p2) -> Integer.compare(p2.getKills(), p1.getKills()));

        for(int i = 0; i < Math.min(sortedParticipants.size(), 3); i++) {
            GameParticipant p = sortedParticipants.get(i);
            topPlayers.append("\n &7● &6" + (i + 1) + ": &f" + p.getName() + " &7- &f" + p.getKills() + " Kill" + (p.getKills() == 1 ? "" : "s"));
        }

        String endMessage = """
                
                &6&lMinigame finished.
                 &7● &6Winner: &f%s
                 \n
                &6&lTop Players: %s
                 \n
                """.formatted(winner.getName(), topPlayers.toString());
        announce(endMessage);
    }

    public enum Type {
        FIREBALL_BLITZ, SKYWARS, ONE_IN_THE_CHAMBER, TNT_TAG;

        @Override
        public String toString() {
            switch(this) {
                case ONE_IN_THE_CHAMBER -> {
                    return "OITC";
                }
                case TNT_TAG -> {
                    return "TNT Tag";
                }
                default -> {
                    String name = this.name();
                    name = name.replace("_", " ");
                    return WordUtils.capitalizeFully(name);
                }
            }
        }

        public String getScoreboardTitle() {
            switch(this) {
                case FIREBALL_BLITZ:
                    return "Blitz";
                case ONE_IN_THE_CHAMBER:
                    return "OITC";
                case TNT_TAG:
                    return "TNT Tag";
                default:
                    String name = this.name();
                    name = name.replace("_", " ");
                    return WordUtils.capitalizeFully(name);
            }
        }

        public Material getMaterial() {
            switch(this) {
                case FIREBALL_BLITZ -> {
                    return Material.FIREBALL;
                }
                case SKYWARS -> {
                    return Material.EYE_OF_ENDER;
                }
                case ONE_IN_THE_CHAMBER -> {
                    return Material.BOW;
                }
                case TNT_TAG -> {
                    return Material.TNT;
                }
            }

            return null;
        }

        public List<String> getDescription() {
            switch(this) {
                case FIREBALL_BLITZ -> {
                    return List.of(
                            "&7Fireball Fight, but crazier.",
                            "&7Be the last player standing",
                            "&7to win.");
                }
                case SKYWARS -> {
                    return List.of(
                            "&7FFA Skywars.",
                            "&7Later player alive wins.");
                }
                case ONE_IN_THE_CHAMBER -> {
                    return List.of(
                            "&7COD-Style One in the Chamber.",
                            "&7One shot, one kill.",
                            "&7First player that gets",
                            "&7to 15 kills wins.");
                }
                case TNT_TAG -> {
                    return List.of(
                            "&7If you are tagged when the",
                            "&7timer runs out, you explode.",
                            "&7Later player alive wins.");
                }
            }

            return null;
        }

        public int getMinPlayers() {
            return 2;
        }

        public int getMaxPlayers() {
            switch(this) {
                case TNT_TAG -> { return 20; }
                case ONE_IN_THE_CHAMBER -> { return 8; }
                default -> { return 4; }
            }
        }

        public Minigame createGame(Practice plugin, UUID uuid) {
            switch(this) {
                case FIREBALL_BLITZ -> {
                    return new FireballBlitzMinigame(plugin, uuid);
                }
                case SKYWARS -> {
                    return new SkywarsMinigame(plugin, uuid);
                }
                case ONE_IN_THE_CHAMBER -> {
                    return new OneInTheChamberMinigame(plugin, uuid);
                }
                case TNT_TAG -> {
                    return new TNTTagMinigame(plugin, uuid);
                }
            }

            return null;
        }
    }
}
