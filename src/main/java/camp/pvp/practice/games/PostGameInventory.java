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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class PostGameInventory extends GameInventory {

    private final GameParticipant gameParticipant;
    private final UUID uuid;
    private final ItemStack[] items, armor;
    private final StandardGui gui;
    private final long created;

    public PostGameInventory(UUID uuid, GameParticipant gameParticipant, ItemStack[] items, ItemStack[] armor) {
        this.uuid = uuid;
        this.gameParticipant = gameParticipant;
        this.items = items;
        this.armor = armor;
        this.created = System.currentTimeMillis();

        this.gui = new StandardGui("&6" + gameParticipant.getName() + "'s Inventory", 54);

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

        for(int x = 3; x > -1; x--) {
            if(armor[x] != null && !armor[x].getType().equals(Material.AIR)) {
                GuiButton button = new GuiButton(armor[x]);
                button.setSlot(39 - x);
                gui.addButton(button);
            }
        }

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwner(gameParticipant.getName());
        skull.setItemMeta(skullMeta);

        GuiButton playerButton = new GuiButton(skull, (gameParticipant.isAlive() ? "&a&l" : "&c&l&o") + gameParticipant.getName());
        if(gameParticipant.isAlive()) {
            playerButton.setLore(
                    "&6Health: &f" + (((double) gameParticipant.getHealth()) / 2) + "/" + ((double) gameParticipant.getMaxHealth() / 2) + " &c&l❤",
                    "&6Hunger: &f" + (((double) gameParticipant.getHunger()) / 2) + "/10.0 &a&l✦"
            );

            playerButton.addGlowing();
        } else {
            playerButton.setLore("&c&oDead.");
        }

        playerButton.setSlot(49);
        gui.addButton(playerButton);

        GuiButton combatStats = new GuiButton(Material.DIAMOND_SWORD, "&4&lCombat Stats");
        List<String> combatStatsLore = new ArrayList<>();

        if(gameParticipant.getHits() > 0) {
            combatStatsLore.add(" ");
            combatStatsLore.add("&6&lMelee:");
            combatStatsLore.add(" &6Hits: &f" + gameParticipant.getHits());
            combatStatsLore.add(" &6Longest Combo: &f" + gameParticipant.getLongestCombo());
            combatStatsLore.add(" &6Blocked Hits: &f" + gameParticipant.getBlockedHits());
        }

        if(gameParticipant.getArrowShots() > 0) {
            if(gameParticipant.getHits() > 0) combatStatsLore.add(" ");

            combatStatsLore.add("&6&lBow:");
            combatStatsLore.add(" &6Shots: &f" + gameParticipant.getArrowShots());
            combatStatsLore.add(" &6Hits: &f" + gameParticipant.getArrowHits());
        }

        if(gameParticipant.getFireballShots() > 0) {
            if(gameParticipant.getArrowShots() > 0 || gameParticipant.getHits() > 0) combatStatsLore.add(" ");

            combatStatsLore.add("&6&lFireballs:");
            combatStatsLore.add(" &6Shots: &f" + gameParticipant.getFireballShots());
        }

        combatStats.setLore(combatStatsLore);
        combatStats.setSlot(48);
        gui.addButton(combatStats);

        int healSlot = 45;
        if(pots || getGameParticipant().getThrownPotions() > 0) {
            GuiButton button = new GuiButton(healPotion.toItemStack(Math.max(potCount, 1)), "&c&l" + potCount + " Health Pots Left");
            button.setLore(
                    "&6Thrown Potions: &f" + gameParticipant.getThrownPotions(),
                    "&6Missed Potions: &f" + gameParticipant.getMissedPotions(),
                    "&6Accuracy: &f" + (gameParticipant.getThrownPotions() == 0 ? "N/A" : Math.round((double) (gameParticipant.getThrownPotions() - gameParticipant.getMissedPotions()) / (double) gameParticipant.getThrownPotions() * 100)) + "%"
            );
            button.setSlot(healSlot);
            gui.addButton(button);
            healSlot++;
        }

        if(soups) {
            GuiButton button = new GuiButton(Material.MUSHROOM_SOUP, "&a&l" + soupsCount + " Soup" + (soupsCount == 1 ? "" : "s") + " Left");
            button.setAmount(Math.max(potCount, 1));
            button.setSlot(healSlot);
            gui.addButton(button);
        }

    }

    public void setOpponentInventory(GameParticipant opponentParticipant, PostGameInventory postGameInventory) {
        GuiButton opponentInventory = new GuiButton(Material.EMPTY_MAP, "&aOpen " + opponentParticipant.getName() + "'s Inventory");
        opponentInventory.setAction((player, b, gui, click) -> postGameInventory.getGui().open(player));

        opponentInventory.setSlot(53);
        gui.addButton(opponentInventory);
    }
}
