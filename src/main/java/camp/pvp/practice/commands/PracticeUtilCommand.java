package camp.pvp.practice.commands;

import camp.pvp.practice.listeners.citizens.NPCClickable;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;

public class PracticeUtilCommand implements CommandExecutor {

    private Practice plugin;
    public PracticeUtilCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("practiceutil").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if (args.length != 0) {
            switch(args[0].toLowerCase()) {
                case "setlobby":
                    if(!(sender instanceof Player player)) {
                        sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
                        return true;
                    }

                    Location location = player.getLocation();

                    location.setY(location.getY() + 1);
                    plugin.setLobbyLocation(location);
                    player.sendMessage(ChatColor.GREEN + "The lobby has been set to your current location.");
                    return true;
                case "setkiteditor":
                    if(!(sender instanceof Player player)) {
                        sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
                        return true;
                    }

                    location = player.getLocation();

                    location.setY(location.getY() + 1);
                    plugin.setKitEditorLocation(location);
                    player.sendMessage(ChatColor.GREEN + "The kit editor has been set to your current location.");
                    return true;
                case "reset":
                    if(!(sender instanceof Player player)) {
                        sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
                        return true;
                    }

                    GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
                    profile.playerUpdate(true);
                    return true;
                case "debug":
                    StringBuilder sb = new StringBuilder();
                    sb.append("&6&lPractice Debug");
                    sb.append("\n&6Version: &f" + plugin.getDescription().getVersion());
                    sb.append("\n\n&6In Game: &f" + plugin.getGameManager().getTotalInGame());
                    sb.append(" &7- &6In Queue: &f" + plugin.getGameQueueManager().getTotalInQueue());
                    sb.append(" &7- &6Active Games: &f" + plugin.getGameManager().getActiveGames().size());

                    sender.sendMessage(Colors.get(sb.toString()));
                    return true;
                case "setnpcid":
                    if(!(sender instanceof Player player)) {
                        sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
                        return true;
                    }

                    if(args.length > 2) {
                        NPCClickable clickable;
                        try {
                            clickable = NPCClickable.valueOf(args[1].toUpperCase());
                        } catch (IllegalArgumentException ignored) {
                            player.sendMessage(ChatColor.RED + "Invalid clickable type. Valid options: DUEL_QUEUE, MINIGAME_QUEUE, STATISTICS, LEADERBOARDS");
                            return true;
                        }

                        int npcId = 0;

                        try {
                            npcId = Integer.parseInt(args[2]);
                        } catch (NumberFormatException ignored) {
                            player.sendMessage(ChatColor.RED + "Invalid ID.");
                            return true;
                        }

                        plugin.getConfig().set("npc_ids." + clickable.name().toLowerCase(), npcId);
                        player.sendMessage(Colors.get("&aNPC id &f" + npcId + "&a is now assigned to &f" + clickable.name().toLowerCase() + "&a."));
                        return true;
                    }
                    break;
                case "togglerankedqueues":
                    for(GameQueue queue : plugin.getGameQueueManager().getGameQueues()) {
                        if(queue.getType().equals(GameQueue.Type.RANKED)) {
                            queue.setAvailable(!queue.isAvailable());
                        }
                    }

                    sender.sendMessage(ChatColor.GREEN + "All ranked queues have been toggled.");
                    return true;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("&6&lPractice Utilities");
        sb.append("\n&6/" + label + " debug &7- &fShows a bunch of useful information.");
        sb.append("\n&6/" + label + " reset &7- &fResets your player.");
        sb.append("\n&6/" + label + " setkiteditor &7- &fSets the kit editor location.");
        sb.append("\n&6/" + label + " setlobby &7- &fSets the lobby location.");
        sb.append("\n&6/" + label + " setnpcid <clickable type> <npc id> &7- &fAssign a clickable type to an NPC.");
        sb.append("\n&6/" + label + " togglerankedqueues &7- &fToggles all ranked queues on or off.");

        sender.sendMessage(Colors.get(sb.toString()));

        return true;
    }
}
