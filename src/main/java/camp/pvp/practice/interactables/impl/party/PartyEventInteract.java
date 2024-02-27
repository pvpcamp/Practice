package camp.pvp.practice.interactables.impl.party;

import camp.pvp.practice.guis.party.FFAKitGui;
import camp.pvp.practice.guis.party.FindPartyGui;
import camp.pvp.practice.guis.party.MinigameHostGui;
import camp.pvp.practice.guis.party.SplitKitGui;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class PartyEventInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        Party party = gameProfile.getParty();
        if(party.getLeader().getUuid().equals(player.getUniqueId())) {
            StandardGui gui = new StandardGui("&6Choose an Event", 9);

            GuiButton ffaEvent = new GuiButton(Material.GOLD_AXE, "&6&lFree For All");
            ffaEvent.setAction(new GuiAction() {
                @Override
                public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                    StandardGui kitGui = new FFAKitGui(gameProfile, party);

                    kitGui.open(player);
                }
            });

            ffaEvent.setSlot(1);
            gui.addButton(ffaEvent, false);

            GuiButton splitEvent = new GuiButton(Material.IRON_SWORD, "&6&lSplit Teams");
            splitEvent.setAction(new GuiAction() {
                @Override
                public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                    StandardGui kitGui = new SplitKitGui(gameProfile, party);

                    kitGui.open(player);
                }
            });

            splitEvent.setSlot(3);
            gui.addButton(splitEvent, false);

            GuiButton hostMinigame = new GuiButton(Material.BOW, "&6&lHost Minigame");
            hostMinigame.setButtonUpdater((button, g) -> {
                if(player.hasPermission("practice.parties.host.minigame")) {
                    button.setLore("&7Click to host a private minigame.");
                } else {
                    button.setLore(
                            "&7This feature is only",
                            "&7available to players with",
                            "&6Voyager Rank&7.",
                            " ",
                            "&7Purchase at &astore.pvp.camp");
                }
            });

            hostMinigame.setAction(new GuiAction() {
                @Override
                public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                    if(player.hasPermission("practice.parties.host.minigame")) {
                        new MinigameHostGui(party).open(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have permission to host a minigame.");
                    }
                }
            });

            hostMinigame.setSlot(5);
            gui.addButton(hostMinigame);

            GuiButton duelOtherParties = new GuiButton(Material.NAME_TAG, "&6&lDuel Other Parties");
            duelOtherParties.setAction(new GuiAction() {
                @Override
                public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                    new FindPartyGui(gameProfile, party).open(player);
                }
            });
            duelOtherParties.setSlot(7);
            gui.addButton(duelOtherParties, false);

            gui.open(player);
        } else {
            player.sendMessage(ChatColor.RED + "You cannot host events since you are not the party leader.");
        }
    }
}
