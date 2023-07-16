package camp.pvp.commands;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import camp.pvp.profiles.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class SpectateCommand implements CommandExecutor {

    private Practice plugin;
    public SpectateCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getCommand("spectate").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            if(args.length > 0) {
                if(!profile.getState().equals(GameProfile.State.LOBBY) && !profile.getState().equals(GameProfile.State.LOBBY_TOURNAMENT)) {
                    player.sendMessage(ChatColor.RED + "You cannot spectate a game right now.");
                    return true;
                }

                UUID uuid = null;
                try {
                    uuid = UUID.fromString(args[0]);
                } catch (Exception ignored) {
                }

                Player target = Bukkit.getPlayer(args[0]);
                if(target != null) {
                    GameProfile targetProfile = plugin.getGameProfileManager().getLoadedProfiles().get(target.getUniqueId());
                    Game game = targetProfile.getGame();
                    if(game == null) {
                        player.sendMessage(ChatColor.RED + "The player you specified is not in a game.");
                        return true;
                    }

                    game.spectateStart(player, target.getLocation());
                } else if (uuid != null) {
                    Game game = plugin.getGameManager().getGames().get(uuid);
                    if(game != null && !game.getState().equals(Game.State.ENDED)) {
                        game.spectateStart(player, game.getAlivePlayers().get(0).getLocation());
                    } else {
                        player.sendMessage(ChatColor.RED + "The game ID you specified is invalid.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "The player you specified was not found.");
                }
            } else if(profile.getState().equals(GameProfile.State.LOBBY) || profile.getState().equals(GameProfile.State.LOBBY_PARTY)) {
                List<Game> games = new ArrayList<>(plugin.getGameManager().getActiveGames());
                if(!games.isEmpty()) {
                    Collections.shuffle(games);
                    Game game = games.get(0);
                    game.spectateStartRandom(player);

                    player.sendMessage(ChatColor.GREEN + "You did not specify what game you wanted to spectate, so we sent you here!");
                } else {
                    player.sendMessage(ChatColor.RED + "No active games found.");
                }
            }
        }

        return true;
    }
}
