package camp.pvp.practice.guis.party;

import camp.pvp.core.utils.Colors;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.minigames.Minigame;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.parties.PartyMember;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.paginated.PaginatedGui;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MinigameHostGui extends PaginatedGui {

    private Party party;

    public MinigameHostGui(Party party) {
        super("&6Host a Private Minigame", 27);
        this.party = party;

        setBorder(true);
        refreshButtons();
    }

    public void refreshButtons() {
        getButtons().clear();

        for(Minigame.Type type : Minigame.Type.values()) {
            GuiButton button = new GuiButton(type.getMaterial(), "&6&l" + type.toString());

            List<String> lore = new ArrayList<>(type.getDescription());
            lore.add(" ");
            lore.add("&6Min/Max Players: &f" + type.getMinPlayers() + "&7/&f" + type.getMaxPlayers());
            lore.add(" ");
            lore.add("&7Click to host this minigame.");
            button.setLore(lore);

            button.setAction((player, button1, gui, clickType) -> {
                if(party.getMembers().size() < type.getMinPlayers()) {
                    player.sendMessage(Colors.get("&cYou need at least " + type.getMinPlayers() + " players in your party to host this minigame."));
                    return;
                }

                if(party.getMembers().size() > type.getMaxPlayers()) {
                    player.sendMessage(Colors.get("&cYou have too many players in your party to host this minigame."));
                    return;
                }

                List<PartyMember> members = new ArrayList<>(), kickedMembers = new ArrayList<>();
                GameProfileManager gpm = Practice.getInstance().getGameProfileManager();
                for (PartyMember member : party.getMembers().values()) {
                    if (gpm.getLoadedProfiles().get(member.getUuid()).getGame() == null) {
                        members.add(member);
                    } else {
                        kickedMembers.add(member);
                    }
                }

                Minigame minigame = type.createGame(Practice.getInstance(), UUID.randomUUID());
                assert minigame != null;

                minigame.setParty(party);

                for (PartyMember member : kickedMembers) {
                    Player p = member.getPlayer();
                    p.sendMessage(ChatColor.RED + "You have been kicked from the party since you were not able to play in the event.");
                    party.leave(member.getPlayer());
                }

                for (PartyMember member : members) {
                    minigame.join(member.getPlayer());
                }

                minigame.initialize();

                player.closeInventory();
            });

            addButton(button);
        }
    }
}
