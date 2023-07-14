package camp.pvp.commands;

import camp.pvp.Practice;
import camp.pvp.profiles.GameProfile;
import camp.pvp.utils.Colors;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import com.lunarclient.bukkitapi.LunarClientAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PracticeUtilCommand implements CommandExecutor {

    private Practice plugin;
    public PracticeUtilCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("practiceutil").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            Location location = player.getLocation();
            if (args.length != 0) {
                switch(args[0].toLowerCase()) {
                    case "setlobby":
                        location.setY(location.getY() + 1);
                        plugin.setLobbyLocation(location);
                        player.sendMessage(ChatColor.GREEN + "The lobby has been set to your current location.");
                        return true;
                    case "setkiteditor":
                        location.setY(location.getY() + 1);
                        plugin.setKitEditorLocation(location);
                        player.sendMessage(ChatColor.GREEN + "The kit editor has been set to your current location.");
                        return true;
                    case "reset":
                        profile.playerUpdate();
                        return true;
                    case "staffmodules":
                        LunarClientAPI lcApi = plugin.getLunarClientAPI();
                        if(lcApi.isRunningLunarClient(player)) {
                            lcApi.giveAllStaffModules(player);
                            player.sendMessage(ChatColor.GREEN + "You have been given the Lunar Client staff modules.");
                        } else {
                            player.sendMessage(ChatColor.RED + "You are not running Lunar Client.");
                        }
                        return true;
                    case "restart":
                    case "stop":
                    case "shutdown":
                    case "stopserver":
                        StandardGui gui = new StandardGui("Are you sure?", 27);
                        gui.setDefaultBackground();

                        GuiButton yes = new GuiButton(Material.GOLD_BLOCK, "&aYes");
                        yes.setSlot(12);
                        yes.setLore(
                                "&4&lCAUTION: &fYou could be disrupting games!",
                                "&f&lAre you sure you want to stop the server?"
                        );
                        yes.setAction((player1, gui1) -> {
                            plugin.shutdown();
                            Bukkit.getScheduler().runTaskLater(plugin, ()-> plugin.getServer().shutdown(), 20);
                        });
                        gui.addButton(yes, false);

                        GuiButton no = new GuiButton(Material.REDSTONE_BLOCK, "&cNo");
                        no.setSlot(14);
                        no.setCloseOnClick(true);
                        gui.addButton(no, false);

                        gui.open(player);
                        return true;
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("&6&l/practiceutil &r&6Help");
            sb.append("\n&6/practiceutil setlobby &7- &fSets the lobby location.");
            sb.append("\n&6/practiceutil setkiteditor &7- &fSets the kit editor location.");
            sb.append("\n&6/practiceutil reset &7- &fResets your player.");
            sb.append("\n&6/practiceutil staffmodules &7- &fGives you the Lunar Client staff modules.");
            sb.append("\n&6/practiceutil shutdown &7- &fShutdown the server.");

            player.sendMessage(Colors.get(sb.toString()));
        }

        return true;
    }
}
