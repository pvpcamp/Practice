package camp.pvp.practice.commands;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.utils.Colors;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GamesCommand implements CommandExecutor {

    private Practice plugin;
    public GamesCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("games").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            List<Game> games = plugin.getGameManager().getActiveGames();

            player.sendMessage(Colors.get("&6&lActive Games &7(" + games.size() + ")"));
            for(Game game : games) {
                if(!game.getState().equals(Game.State.INACTIVE)) {
                    TextComponent component = new TextComponent(Colors.get(
                            "&6 ● Game Type: &f" + game.getClass().getSimpleName()
                                    + "&7, &6Kit: " + game.getKit().getColor() + game.getKit().getDisplayName()
                                    + "&7, &6Arena: &f" + game.getArena().getDisplayName()
                                    + "&7, &6Alive: &f" + game.getAlive().size()
                                    + "&7, &6Spectating: &f" + game.getSpectators().size()
                    ));
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spectate " + game.getUuid().toString()));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + game.getUuid().toString()).create()));
                    player.spigot().sendMessage(component);
                }
            }
        }

        return true;
    }
}
