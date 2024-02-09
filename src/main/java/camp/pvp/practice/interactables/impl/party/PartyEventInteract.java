package camp.pvp.practice.interactables.impl.party;

import camp.pvp.practice.guis.party.FFAKitGui;
import camp.pvp.practice.guis.party.FindPartyGui;
import camp.pvp.practice.guis.party.SplitKitGui;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.impl.FreeForAll;
import camp.pvp.practice.games.impl.teams.TeamDuel;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.parties.PartyMember;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PartyEventInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        Party party = gameProfile.getParty();
        if(party.getLeader().getUuid().equals(player.getUniqueId())) {
            StandardGui gui = new StandardGui("Choose an Event", 9);

            GuiButton ffaEvent = new GuiButton(Material.GOLD_AXE, "&6&lFree For All");
            ffaEvent.setAction(new GuiAction() {
                @Override
                public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                    StandardGui kitGui = new FFAKitGui(gameProfile, party);

                    kitGui.open(player);
                }
            });

            ffaEvent.setSlot(2);
            gui.addButton(ffaEvent, false);

            GuiButton duelOtherParties = new GuiButton(Material.NAME_TAG, "&6&lDuel Other Parties");
            duelOtherParties.setAction(new GuiAction() {
                @Override
                public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                    new FindPartyGui(gameProfile, party).open(player);
                }
            });
            duelOtherParties.setSlot(4);
            gui.addButton(duelOtherParties, false);

            GuiButton splitEvent = new GuiButton(Material.IRON_SWORD, "&6&lSplit Teams");
            splitEvent.setAction(new GuiAction() {
                @Override
                public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                    StandardGui kitGui = new SplitKitGui(gameProfile, party);

                    kitGui.open(player);
                }
            });

            splitEvent.setSlot(6);
            gui.addButton(splitEvent, false);

            gui.open(player);
        } else {
            player.sendMessage(ChatColor.RED + "You cannot host events since you are not the party leader.");
        }
    }
}
