package camp.pvp.practice.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.practice.Practice;
import camp.pvp.practice.loot.LootChest;
import camp.pvp.practice.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LootChestCommand {

    private Practice plugin;
    public LootChestCommand(Practice plugin) {
        this.plugin = plugin;
    }

    @Command(name = "lootchest",
            aliases = {"chest"},
            permission = "practice.commands.lootchest",
            inGameOnly = true)
    public void lootChest(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if(args.length > 0) {
            LootChest lootChest;

            try {
                lootChest = LootChest.valueOf(args[0].toUpperCase());
            } catch(IllegalArgumentException ignored) {
                StringBuilder sb = new StringBuilder();
                for(LootChest lc : LootChest.values()) {
                    sb.append(lc.name() + " ");
                }

                player.sendMessage(ChatColor.RED + "Available chests: " + sb.toString());
                return;
            }

            ItemStack item = new ItemBuilder(Material.CHEST, lootChest.getChestName()).create();
            player.getInventory().addItem(item);
        } else {
            player.sendMessage(ChatColor.RED + "Usage: /" + commandArgs.getLabel() + " <type> ");
        }
    }
}
