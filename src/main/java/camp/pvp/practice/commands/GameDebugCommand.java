package camp.pvp.practice.commands;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.List;

public class GameDebugCommand implements CommandExecutor {

    private Practice plugin;
    public GameDebugCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("gamedebug").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if(profile.getGame() == null) {
            player.sendMessage(ChatColor.RED + "You are not in a game.");
            return true;
        }

        StandardGui gui = new StandardGui("&4Game Debug", 27);
        Game game = profile.getGame();

        GuiButton resetHits = new GuiButton(Material.DIAMOND_SWORD, "&6Reset Hits");
        resetHits.setAction(new GuiAction() {
            @Override
            public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                for(GameParticipant p : game.getParticipants().values()) {
                    p.setHits(0);
                }

                player.sendMessage(ChatColor.GREEN + "You have reset the hits for this game.");
            }
        });
        resetHits.setSlot(10);
        resetHits.setCloseOnClick(true);

        GuiButton refill = new GuiButton(Material.POTION, "&6Refill");
        refill.setAction(new GuiAction() {
            @Override
            public void run(Player player, GuiButton b, Gui gui, ClickType click) {
                StandardGui refillGui = new StandardGui("&6Choose an Item", 9);
                Potion health = new Potion(PotionType.INSTANT_HEAL, 2, true);
                List<ItemStack> items = Arrays.asList(new ItemStack(Material.MUSHROOM_SOUP), health.toItemStack(1));

                int x = 0;
                for(ItemStack i : items) {
                    GuiButton button = new GuiButton(i);
                    button.setAction(new GuiAction() {
                        @Override
                        public void run(Player player, GuiButton b, Gui gui, ClickType click) {
                            for(Player p : game.getCurrentPlayersPlaying()) {
                                PlayerInventory pi = p.getInventory();
                                for(int invSlot = 0; invSlot < 36; invSlot++) {
                                    if(pi.getItem(invSlot) == null || pi.getItem(invSlot).getType().equals(Material.AIR)) {
                                        pi.setItem(invSlot, i);
                                    }
                                }
                            }

                            player.sendMessage(ChatColor.GREEN + "You have refilled all currently alive player's inventories.");
                        }
                    });
                    button.setSlot(x);
                    x++;

                    refillGui.addButton(button, false);
                }

                refillGui.open(player);
            }
        });
        refill.setSlot(11);
        refill.setCloseOnClick(true);

        gui.addButton(resetHits, false);
        gui.addButton(refill, false);

        gui.open(player);

        return true;
    }
}
