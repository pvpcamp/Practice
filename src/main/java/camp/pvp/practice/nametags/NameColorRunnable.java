package camp.pvp.practice.nametags;

import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.impl.teams.TeamGame;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.GameParticipant;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NameColorRunnable implements Runnable{

    private Practice plugin;
    private List<String> teams;
    public NameColorRunnable(Practice plugin) {
        this.plugin = plugin;
        this.teams = Arrays.asList("playing", "enemies", "blue", "red", "spectators", "lobby", "party", "tournament");
    }

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            if (profile != null && player.isOnline()) {
                Scoreboard scoreboard = player.getScoreboard();
                for (Team team : scoreboard.getTeams()) {
                    if (teams.contains(team.getName())) {
                        List<String> entries = new ArrayList<>(team.getEntries());
                        for (String s : entries) {
                            team.removeEntry(s);
                        }
                    }
                }

                Team blueTeam = scoreboard.getTeam("blue");
                Team redTeam = scoreboard.getTeam("red");
                Team taggedBlueTeam = scoreboard.getTeam("blue_tagged");
                Team taggedRedTeam = scoreboard.getTeam("red_tagged");
                Team spectatorTeam = scoreboard.getTeam("spectators");
                Team partyTeam = scoreboard.getTeam("party");
                Team tournamentTeam = scoreboard.getTeam("tournament");

                boolean showHealth = false;
                if(profile.getGame() != null) {
                    Game game = profile.getGame();
                    GameKit kit = game.getKit();

                    if(kit.showHealthBar() && game.getCurrentPlaying().containsKey(player.getUniqueId())) {
                        showHealth = true;
                    }
                }

                Objective objective = scoreboard.getObjective(DisplaySlot.BELOW_NAME);
                if(showHealth) {
                    if(objective == null) {
                        objective = scoreboard.registerNewObjective(Colors.get("&c❤"), "health");
                        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
                    }
                } else {
                    if(objective != null) objective.unregister();
                }

                if (blueTeam == null) {
                    blueTeam = scoreboard.registerNewTeam("blue");
                    blueTeam.setPrefix(Colors.get("&9"));
                }

                if (redTeam == null) {
                    redTeam = scoreboard.registerNewTeam("red");
                    redTeam.setPrefix(Colors.get("&c"));
                }

                if (taggedBlueTeam == null) {
                    taggedBlueTeam = scoreboard.registerNewTeam("blue_tagged");
                    taggedBlueTeam.setPrefix(Colors.get("&a&l** &9"));
                }

                if (taggedRedTeam == null) {
                    taggedRedTeam = scoreboard.registerNewTeam("red_tagged");
                    taggedRedTeam.setPrefix(Colors.get("&a&l** &c"));
                }

                if (spectatorTeam == null) {
                    spectatorTeam = scoreboard.registerNewTeam("spectators");
                    spectatorTeam.setPrefix(Colors.get("&7&o"));
                    spectatorTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);
                    spectatorTeam.setCanSeeFriendlyInvisibles(true);
                }

                if (partyTeam == null) {
                    partyTeam = scoreboard.registerNewTeam("party");
                    partyTeam.setPrefix(Colors.get("&b[Party] &f"));
                }

                if (tournamentTeam == null) {
                    tournamentTeam = scoreboard.registerNewTeam("tournament");
                    tournamentTeam.setPrefix(Colors.get("&6&l* &r&6"));
                }

                switch(profile.getState()) {
                    case SPECTATING:
                    case IN_GAME:
                        if (profile.getGame() instanceof TeamGame) {
                            TeamGame teamGame = (TeamGame) profile.getGame();
                            for (GameParticipant p : teamGame.getBlue().getAliveParticipants().values()) {
                                if(p.isArcherTagged()) {
                                    taggedBlueTeam.addEntry(p.getName());
                                } else {
                                    blueTeam.addEntry(p.getName());
                                }
                            }

                            for (GameParticipant p : teamGame.getRed().getAliveParticipants().values()) {
                                if(p.isArcherTagged()) {
                                    taggedRedTeam.addEntry(p.getName());
                                } else {
                                    redTeam.addEntry(p.getName());
                                }
                            }
                        }

                        for (Player p : profile.getGame().getSpectatorsPlayers()) {
                            spectatorTeam.addEntry(p.getName());
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
