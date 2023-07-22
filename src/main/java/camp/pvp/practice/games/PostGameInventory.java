package camp.pvp.practice.games;

import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.StandardGui;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.UUID;

@Getter @Setter
public class PostGameInventory extends GameInventory {

    private final GameParticipant gameParticipant;
    private final UUID uuid;
    private final ItemStack[] items, armor;
    private final StandardGui gui;

    public PostGameInventory(UUID uuid, GameParticipant gameParticipant, ItemStack[] items, ItemStack[] armor) {
        this.uuid = uuid;
        this.gameParticipant = gameParticipant;
        this.items = items;
        this.armor = armor;

        this.gui = new StandardGui(gameParticipant.getName() + "'s Inventory", 54);

        boolean pots = false, soups = false;
        int potCount = 0, soupsCount = 0;

        Potion healPotion = new Potion(PotionType.INSTANT_HEAL, 2);
        healPotion.setSplash(true);

        for(int x = 0; x < 36; x++) {
            ItemStack item = items[x];
            if(item != null && !item.getType().equals(Material.AIR)) {
                GuiButton button = new GuiButton(item);
                if (x < 9) {
                    button.setSlot(x + 27);
                } else {
                    button.setSlot(x - 9);
                }

                gui.addButton(button, false);

                if (item.isSimilar(healPotion.toItemStack(1))) {
                    pots = true;
                    potCount++;
                } else {
                    switch(item.getType()) {
                        case MUSHROOM_SOUP:
                            soups = true;
                            soupsCount++;
                            break;
                    }
                }
            }
        }

        for(int x = 0; x < 4; x++) {
            if(armor[x] != null && !armor[x].getType().equals(Material.AIR)) {
                GuiButton button = new GuiButton(armor[x]);
                button.setSlot(x + 36);
                gui.addButton(button, false);
            }
        }

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwner(gameParticipant.getName());
        skull.setItemMeta(skullMeta);

        GuiButton playerButton = new GuiButton(skull, (gameParticipant.isAlive() ? "&a" : "&c") + gameParticipant.getName());
        if(gameParticipant.isAlive()) {
            playerButton.setLore(
                    "&6Health: &f" + gameParticipant.getHealth() + "/" + gameParticipant.getMaxHealth(),
                    "&6Total Hits: &f" + gameParticipant.getHits(),
                    "&6Longest Combo: &f" + gameParticipant.getLongestCombo()
            );
        } else {
            playerButton.setLore(
                    "&c&oDead.",
                    "&6Total Hits: &f" + gameParticipant.getHits(),
                    "&6Longest Combo: &f" + gameParticipant.getLongestCombo()
            );
        }

        playerButton.setSlot(49);
        gui.addButton(playerButton, false);

        if(pots || getGameParticipant().getThrownPotions() > 0) {
            GuiButton button = new GuiButton(healPotion.toItemStack(Math.max(potCount, 1)), "&c" + potCount + " pots left.");
            button.setLore(
                    "&6Thrown Potions: &f" + gameParticipant.getThrownPotions(),
                    "&6Missed Potions: &f" + gameParticipant.getMissedPotions()
                    );
            button.setSlot(45);
            gui.addButton(button, false);
        }

        if(soups) {
            GuiButton button = new GuiButton(Material.MUSHROOM_SOUP, "&a" + soupsCount + " soups left.");
            button.setAmount(Math.max(potCount, 1));
            button.setSlot(45);
            gui.addButton(button, false);
        }

    }

    public void setOpponentInventory(GameParticipant opponentParticipant, PostGameInventory postGameInventory) {
        GuiButton opponentInventory = new GuiButton(Material.MAP, "&aOpen " + opponentParticipant.getName() + "'s Inventory");
        opponentInventory.setAction((player, gui) -> postGameInventory.getGui().open(player));

        opponentInventory.setSlot(53);
        gui.addButton(opponentInventory, true);
    }
}
