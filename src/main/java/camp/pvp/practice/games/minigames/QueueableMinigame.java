package camp.pvp.practice.games.minigames;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.profiles.GameProfile;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;


public abstract class QueueableMinigame extends Game {

    @Getter @Setter private Type type;
    @Getter @Setter private GameParticipant winner;
    @Getter @Setter private Party party;

    protected QueueableMinigame(Practice plugin, UUID uuid) {
        super(plugin, uuid);
    }

    @Override
    public List<String> getScoreboard(GameProfile profile) {
        return null;
    }

    @Override
    public List<String> getSpectatorScoreboard(GameProfile profile) {
        return null;
    }

    public abstract GameParticipant determineWinner();

    @Override
    public String getScoreboardTitle() {
        return "&fMinigame";
    }

    public enum Type {
        SKYWARS, ONE_IN_THE_CHAMBER;

        @Override
        public String toString() {
            switch(this) {
                case ONE_IN_THE_CHAMBER -> {
                    return "OITC";
                }
                default -> {
                    String name = this.name();
                    name = name.replace("_", " ");
                    return WordUtils.capitalizeFully(name);
                }
            }
        }

        public Material getMaterial() {
            switch(this) {
                case SKYWARS -> {
                    return Material.EYE_OF_ENDER;
                }
                case ONE_IN_THE_CHAMBER -> {
                    return Material.BOW;
                }
            }

            return null;
        }

        public List<String> getDescription() {
            switch(this) {
                case SKYWARS -> {
                    return List.of(
                            "&74 Player FFA Skywars.",
                            "&7No respawns, last player",
                            "&7alive wins.");
                }
                case ONE_IN_THE_CHAMBER -> {
                    return List.of(
                            "&7COD-Style One in the Chamber.",
                            "&7One shot, one kill.",
                            "&7First player that gets",
                            "&7to 15 kills wins.");
                }
            }

            return null;
        }

        public int getMinPlayers() {
            switch(this) {
                case SKYWARS -> {
                    return 4;
                }
                case ONE_IN_THE_CHAMBER -> {
                    return 2;
                }
            }

            return 0;
        }

        public int getMaxPlayers() {
            switch(this) {
                case SKYWARS -> {
                    return 4;
                }
                case ONE_IN_THE_CHAMBER -> {
                    return 8;
                }
            }

            return 0;
        }
    }
}
