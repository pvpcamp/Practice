package camp.pvp.practice.commands;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.practice.profiles.stats.MatchRecord;
import camp.pvp.practice.profiles.stats.ProfileELO;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.utils.Colors;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.paginated.PaginatedGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class MatchesCommand implements CommandExecutor {

    private Practice plugin;
    public MatchesCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getCommand("matches").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        String targetName = args.length > 0 ? args[0] : player.getName();

        if(args.length != 0 && !args[0].matches("^[a-zA-Z0-9_]{1,16}$")) {
            player.sendMessage(ChatColor.RED + "Invalid name.");
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            GameProfile profile = plugin.getGameProfileManager().find(targetName);

            if(profile == null) {
                player.sendMessage(ChatColor.RED + "No match records found for " + targetName + ".");
                return;
            }

            PaginatedGui gui = new PaginatedGui("&6Matches &7- &6" + profile.getName(), 36);
            for(MatchRecord record : profile.getMatchRecords()) {
                DuelKit kit = record.getKit();
                GuiButton button = new GuiButton(record.getKit().getIcon(), "&6&l" + kit.getDisplayName() + " Duel");

                boolean admin = player.hasPermission("practice.admin");

                button.setButtonUpdater(new AbstractButtonUpdater() {
                    @Override
                    public void update(GuiButton guiButton, Gui gui) {
                        List<String> lore = new ArrayList<>();
                        lore.add("&6Winner: &f" + record.getWinnerName());
                        lore.add("&6Loser: &f" + record.getLoserName());
                        lore.add(" ");
                        lore.add("&6Queue Type: &f" + record.getQueueType().toString());
                        lore.add(" ");
                        lore.add("&6Date: &f" + record.getEnded());
                        lore.add("&6Duration: &f" + record.getMatchDuration());

                        if(record.getQueueType().equals(GameQueue.Type.RANKED)) {
                            lore.add(" ");
                            lore.add("&a" + record.getWinnerName() + " ELO: &f" + record.getWinnerElo() + " &7(+" + record.getEloChange() + ")");
                            lore.add("&c" + record.getLoserName() + " ELO: &f" + record.getLoserElo() + " &7(-" + record.getEloChange() + ")");

                            if(admin) {
                                lore.add(" ");

                                if(record.isRolledBack()) {
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
                            if(record.isRolledBack()) return;

                            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                                GameProfile winnerProfile = plugin.getGameProfileManager().find(record.getWinner());
                                GameProfile loserProfile = plugin.getGameProfileManager().find(record.getLoser());

                                ProfileELO winnerElo = winnerProfile.getProfileElo();
                                ProfileELO loserElo = loserProfile.getProfileElo();

                                winnerElo.subtractElo(record.getKit(), record.getEloChange());
                                loserElo.addElo(record.getKit(), record.getEloChange());

                                record.setRolledBack(true);
                                plugin.getGameProfileManager().exportMatchRecord(record, true);
                                plugin.getGameProfileManager().exportElo(winnerElo, true);
                                plugin.getGameProfileManager().exportElo(loserElo, true);

                                player.sendMessage(Colors.get("&aSuccessfully rolled back ELO changes for match between &f" + record.getWinnerName() + " &aand &f" + record.getLoserName() + "&a."));

                                gui.updateGui();
                            });
                        }
                    });
                }


                gui.addButton(button);
            }

            gui.open(player);
        });

        return true;
    }
}
