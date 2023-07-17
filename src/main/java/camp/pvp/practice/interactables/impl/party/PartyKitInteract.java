package camp.pvp.practice.interactables.impl.party;

import camp.pvp.practice.parties.Party;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.kits.HCFKit;
import camp.pvp.practice.parties.PartyMember;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PartyKitInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        Party party = gameProfile.getParty();
        PartyMember member = party.getMembers().get(player.getUniqueId());
        if(party.getLeader().getUuid().equals(player.getUniqueId())) {
            StandardGui gui = new StandardGui("Choose Player Kits", 54);

            int x = 0;
            for(PartyMember m : party.getMembers().values()) {
                ItemStack skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta meta = (SkullMeta) skullItem.getItemMeta();
                meta.setOwner(m.getName());
                skullItem.setItemMeta(meta);

                GuiButton button = new GuiButton(skullItem, "&6" + m.getName());
                button.setAction(new GuiAction() {
                    @Override
                    public void run(Player player, Gui gui) {
                        if(m.getHcfKit().ordinal() == HCFKit.ARCHER.ordinal()) {
                            m.setHcfKit(HCFKit.DIAMOND);
                        } else {
                            m.setHcfKit(HCFKit.values()[m.getHcfKit().ordinal() + 1]);
                        }

                        gui.updateGui();
                    }
                });

                button.setButtonUpdater(new AbstractButtonUpdater() {
                    @Override
                    public void update(GuiButton guiButton, Gui gui) {
                        HCFKit kit = m.getHcfKit();
                        guiButton.setLore(
                                (kit.equals(HCFKit.DIAMOND) ? kit.getColor() : "&8") + " ● Diamond" ,
                                (kit.equals(HCFKit.BARD) ? kit.getColor() : "&8") + " ● Bard",
                                (kit.equals(HCFKit.ARCHER) ? kit.getColor() : "&8") + " ● Archer"
                        );
                    }
                });
                button.setSlot(x);
                gui.addButton(button, false);
                x++;
            }
            gui.open(player);
        } else {
            if(party.isChooseKits()) {
                StandardGui gui = new StandardGui("Choose Your Kit", 9);
                GuiButton button = new GuiButton(Material.DIAMOND_SWORD, "&6Choose Your HCF Kit");
                button.setAction(new GuiAction() {
                    @Override
                    public void run(Player player, Gui gui) {
                        if(member.getHcfKit().ordinal() == HCFKit.ARCHER.ordinal()) {
                            member.setHcfKit(HCFKit.DIAMOND);
                        } else {
                            member.setHcfKit(HCFKit.values()[member.getHcfKit().ordinal() + 1]);
                        }

                        gui.updateGui();
                    }
                });

                button.setButtonUpdater(new AbstractButtonUpdater() {
                    @Override
                    public void update(GuiButton guiButton, Gui gui) {
                        HCFKit kit = member.getHcfKit();
                        guiButton.setLore(
                                "&7Which HCF kit would you like",
                                "&7applied for HCF team fights?",
                                (kit.equals(HCFKit.DIAMOND) ? kit.getColor() : "&8") + " ● Diamond" ,
                                (kit.equals(HCFKit.BARD) ? kit.getColor() : "&8") + " ● Bard",
                                (kit.equals(HCFKit.ARCHER) ? kit.getColor() : "&8") + " ● Archer"
                        );
                    }
                });
                button.setSlot(4);
                gui.addButton(button, false);
                gui.open(player);
            } else {
                player.sendMessage(ChatColor.RED + "Since you are not leader of the party, you cannot choose your kit.");
            }
        }
    }
}
