package camp.pvp.practice.commands;

import camp.pvp.practice.listeners.citizens.NPCClickable;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.Practice;
import camp.pvp.utils.buttons.GuiButton;
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

import java.util.Date;

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
                        profile.playerUpdate(true);
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
                    case "setnpcid":
                        if(args.length > 2) {
                            NPCClickable clickable;
                            try {
                                clickable = NPCClickable.valueOf(args[1].toUpperCase());
                            } catch (IllegalArgumentException ignored) {
                                player.sendMessage(ChatColor.RED + "Invalid clickable type. Valid options: UNRANKED, RANKED, HOST_EVENT, STATISTICS, LEADERBOARDS");
                                return true;
                            }

                            int npcId = 0;

                            try {
                                npcId = Integer.parseInt(args[2]);
                            } catch (NumberFormatException ignored) {
                                player.sendMessage(ChatColor.RED + "Invalid ID.");
                                return true;
                            }

                            plugin.getConfig().set("npc_ids." + clickable.name().toLowerCase(), npcId);
                            player.sendMessage(Colors.get("&aNPC id &f" + npcId + "&a is now assigned to &f" + clickable.name().toLowerCase() + "&a."));
                            return true;
                        }
                        break;
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
                    case "schedulereboot":
                        plugin.getServerRebootTask().setRebootTime(new Date());
                        player.sendMessage(ChatColor.GREEN + "You have scheduled a server reboot.");
                        return true;
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("&6&l/practiceutil &r&6Help");
            sb.append("\n&6/practiceutil setlobby &7- &fSets the lobby location.");
            sb.append("\n&6/practiceutil setkiteditor &7- &fSets the kit editor location.");
            sb.append("\n&6/practiceutil reset &7- &fResets your player.");
            sb.append("\n&6/practiceutil staffmodules &7- &fGives you the Lunar Client staff modules.");
            sb.append("\n&6/practiceutil setnpcid <clickable type> <npc id> &7- &fAssign a clickable type to an NPC.");
            sb.append("\n&6/practiceutil shutdown &7- &fShutdown the server immediately.");
            sb.append("\n&6/practiceutil schedulereboot &7- &fSchedule the daily server restart for right now.");

            player.sendMessage(Colors.get(sb.toString()));
        }

        return true;
    }
}
