package camp.pvp.practice.commands;

import camp.pvp.practice.guis.games.duel.DuelRequestKitSelectionGui;
import camp.pvp.practice.profiles.DuelRequest;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

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

                if(profile.getState().equals(GameProfile.State.LOBBY)) {

                    Player target = Bukkit.getPlayer(args[0]);

                    if(target != null && target != player) {

                        GameProfile targetProfile = gpm.getLoadedProfiles().get(target.getUniqueId());

                        if(targetProfile.getState().equals(GameProfile.State.LOBBY)) {
                            DuelRequest newRequest = new DuelRequest(profile, targetProfile);
                            StandardGui requestGui = new DuelRequestKitSelectionGui(plugin, newRequest);

                            DuelRequest duelRequest = profile.getDuelRequests().get(target.getUniqueId());

                            if(duelRequest != null && !duelRequest.isExpired()) {
                                StandardGui acceptGui = new StandardGui("Accept Duel from " + target.getName() + "?", 27);

                                DuelKit kit = duelRequest.getKit();
                                GuiButton acceptButton = new GuiButton(kit.getIcon(), "&6" + kit.getDisplayName() + " Duel Request");
                                acceptButton.setLore(
                                        "&7Would you like to accept this duel?",
                                        "&6Arena: &f" + (duelRequest.getArena() == null ? "Random" : Colors.get(duelRequest.getArena().getDisplayName())));
                                acceptButton.setAction(new GuiAction() {
                                    @Override
                                    public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                                        if(!duelRequest.isExpired() && profile.getState().equals(GameProfile.State.LOBBY) && targetProfile.getState().equals(GameProfile.State.LOBBY)) {
                                            duelRequest.startGame();
                                        } else {
                                            player.closeInventory();
                                            player.sendMessage(ChatColor.RED + "This duel has expired.");
                                        }
                                    }
                                });

                                acceptButton.setSlot(11);
                                acceptGui.addButton(acceptButton, false);

                                GuiButton duelGuiButton = new GuiButton(Material.GOLD_SWORD, "&6Send New Duel Request");
                                duelGuiButton.setAction(new GuiAction() {
                                    @Override
                                    public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                                        requestGui.open(player);
                                    }
                                });

                                duelGuiButton.setSlot(15);
                                acceptGui.addButton(duelGuiButton, false);
                                acceptGui.open(player);
                            } else {
                                requestGui.open(player);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You cannot send a duel to this player right now as they are busy.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "The player you specified is not on this server.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot duel someone right now.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /duel <player>");
            }
        }

        return true;
    }
}
