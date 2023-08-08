package camp.pvp.practice.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.stats.ProfileELO;
import camp.pvp.practice.utils.Colors;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class EloManagerCommand {

    private Practice plugin;
    public EloManagerCommand(Practice plugin) {
        this.plugin = plugin;
    }

    @Command(name = "elomanager",
            permission = "practice.commands.elomanager",
            description = "Manage player ELO.")
    public void setElo(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        if(args.length > 0) {
            switch(args[0].toLowerCase()) {
                case "reset":
                    if(args.length > 1) {
                        GameProfile profile = plugin.getGameProfileManager().find(args[1], false);
                        if(profile == null) {
                            sender.sendMessage(ChatColor.RED + "The player you specified has never joined the network.");
                            return;
                        }

                        ProfileELO elo = new ProfileELO(profile.getUuid());
                        elo.setName(profile.getName());
                        profile.setProfileElo(elo);
                        plugin.getGameProfileManager().exportElo(profile.getProfileElo(), true);
                        sender.sendMessage(ChatColor.GREEN + "ELO has been reset for player " + ChatColor.WHITE + profile.getName() + ChatColor.GREEN + ".");
                        return;
                    }
                    break;
                case "ladder":
                    if(args.length > 3) {
                        GameProfile profile = plugin.getGameProfileManager().find(args[1], false);
                        if(profile == null) {
                            sender.sendMessage(ChatColor.RED + "The player you specified has never joined the network.");
                            return;
                        }

                        DuelKit kit = null;
                        for(DuelKit k : DuelKit.values()) {
                            if(k.name().equalsIgnoreCase(args[2])) {
                                kit = k;
                            }
                        }

                        if(kit == null) {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                            return;
                        }

                        int elo;
                        try {
                            elo = Integer.parseInt(args[3]);
                        } catch (NumberFormatException ignored) {
                            sender.sendMessage(ChatColor.RED + "Invalid ELO.");
                            return;
                        }

                        ProfileELO profileELO = profile.getProfileElo();
                        if(profileELO == null) {
                            profileELO = plugin.getGameProfileManager().importElo(profile.getUuid(), false);
                        }

                        profileELO.getRatings().put(kit, elo);
                        plugin.getGameProfileManager().exportElo(profileELO, true);
                        sender.sendMessage(ChatColor.GREEN + "ELO for player " + ChatColor.WHITE + profile.getName() + ChatColor.GREEN + " for kit " + ChatColor.WHITE + kit.getDisplayName() + ChatColor.GREEN + " has been set to " + elo + ".");
                        return;
                    }
                    break;
            }
        }

        StringBuilder help = new StringBuilder();
        help.append("&6&l/elomanager &r&6Help");
        help.append("\n&6/elomanager reset <player> &7- &fResets all ELO for player for all kits.");
        help.append("\n&6/elomanager ladder <player> <kit> <elo> &7- &fSets ELO for player for specified kit.");

        sender.sendMessage(Colors.get(help.toString()));
    }
}
