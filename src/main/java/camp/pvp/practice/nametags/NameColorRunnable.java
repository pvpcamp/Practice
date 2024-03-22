package camp.pvp.practice.nametags;

import camp.pvp.core.Core;
import camp.pvp.core.api.CoreAPI;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.ranks.Rank;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.impl.teams.TeamGame;
import camp.pvp.practice.games.minigames.tag.TNTTagMinigame;
import camp.pvp.practice.games.minigames.tag.TNTTagParticipant;
import camp.pvp.practice.kits.BaseKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.GameParticipant;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class NameColorRunnable implements Runnable{

    private Practice plugin;
    public NameColorRunnable(Practice plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

            if(profile == null) continue;

            Scoreboard scoreboard = player.getScoreboard();

            Team blueTeam = scoreboard.getTeam("blue");
            Team redTeam = scoreboard.getTeam("red");
            Team taggedBlueTeam = scoreboard.getTeam("blue_tagged");
            Team taggedRedTeam = scoreboard.getTeam("red_tagged");
            Team spectatorTeam = scoreboard.getTeam("spectators");
            Team itTeam = scoreboard.getTeam("it");

            boolean showHealth = false;
            if(profile.getGame() != null) {
                Game game = profile.getGame();
                BaseKit kit = game.getKit();

                if(kit.isShowHealthBar() && game.getCurrentPlaying().containsKey(player.getUniqueId())) {
                    showHealth = true;
                }
            }

            Objective objective = scoreboard.getObjective(DisplaySlot.BELOW_NAME);
            if(showHealth) {
                if(objective == null) {
                    String name = Colors.get("&c❤");
                    objective = scoreboard.registerNewObjective(name, "health");
                    objective.setDisplaySlot(DisplaySlot.BELOW_NAME);

                    for(Player p : Bukkit.getOnlinePlayers()) {
                        objective.getScore(p.getName()).setScore((int) p.getHealth());
                    }
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

            if (itTeam == null) {
                itTeam = scoreboard.registerNewTeam("it");
                itTeam.setPrefix(Colors.get("&c&l[IT] "));
            }

            Map<Rank, Team> rankTeams = new HashMap<>();
            List<Team> allTeams = new ArrayList<>(Arrays.asList(blueTeam, redTeam, taggedBlueTeam, taggedRedTeam, spectatorTeam, itTeam));
            Map<Team, List<String>> newEntries = new HashMap<>();

            for (Rank rank : Core.getInstance().getRankManager().getRanks().values()) {
                Team team = scoreboard.getTeam(rank.getName());
                if (team == null) {
                    team = scoreboard.registerNewTeam(rank.getName());
                }

                team.setPrefix(Colors.get(rank.getColor()));
                rankTeams.put(rank, team);

                allTeams.add(team);
            }

            for (Team team : allTeams) {
                if(team == null) continue;
                newEntries.put(team, new ArrayList<>());
            }

            switch(profile.getState()) {
                case SPECTATING:
                case IN_GAME:
                    final Game game = profile.getGame();
                    if (game instanceof TeamGame) {
                        TeamGame teamGame = (TeamGame) profile.getGame();
                        for (GameParticipant p : teamGame.getBlue().getAliveParticipants().values()) {
                            if(p.isArcherTagged()) {
                                newEntries.get(taggedBlueTeam).add(p.getName());
                            } else {
                                newEntries.get(blueTeam).add(p.getName());
                            }
                        }

                        for (GameParticipant p : teamGame.getRed().getAliveParticipants().values()) {
                            if(p.isArcherTagged()) {
                                newEntries.get(taggedRedTeam).add(p.getName());
                            } else {
                                newEntries.get(redTeam).add(p.getName());
                            }
                        }
                    }

                    if(game instanceof TNTTagMinigame minigame) {
                        for (TNTTagParticipant p : minigame.getTagged()) {
                            newEntries.get(itTeam).add(p.getName());
                        }
                    }

                    for (Player p : profile.getGame().getSpectatorsPlayers()) {
                        newEntries.get(spectatorTeam).add(p.getName());
                    }
                    break;
                default:
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        CoreProfile coreProfile = Core.getApi().getLoadedProfile(p);
                        if(coreProfile != null) {
                            GameProfile gameProfile = plugin.getGameProfileManager().getLoadedProfile(p.getUniqueId());

                            if(!gameProfile.getState().isLobby()) continue;

                            newEntries.get(rankTeams.get(coreProfile.getHighestRank())).add(p.getName());
                        }
                    }
                    break;
            }

            for (Map.Entry<Team, List<String>> entry : newEntries.entrySet()) {
                Team team = entry.getKey();
                List<String> entries = entry.getValue();

                for (String s : entries) {
                    if(!team.hasEntry(s)) {
                        team.addEntry(s);
                    }
                }

                for (String s : team.getEntries()) {
                    if(!entries.contains(s)) {
                        team.removeEntry(s);
                    }
                }
            }
        }
    }
}
