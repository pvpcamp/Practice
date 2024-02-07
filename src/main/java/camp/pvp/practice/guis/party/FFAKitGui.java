package camp.pvp.practice.guis.party;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.impl.FreeForAll;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.parties.PartyMember;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class FFAKitGui extends ArrangedGui {
    public FFAKitGui(GameProfile profile, Party party) {
        super("&6Choose a Kit");

        this.setDefaultBorder();

        for(DuelKit kit : DuelKit.values()) {
            if(!kit.isFfa()) continue;

            GuiButton button = new GuiButton(kit.getIcon(), "&6" + kit.getDisplayName());
            button.setCloseOnClick(true);
            button.setLore(
                    "&7Click to start &f" + kit.getDisplayName() + " &7FFA event!");
            button.setAction(new GuiAction() {
                @Override
                public void run(Player player, Gui gui) {
                    Practice plugin = Practice.getInstance();

                    List<PartyMember> members = new ArrayList<>(), kickedMembers = new ArrayList<>();
                    GameProfileManager gpm = plugin.getGameProfileManager();
                    for (PartyMember member : party.getMembers().values()) {
                        if (gpm.getLoadedProfiles().get(member.getUuid()).getGame() == null) {
                            members.add(member);
                        } else {
                            kickedMembers.add(member);
                        }
                    }

                    if (members.size() > 1) {
                        FreeForAll ffa = new FreeForAll(plugin, UUID.randomUUID());
                        for (PartyMember member : kickedMembers) {
                            Player p = member.getPlayer();
                            p.sendMessage(ChatColor.RED + "You have been kicked from the party since you were not able to play in the event.");
                            party.leave(member.getPlayer());
                        }

                        ffa.getParties().add(party);
                        ffa.setKit(kit);

                        for (PartyMember member : members) {
                            ffa.join(member.getPlayer());
                        }

                        ffa.start();
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have enough players in your party to participate in this event.");
                    }
                }
            });

            getButtons().add(button);
        }
    }
}
