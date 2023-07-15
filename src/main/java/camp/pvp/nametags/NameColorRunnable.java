package camp.pvp.nametags;

import camp.pvp.Practice;
import camp.pvp.games.GameParticipant;
import camp.pvp.games.impl.teams.TeamGame;
import camp.pvp.profiles.GameProfile;
import camp.pvp.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

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

                Team playingTeam = scoreboard.getTeam("playing");
                Team enemyTeam = scoreboard.getTeam("enemies");
                Team blueTeam = scoreboard.getTeam("blue");
                Team redTeam = scoreboard.getTeam("red");
                Team spectatorTeam = scoreboard.getTeam("spectators");
                Team lobbyTeam = scoreboard.getTeam("lobby");
                Team partyTeam = scoreboard.getTeam("party");
                Team tournamentTeam = scoreboard.getTeam("tournament");

                if (playingTeam == null) {
                    playingTeam = scoreboard.registerNewTeam("playing");
                    playingTeam.setPrefix(Colors.get("&e"));
                }

                if (enemyTeam == null) {
                    enemyTeam = scoreboard.registerNewTeam("enemies");
                    enemyTeam.setPrefix(Colors.get("&c"));
                }

                if (blueTeam == null) {
                    blueTeam = scoreboard.registerNewTeam("blue");
                    blueTeam.setPrefix(Colors.get("&9[Blue] "));
                    blueTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);
                    blueTeam.setAllowFriendlyFire(false);
                    blueTeam.setCanSeeFriendlyInvisibles(true);
                }

                if (redTeam == null) {
                    redTeam = scoreboard.registerNewTeam("red");
                    redTeam.setPrefix(Colors.get("&c[Red] "));
                    redTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);
                    redTeam.setAllowFriendlyFire(false);
                    redTeam.setCanSeeFriendlyInvisibles(true);
                }

                if (spectatorTeam == null) {
                    spectatorTeam = scoreboard.registerNewTeam("spectators");
                    spectatorTeam.setPrefix(Colors.get("&7&o"));
                    spectatorTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);
                    spectatorTeam.setCanSeeFriendlyInvisibles(true);
                }

                if (lobbyTeam == null) {
                    lobbyTeam = scoreboard.registerNewTeam("lobby");
                    lobbyTeam.setPrefix(Colors.get("&b"));
                }

                if (partyTeam == null) {
                    partyTeam = scoreboard.registerNewTeam("party");
                    partyTeam.setPrefix(Colors.get("&b[Party] &f"));
                }

                if (tournamentTeam == null) {
                    tournamentTeam = scoreboard.registerNewTeam("tournament");
                    tournamentTeam.setPrefix(Colors.get("&6&l* &r&6"));
                }

                if (profile.getGame() != null) {
                    if (profile.getGame() instanceof TeamGame) {
                        TeamGame teamGame = (TeamGame) profile.getGame();
                        for (GameParticipant p : teamGame.getBlue().getAliveParticipants().values()) {
                            blueTeam.addEntry(p.getName());
                        }

                        for (GameParticipant p : teamGame.getRed().getAliveParticipants().values()) {
                            redTeam.addEntry(p.getName());
                        }
                    } else {
                        if (plugin.getGameProfileManager().getState(profile).equals(GameProfile.State.SPECTATING)) {
                            for (Player p : profile.getGame().getAlivePlayers()) {
                                playingTeam.addEntry(p.getName());
                            }
                        } else {
                            for (Player p : profile.getGame().getAlivePlayers()) {
                                if (!p.equals(player)) {
                                    enemyTeam.addEntry(p.getName());
                                }
                            }
                        }
                    }

                    for (Player p : profile.getGame().getSpectatorsPlayers()) {
                        spectatorTeam.addEntry(p.getName());
                    }
                }
            }
        }
    }
}
