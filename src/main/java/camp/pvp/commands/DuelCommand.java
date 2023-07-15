package camp.pvp.commands;

import camp.pvp.Practice;
import camp.pvp.kits.DuelKit;
import camp.pvp.profiles.DuelRequest;
import camp.pvp.profiles.GameProfile;
import camp.pvp.profiles.GameProfileManager;
import camp.pvp.utils.Colors;
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

                if(profile.getState().equals(GameProfile.State.LOBBY)) {

                    Player target = Bukkit.getPlayer(args[0]);

                    if(target != null && target != player) {

                        GameProfile targetProfile = gpm.getLoadedProfiles().get(target.getUniqueId());

                        if(targetProfile.getState().equals(GameProfile.State.LOBBY)) {
                            StandardGui requestGui = new StandardGui("Duel " + target.getName(), 9);

                            int x = 0;
                            for(DuelKit duelKit : DuelKit.values()) {
                                if(duelKit.isQueueable()) {
                                    ItemStack item = duelKit.getIcon();
                                    GuiButton button = new GuiButton(item, duelKit.getColor() + duelKit.getDisplayName());

                                    button.setCloseOnClick(true);
                                    button.setLore(
                                            "&7Click to duel &6" + targetProfile.getName() + "&7!"
                                    );

                                    button.setAction((pl, igui) -> {
                                        GameProfile gp = gpm.getLoadedProfiles().get(target.getUniqueId());
                                        if(gp != null) {
                                            DuelRequest oldRequest = gp.getDuelRequests().get(pl.getUniqueId());
                                            if(gp.getState().equals(GameProfile.State.LOBBY)) {
                                                if(oldRequest != null && oldRequest.getKit().equals(duelKit) && !oldRequest.isExpired()) {
                                                    player.sendMessage(ChatColor.RED + "You already sent this player a duel request recently for this same kit.");
                                                    return;
                                                }

                                                DuelRequest duelRequest = new DuelRequest(pl.getUniqueId(), target.getUniqueId(), duelKit, null, 30);
                                                duelRequest.send();
                                                gp.getDuelRequests().put(pl.getUniqueId(), duelRequest);
                                            } else {
                                                player.sendMessage(ChatColor.RED + "You cannot send a duel to this player right now as they are busy.");
                                            }
                                        } else {
                                            pl.sendMessage(ChatColor.RED + "The player you specified is not on this server.");
                                        }
                                    });

                                    button.setSlot(x);
                                    requestGui.addButton(button, false);
                                    x++;
                                }
                            }

                            DuelRequest duelRequest = profile.getDuelRequests().get(target.getUniqueId());

                            if(duelRequest != null && !duelRequest.isExpired()) {
                                StandardGui acceptGui = new StandardGui("Accept Duel from " + target.getName() + "?", 27);

                                DuelKit kit = duelRequest.getKit();
                                GuiButton acceptButton = new GuiButton(kit.getIcon(), kit.getColor() + kit.getDisplayName() + " Duel Request");
                                acceptButton.setLore(
                                        "&7Would you like to accept this duel?",
                                        "&6Arena: &f" + (duelRequest.getArena() == null ? "Random" : Colors.get(duelRequest.getArena().getDisplayName())));
                                acceptButton.setAction(new GuiAction() {
                                    @Override
                                    public void run(Player player, Gui gui) {
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
                                    public void run(Player player, Gui gui) {
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
                    player.sendMessage(ChatColor.RED + "You cannot duel someone when you are in a game.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /duel <player>");
            }
        }

        return true;
    }
}
