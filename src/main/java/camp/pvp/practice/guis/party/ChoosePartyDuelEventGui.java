package camp.pvp.practice.guis.party;

import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.parties.PartyGameRequest;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ChoosePartyDuelEventGui extends StandardGui {
    public ChoosePartyDuelEventGui(GameProfile profile, Party party) {
        super("&6&lChoose a Gamemode", 27);

        this.setDefaultBackground();

        GuiButton teamFight = new GuiButton(Material.IRON_SWORD, "&6&lClassic Team Fight");
        teamFight.setLore(
                "&7Classic Team vs Team fights",
                "&7using a kit of your choosing.",
                " ",
                "&aNext, choose a kit for this duel.");
        teamFight.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
                StandardGui kitGui = new StandardGui("&6&lChoose a Kit", 9);

                int x = 0;
                for(DuelKit kit : DuelKit.values()) {
                    if(kit.isQueueable() && kit.is2v2()) {
                        GuiButton kitButton = new GuiButton(kit.getIcon(), kit.getColor() + kit.getDisplayName());
                        kitButton.setCloseOnClick(true);

                        kitButton.setLore("&7Click to invite &6" + party.getLeader().getName() + "'s Party", "&7to a " + kit.getColor() + kit.getDisplayName() + " &7team fight.");
                        kitButton.setAction(new GuiAction() {
                            @Override
                            public void run(Player player, Gui gui) {
                                if(party.getGame() == null && profile.getParty().getGame() == null) {
                                    PartyGameRequest pgr = new PartyGameRequest(profile.getParty(), party, PartyGameRequest.Type.TEAMS);
                                    pgr.setKit(kit);
                                    pgr.send();
                                } else {
                                    player.sendMessage(ChatColor.RED + "You cannot invite this party to a game right now.");
                                }
                            }
                        });

                        kitButton.setSlot(x);
                        kitGui.addButton(kitButton, false);
                        x++;
                    }
                }

                kitGui.open(player);
            }
        });

        teamFight.setSlot(11);
        this.addButton(teamFight, false);

        GuiButton hcfFight = new GuiButton(Material.FENCE, "&6&lHCF Team Fight");
        hcfFight.setCloseOnClick(true);
        hcfFight.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
                if(party.getGame() == null && profile.getParty().getGame() == null) {
                    PartyGameRequest pgr = new PartyGameRequest(profile.getParty(), party, PartyGameRequest.Type.HCF);
                    pgr.send();
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot invite this party to a game right now.");
                }
            }
        });

        hcfFight.setLore(
                "&7Team vs Team HCF fights",
                "&7using classic HCF kits to",
                "&7defeat another party.",
                " ",
                "&aClick to invite.");
        hcfFight.setSlot(15);
        this.addButton(hcfFight, false);

    }
}
