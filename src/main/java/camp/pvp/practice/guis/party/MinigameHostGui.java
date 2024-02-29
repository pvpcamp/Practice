package camp.pvp.practice.guis.party;

import camp.pvp.core.utils.Colors;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.minigames.OneInTheChamberMinigame;
import camp.pvp.practice.games.minigames.Minigame;
import camp.pvp.practice.games.minigames.SkywarsMinigame;
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

        final Minigame.Type skywarsType = Minigame.Type.SKYWARS;
        GuiButton skywars = new GuiButton(skywarsType.getMaterial(), "&6&l" + skywarsType.toString());

        List<String> skywarsLore = new ArrayList<>(skywarsType.getDescription());
        skywarsLore.add(" ");
        skywarsLore.add("&6Min/Max Players: &f" + skywarsType.getMinPlayers() + "&7/&f" + skywarsType.getMaxPlayers());
        skywarsLore.add(" ");
        skywarsLore.add("&7Click to host this minigame.");
        skywars.setLore(skywarsLore);

        skywars.setAction((player, button, gui, clickType) -> {
            if(party.getMembers().size() < skywarsType.getMinPlayers()) {
                player.sendMessage(Colors.get("&cYou need at least " + skywarsType.getMinPlayers() + " players in your party to host this minigame."));
                return;
            }

            if(party.getMembers().size() > skywarsType.getMaxPlayers()) {
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

            SkywarsMinigame minigame = new SkywarsMinigame(Practice.getInstance(), UUID.randomUUID());
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

        addButton(skywars);

        final Minigame.Type oitcType = Minigame.Type.ONE_IN_THE_CHAMBER;
        GuiButton oitc = new GuiButton(oitcType.getMaterial(), "&6&l" + oitcType.toString());

        List<String> oitcLore = new ArrayList<>(oitcType.getDescription());
        oitcLore.add(" ");
        oitcLore.add("&6Min/Max Players: &f" + oitcType.getMinPlayers() + "&7/&f" + oitcType.getMaxPlayers());
        oitcLore.add(" ");
        oitcLore.add("&7Click to host this minigame.");
        oitc.setLore(oitcLore);

        oitc.setAction((player, button, gui, clickType) -> {
            if(party.getMembers().size() < oitcType.getMinPlayers()) {
                player.sendMessage(Colors.get("&cYou need at least " + oitcType.getMinPlayers() + " players in your party to host this minigame."));
                return;
            }

            if(party.getMembers().size() > oitcType.getMaxPlayers()) {
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

            OneInTheChamberMinigame minigame = new OneInTheChamberMinigame(Practice.getInstance(), UUID.randomUUID());
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

        addButton(oitc);
    }
}
