package camp.pvp.practice.guis.party;

import camp.pvp.practice.kits.BaseKit;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.parties.PartyGameRequest;
import camp.pvp.practice.parties.PartyMember;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.paginated.PaginatedGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class FindPartyGui extends PaginatedGui {

    public FindPartyGui(GameProfile profile, Party party) {
        super("Choose a Party", 36);

        for(Party p : party.getPlugin().getPartyManager().getParties()) {
            if(p != party) {
                ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                skullMeta.setOwner(p.getLeader().getName());
                skull.setItemMeta(skullMeta);

                GuiButton partyButton = new GuiButton(skull, "&6" + p.getLeader().getName() + "'s Party");

                List<String> lines = new ArrayList<>();

                if(p.getGame() == null) {
                    lines.add("&eAvailable");
                } else {
                    lines.add("&cUnavailable");
                }

                lines.add(" ");
                lines.add("&6Members &7(" + p.getMembers().size() + "&7):");

                int i = 0;
                for(PartyMember member : p.getMembers().values()) {
                    if(i < 9) {
                        lines.add(" &7● &f" + member.getName());
                    } else {
                        lines.add(" &7...");
                        break;
                    }
                    i++;
                }

                partyButton.setAction(new GuiAction() {
                    @Override
                    public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                        if(p.getGame() == null && party.getGame() == null) {
                            ArrangedGui kitGui = new ArrangedGui("&6Choose a Kit");

                            kitGui.setDefaultBorder();

                            for(GameKit kit : GameKit.values()) {
                                BaseKit baseKit = kit.getBaseKit();
                                if(baseKit.getGameTypes().contains(GameQueue.GameType.DUEL) && baseKit.isTeams()) {
                                    GuiButton kitButton = new GuiButton(baseKit.getIcon(), "&6" + kit.getDisplayName());
                                    kitButton.setCloseOnClick(true);

                                    kitButton.setLore("&7Click to invite &6" + party.getLeader().getName() + "'s Party", "&7to a &f" + kit.getDisplayName() + " &7team fight.");
                                    kitButton.setAction(new GuiAction() {
                                        @Override
                                        public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                                            if(party.getGame() == null && profile.getParty().getGame() == null) {
                                                PartyGameRequest pgr = new PartyGameRequest(profile.getParty(), party);
                                                pgr.setKit(kit);
                                                pgr.send();
                                            } else {
                                                player.sendMessage(ChatColor.RED + "You cannot invite this party to a game right now.");
                                            }
                                        }
                                    });

                                    kitGui.addButton(kitButton);
                                }
                            }

                            kitGui.open(player);
                            new ChoosePartyDuelEventGui(profile, p).open(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "You cannot send a game invite to this party right now.");
                        }
                    }
                });

                partyButton.setLore(lines);
                this.addButton(partyButton, false);
            }
        }
    }
}
