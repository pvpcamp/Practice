package camp.pvp.practice.guis.profile;

import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HotbarSlotGui extends StandardGui {

    public HotbarSlotGui(GameProfile profile) {
        super("&6Choose a Hotbar Slot", 18);

        for(int i = 0; i < 9; i++) {
            GuiButton hotbarSlot = new GuiButton(Material.WOOD_SWORD, "&6Slot " + (i + 1));

            final int slot = i;
            hotbarSlot.setButtonUpdater(new AbstractButtonUpdater() {
                @Override
                public void update(GuiButton guiButton, Gui gui) {
                    boolean isSlot = profile.getNoDropHotbarSlot() == slot;

                    guiButton.updateName("&6&lSlot #" + (guiButton.getSlot() + 1) + (isSlot ? " &r&7(Selected)" : ""));

                    List<String> lore = new ArrayList<>();
                    lore.add("&7When in game, you will not");
                    lore.add("&7be able to drop items in");
                    lore.add("&7hotbar slot " + (slot + 1) + ".");

                    if(profile.getNoDropHotbarSlot() == -1) {
                        lore.add(" ");
                        lore.add("&cYou currently have no drop disabled.");
                    }

                    guiButton.setLore(lore);

                    guiButton.setType(isSlot ? Material.DIAMOND_SWORD : Material.WOOD_SWORD);
                }
            });

            hotbarSlot.setAction(new GuiAction() {
                @Override
                public void run(Player player, Gui gui) {
                    if(profile.getNoDropHotbarSlot() == slot) {
                        profile.setNoDropHotbarSlot(-1);
                    } else {
                        profile.setNoDropHotbarSlot(slot);
                    }

                    gui.updateGui();
                }
            });

            hotbarSlot.setSlot(i);
            addButton(hotbarSlot, false);
        }

        GuiButton back = new GuiButton(Material.ARROW, "&cBack to Settings");
        back.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
                new SettingsGui(profile).open(player);
            }
        });
        back.setSlot(9);
        addButton(back, false);
    }
}
