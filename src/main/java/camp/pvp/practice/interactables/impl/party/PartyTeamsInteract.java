package camp.pvp.practice.interactables.impl.party;

import camp.pvp.practice.games.GameTeam;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.parties.PartyMember;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class PartyTeamsInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        Party party = gameProfile.getParty();
        StandardGui gui = new StandardGui("Choose Teams", 54);

        for(PartyMember member : party.getMembers().values()) {
            ItemStack skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta meta = (SkullMeta) skullItem.getItemMeta();
            meta.setOwner(member.getName());
            skullItem.setItemMeta(meta);

            GuiButton button = new GuiButton(skullItem, "&6" + member.getName());
            button.setButtonUpdater(new AbstractButtonUpdater() {
                @Override
                public void update(GuiButton guiButton, Gui gui) {
                    GameTeam.Color color = member.getTeamColor();
                    guiButton.setLore(
                            (color.equals(GameTeam.Color.BLUE) ? color.getChatColor() : "&8") + " ● Blue" ,
                            (color.equals(GameTeam.Color.RED) ? color.getChatColor() : "&8") + " ● Red"
                    );
                }
            });

            button.setAction(new GuiAction() {
                @Override
                public void run(Player player, Gui gui) {
                    member.setTeamColor(member.getTeamColor().equals(GameTeam.Color.BLUE) ? GameTeam.Color.RED : GameTeam.Color.BLUE);

                    int start = member.getTeamColor().equals(GameTeam.Color.BLUE) ? 1 : 28;
                    int slot = 0;
                    List<PartyMember> members = new ArrayList<>(party.getMembers().values());
                    for(int x = 0; x < party.getMembers().size(); x++) {
                        if(members.get(x).getUuid().equals(member.getUuid())) {
                            slot = start + x;
                            break;
                        }
                    }

                    button.setSlot(slot);

                    gui.updateGui();
                }
            });

            int start = member.getTeamColor().equals(GameTeam.Color.BLUE) ? 1 : 28;
            int slot = 0;
            List<PartyMember> members = new ArrayList<>(party.getMembers().values());
            for(int x = 0; x < party.getMembers().size(); x++) {
                if(members.get(x).getUuid().equals(member.getUuid())) {
                    slot = start + x;
                    break;
                }
            }

            button.setSlot(slot);

            gui.addButton(button, false);
        }

        GuiButton blueTeam = new GuiButton(Material.STAINED_GLASS_PANE, "&9Blue Team");
        blueTeam.setDurability((short) 11);
        blueTeam.setSlot(0);
        gui.addButton(blueTeam, false);

        GuiButton redTeam = new GuiButton(Material.STAINED_GLASS_PANE, "&cRed Team");
        redTeam.setDurability((short) 14);
        redTeam.setSlot(27);
        gui.addButton(redTeam, false);

        gui.open(player);
    }
}
