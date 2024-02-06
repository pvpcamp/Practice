package camp.pvp.practice.commands;

import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

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
                if(!Arrays.asList(GameProfile.State.LOBBY, GameProfile.State.LOBBY_TOURNAMENT, GameProfile.State.LOBBY_PARTY, GameProfile.State.SPECTATING).contains(profile.getState())) {
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

                    if(game.equals(profile.getGame())) {
                        player.sendMessage(ChatColor.RED + "You are already spectating this game.");
                        return true;
                    }

                    if(label.equalsIgnoreCase("staffspectate") && player.hasPermission("practice.staff")) {
                        if(!profile.isStaffMode()) {
                            profile.setStaffMode(true);
                            player.sendMessage(ChatColor.GREEN + "You have been set to staff mode to spectate this game.");
                        }
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
