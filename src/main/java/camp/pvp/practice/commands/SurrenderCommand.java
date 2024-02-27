package camp.pvp.practice.commands;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SurrenderCommand implements CommandExecutor {

    private Practice plugin;
    public SurrenderCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("surrender").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player player)) return true;

        GameProfile profile = plugin.getGameProfileManager().getLoadedProfile(player.getUniqueId());

        if(!profile.getState().equals(GameProfile.State.IN_GAME)) {
            player.sendMessage("You are not in a game.");
            return true;
        }

        Game game = profile.getGame();
        game.leave(player);

        profile.setGame(null);
        profile.playerUpdate(true);

        return true;
    }
}
