package camp.pvp.commands;

import camp.pvp.Practice;
import camp.pvp.parties.Party;
import camp.pvp.parties.PartyMember;
import camp.pvp.profiles.GameProfile;
import camp.pvp.utils.Colors;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PartyCommand implements CommandExecutor {

    private Practice plugin;
    public PartyCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("party").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            Party party = profile.getParty();

            if(args.length > 0) {
                switch(args.length) {
                    case 1:
                        if(party == null) {
                            player.sendMessage(ChatColor.RED + "You are not in a party.");
                            return true;
                        }

                        switch(args[0].toLowerCase()) {
                            case "info":
                                player.sendMessage(Colors.get(info(party).toString()));
                                return true;
                            case "settings":
                                if(player.getUniqueId().equals(party.getLeader().getUuid())) {
                                    StandardGui gui = new StandardGui("Party Settings", 27);
                                    gui.setDefaultBackground();

                                    GuiButton openPartyButton = new GuiButton(Material.NETHER_STAR, "&6Open/Closed Party");
                                    openPartyButton.setButtonUpdater(new AbstractButtonUpdater() {
                                        @Override
                                        public void update(GuiButton guiButton, Gui gui) {
                                            guiButton.setLore(
                                                    "&7Should the party be open to",
                                                    "&7the public or invite only?",
                                                    "&6Current Setting: &f" + (party.isOpen() ? "Public" : "Invite Only")
                                            );
                                        }
                                    });

                                    openPartyButton.setAction(new GuiAction() {
                                        @Override
                                        public void run(Player player, Gui gui) {
                                            party.setOpen(!party.isOpen());
                                            gui.updateGui();
                                        }
                                    });

                                    openPartyButton.setSlot(11);
                                    gui.addButton(openPartyButton, false);

                                    GuiButton playerKitsButton = new GuiButton(Material.DIAMOND_SWORD, "&6Member Customizable HCF Kits");
                                    playerKitsButton.setButtonUpdater(new AbstractButtonUpdater() {
                                        @Override
                                        public void update(GuiButton guiButton, Gui gui) {
                                            guiButton.setLore(
                                                    "&7Should party members have the",
                                                    "&7ability to customize their HCF kit?",
                                                    "&6Current Setting: &f" + (party.isChooseKits() ? "Enabled" : "Disabled")
                                            );
                                        }
                                    });

                                    playerKitsButton.setAction(new GuiAction() {
                                        @Override
                                        public void run(Player player, Gui gui) {
                                            party.setChooseKits(!party.isChooseKits());
                                            gui.updateGui();
                                        }
                                    });

                                    playerKitsButton.setSlot(15);
                                    gui.addButton(playerKitsButton, false);

                                    gui.open(player);
                                } else {
                                    player.sendMessage(ChatColor.RED + "You cannot do this unless you are the party leader.");
                                }
                                return true;
                            case "leave":
                                party.leave(player);
                                return true;
                        }
                    case 2:
                }
            }

            StringBuilder help = new StringBuilder();
            help.append("&6&l/party &r&6Help");
            help.append("\n&6/party info &7- &fGeneral party information.");
            help.append("\n&6/party leave &7- &fLeaves the party.");
            help.append("\n&6/party settings &7- &fOpens party settings.");
            help.append("\n&6/party leader <name> &7- &fAssigns a new party leader.");
            help.append("\n&6/party invite <name> &7- &fInvites a new player to your party.");
            help.append("\n&6/party join <name> &7- &fJoin a player's party.");

            player.sendMessage(Colors.get(help.toString()));
        }

        return true;
    }

    public StringBuilder info(Party party) {
        StringBuilder sb = new StringBuilder();
        sb.append("&6&lParty &7(" + party.getMembers().size() + ")");
        sb.append("\n&6Leader: &f" + party.getLeader().getName());
        sb.append("\n&6Members: &f");

        List<PartyMember> members = new ArrayList<>(party.getMembers().values());
        for(int x = 0; x < members.size(); x++) {
            sb.append(members.get(x).getName());

            if(x + 1 == party.getMembers().size()) {
                sb.append("&7.");
            } else {
                sb.append("&7, &f");
            }
        }

        return sb;
    }
}
