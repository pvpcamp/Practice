package camp.pvp.commands;

import camp.pvp.Practice;
import camp.pvp.games.tournaments.Tournament;
import camp.pvp.profiles.GameProfile;
import camp.pvp.utils.Colors;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class TournamentCommand implements CommandExecutor {

    private Practice plugin;
    public TournamentCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("tournament").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

            Tournament tournament = plugin.getGameManager().getTournament();
            if(args.length > 0) {
                switch(args[0].toLowerCase()) {
                    case "join":
                        if(tournament != null && !tournament.getState().equals(Tournament.State.ENDED)) {
                            if (profile.getState().equals(GameProfile.State.LOBBY)) {
                                tournament.join(player);
                            } else {
                                player.sendMessage(ChatColor.RED + "You cannot join a tournament right now.");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "There is not a tournament active at this time.");
                        }

                        return true;
                    case "leave":
                        if(profile.getTournament() != null) {
                            profile.getTournament().leave(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "You are not in a tournament right now.");
                        }
                        return true;
                    case "status":
                        if(tournament != null && !tournament.getState().equals(Tournament.State.ENDED)) {
                            Tournament.State state = tournament.getState();
                            StringBuilder sb = new StringBuilder();
                            sb.append("&6&lTournament Status");
                            sb.append("\n&6State: &f" + state.toString());
                            switch(tournament.getState()) {
                                case NEXT_ROUND_STARTING:
                                    sb.append("\n&6Round: &f" + tournament.getCurrentRound());
                                case STARTING:
                                    sb.append("\n&6Players Left: &f" + tournament.getAlive().size());
                                    break;
                                case IN_GAME:
                                    sb.append("\n&6Round: &f" + tournament.getCurrentRound());
                                    sb.append("\n&6Players Left: &f" + tournament.getAlive().size());
                                    sb.append("\n&6Active Games: &f" + tournament.getActiveGames().size());
                                    break;
                            }

                            player.sendMessage(Colors.get(sb.toString()));
                        } else {
                            player.sendMessage(ChatColor.RED + "There is not a tournament active at this time.");
                        }
                        return true;
                }
            }

            StringBuilder help = new StringBuilder();
            help.append("&6&l/tournament &r&6Help");
            help.append("\n&6/tournament join &7- &fJoin the tournament.");
            help.append("\n&6/tournament leave &7- &fLeave the tournament.");
            help.append("\n&6/tournament status &7- &fView the current tournament status.");

            if(player.hasPermission("practice.events.host.tournament")) {
                help.append("\n&6/tournament host &7- &fHost a tournament.");
            }

            player.sendMessage(Colors.get(help.toString()));
        }


        return true;
    }
}
