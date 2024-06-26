package camp.pvp.practice.commands;

import camp.pvp.practice.Practice;
import camp.pvp.practice.guis.profile.MyProfileGui;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.stats.MatchRecord;
import camp.pvp.practice.profiles.stats.ProfileStatistics;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.utils.Colors;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.paginated.PaginatedGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MatchesCommand implements CommandExecutor {

    private Practice plugin;
    public MatchesCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getCommand("matches").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) return true;

        final Player player = (Player) sender;

        String targetName = args.length > 0 ? args[0] : player.getName();

        if(args.length != 0 && !args[0].matches("^[a-zA-Z0-9_]{1,16}$")) {
            player.sendMessage(ChatColor.RED + "Invalid name.");
            return true;
        }

        CompletableFuture<GameProfile> profileFuture = plugin.getGameProfileManager().findAsync(targetName);

        profileFuture.thenAccept(profile -> {
            if(profile == null || profile.getMatchRecords().isEmpty()) {
                player.sendMessage(ChatColor.RED + "The target you specified does not have any match records.");
                return;
            }


            PaginatedGui gui = new PaginatedGui("&6Matches &7- &6" + targetName, 45);
            gui.setBorder(true);

            if(targetName.equalsIgnoreCase(sender.getName())) {
                GuiButton myProfile = new GuiButton(Material.SKULL_ITEM, "&6&lGo to My Profile");
                myProfile.setDurability((short) 3);
                SkullMeta meta = (SkullMeta) myProfile.getItemMeta();
                meta.setOwner(player.getName());
                myProfile.setItemMeta(meta);

                myProfile.setAction((p, b, g, click) -> {
                    new MyProfileGui(profile).open(p);
                });
                myProfile.setSlot(4);
                gui.addNavigationButton(myProfile);
            }

            for(MatchRecord record : profile.getMatchRecords()) {
                GameKit kit = record.getKit();
                GuiButton button = new GuiButton(record.getKit().getBaseKit().getIcon(), "&6&l" + kit.getDisplayName() + " Duel");

                boolean admin = player.hasPermission("practice.admin");

                button.setButtonUpdater(new AbstractButtonUpdater() {
                    @Override
                    public void update(GuiButton guiButton, Gui gui) {
                        List<String> lore = new ArrayList<>();
                        lore.add("&6Winner: &f" + record.getWinnerName());
                        lore.add("&6Loser: &f" + record.getLoserName());
                        lore.add(" ");
                        lore.add("&6Queue Type: &f" + record.getQueueType().toString());
                        lore.add("&6Date: &f" + record.getEnded());
                        lore.add("&6Duration: &f" + record.getMatchDuration());

                        if (record.getQueueType().equals(GameQueue.Type.RANKED)) {
                            lore.add(" ");
                            lore.add("&a" + record.getWinnerName() + " ELO: &f" + record.getWinnerElo() + " &7(+" + record.getEloChange() + ")");
                            lore.add("&c" + record.getLoserName() + " ELO: &f" + record.getLoserElo() + " &7(-" + record.getEloChange() + ")");

                            if (admin) {
                                lore.add(" ");

                                if (record.isRolledBack()) {
                                    lore.add("&c&l&oThis match was rolled back.");
                                } else {
                                    lore.add("&aClick to roll back ELO changes.");
                                }
                            }
                        }

                        guiButton.setLore(lore);
                    }
                });

                if(admin) {
                    button.setAction(new GuiAction() {
                        @Override
                        public void run(Player player, GuiButton guiButton, Gui gui, ClickType clickType) {
                            if(record.isRolledBack() || !record.getQueueType().equals(GameQueue.Type.RANKED)) return;

                            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                                CompletableFuture<GameProfile> winnerProfile = plugin.getGameProfileManager().findAsync(record.getWinner());
                                winnerProfile.thenAccept(profile -> {
                                    ProfileStatistics profileStatistics = profile.getProfileStatistics();
                                    profileStatistics.subtractElo(record.getKit(), record.getEloChange());
                                    plugin.getGameProfileManager().exportStatistics(profileStatistics, false);
                                });

                                CompletableFuture<GameProfile> loserProfile = plugin.getGameProfileManager().findAsync(record.getLoser());
                                loserProfile.thenAccept(profile -> {
                                    ProfileStatistics profileStatistics = profile.getProfileStatistics();
                                    profileStatistics.addElo(record.getKit(), record.getEloChange());
                                    plugin.getGameProfileManager().exportStatistics(profileStatistics, false);
                                });

                                record.setRolledBack(true);
                                plugin.getGameProfileManager().exportMatchRecord(record);

                                player.sendMessage(Colors.get("&aSuccessfully rolled back ELO changes for match between &f" + record.getWinnerName() + " &aand &f" + record.getLoserName() + "&a."));

                                gui.updateGui();
                            });
                        }
                    });
                }


                gui.addButton(button);
            }

            if(player.isOnline()) {
                gui.open(player);
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
        return true;
    }
}
