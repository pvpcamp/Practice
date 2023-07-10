package camp.pvp.commands;

import camp.pvp.Practice;
import camp.pvp.kits.DuelKit;
import camp.pvp.profiles.DuelRequest;
import camp.pvp.profiles.GameProfile;
import camp.pvp.profiles.GameProfileManager;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DuelCommand implements CommandExecutor {

    private Practice plugin;
    public DuelCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("duel").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            if(args.length > 0) {
                Player player = (Player) sender;
                GameProfileManager gpm = plugin.getGameProfileManager();
                GameProfile profile = gpm.getLoadedProfiles().get(player.getUniqueId());

                if(profile.getGame() == null) {

                    Player target = Bukkit.getPlayer(args[0]);

                    if(target != null && target != player) {

                        GameProfile targetProfile = gpm.getLoadedProfiles().get(target.getUniqueId());

                        if(targetProfile.getGame() == null) {
                            StandardGui gui = new StandardGui("Duel " + target.getName(), 9);

                            int x = 0;
                            for(DuelKit duelKit : DuelKit.values()) {
                                if(duelKit.isQueueable()) {
                                    ItemStack item = duelKit.getIcon();
                                    GuiButton button = new GuiButton(item.getType(), duelKit.getDisplayName());
                                    button.setItemMeta(item.getItemMeta());
                                    button.setData(item.getData());
                                    button.setName(duelKit.getDisplayName());

                                    button.setAction((player1, gui1) -> {
                                        GameProfile gp = gpm.getLoadedProfiles().get(target.getUniqueId());
                                        if(gp != null) {
                                            DuelRequest duelRequest = new DuelRequest(player1.getUniqueId(), target.getUniqueId(), duelKit, null, 30);
                                            duelRequest.send();
                                            gp.getDuelRequests().put(player1.getUniqueId(), duelRequest);
                                        } else {
                                            player1.sendMessage(ChatColor.RED + "The player you specified is not on this server.");
                                        }

                                        player1.closeInventory();
                                    });

                                    button.setSlot(x);
                                    gui.addButton(button, false);
                                    x++;
                                }
                            }

                            gui.open(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "The player you specified is in a game.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "The player you specified is not on this server.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot duel someone when you are in a game.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /duel <player>");
            }
        }

        return true;
    }
}
