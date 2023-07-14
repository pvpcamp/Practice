package camp.pvp.commands;

import camp.pvp.Practice;
import camp.pvp.arenas.Arena;
import camp.pvp.arenas.ArenaManager;
import camp.pvp.arenas.ArenaPosition;
import camp.pvp.utils.Colors;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArenaCommand implements CommandExecutor {

    private Practice plugin;
    public ArenaCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("arena").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length > 1) {
                String arenaName = args[1];
                if (!arenaName.matches("[a-zA-Z]+")) {
                    player.sendMessage(ChatColor.RED + "Arena names can only contain letters A-Z.");
                    return true;
                }

                ArenaManager arenaManager = plugin.getArenaManager();
                Arena arena = arenaManager.getArenaFromName(args[1]);
                boolean exists = arena != null;
                String existsMessage = ChatColor.RED + "The arena you specified does not exist.";

                switch (args.length) {
                    case 2:
                        switch (args[0].toLowerCase()) {
                            case "create":
                                if(exists) {
                                    player.sendMessage(ChatColor.RED + "This arena already exists.");
                                    return true;
                                }

                                arena = new Arena(args[1].toLowerCase());
                                arenaManager.getArenas().add(arena);
                                player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " has been created.");
                                return true;
                            case "delete":
                                if(!exists) {
                                    player.sendMessage(existsMessage);
                                    return true;
                                }

                                arenaManager.deleteArena(arena);
                                player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " has been deleted.");
                                return true;
                            case "toggle":
                                if(!exists) {
                                    player.sendMessage(existsMessage);
                                    return true;
                                }

                                arena.setEnabled(!arena.isEnabled());
                                player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " has been "
                                                + (arena.isEnabled() ? ChatColor.WHITE + "enabled" : ChatColor.RED + "disabled") + ChatColor.GREEN + ".");
                                return true;
                            case "type":
                                if(!exists) {
                                    player.sendMessage(existsMessage);
                                    return true;
                                }

                                player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " type is currently set to " + ChatColor.WHITE + arena.getType().name() + ChatColor.GREEN + ".");
                                return true;
                            case "info":
                                if(!exists) {
                                    player.sendMessage(existsMessage);
                                    return true;
                                }

                                StringBuilder sb = new StringBuilder();
                                sb.append("&6&lArena Info");
                                sb.append("\n&6Arena: &f" + Colors.get(arena.getDisplayName()) + ChatColor.GRAY + " (" + arena.getName() + ")");
                                sb.append("\n&6Type: &f" + arena.getType().name());
                                sb.append("\n&6Queueable: &f" + arena.isEnabled());
                                sb.append("\n&6Available: &f" + !arena.isInUse());

                                List<String> validPositions = arena.getType().getValidPositions();
                                for(String vp : validPositions) {
                                    ArenaPosition pos = arena.getPositions().get(vp);
                                    sb.append("\n" + (pos != null ? " &a✓ " : " &4[x] ") + vp);
                                }

                                for(String pos : arena.getPositions().keySet()) {
                                    if(!validPositions.contains(pos)) {
                                        sb.append("\n" + " &4&l[INVALID FOR TYPE] " + pos);
                                    }
                                }

                                player.sendMessage(Colors.get(sb.toString()));
                                return true;
                            default:
                                break;
                        }
                        break;
                    case 3:
                        if(exists) {
                            switch (args[0].toLowerCase()) {
                                case "displayname":
                                    arena.setDisplayName(args[2]);
                                    player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName()
                                            + ChatColor.GREEN + " display name has been set to " + Colors.get(arena.getDisplayName()) + ChatColor.GREEN + ".");
                                    return true;
                                case "rename":
                                    if (!args[2].matches("[a-zA-Z]+")) {
                                        player.sendMessage(ChatColor.RED + "Arena names can only contain letters A-Z.");
                                        return true;
                                    }

                                    player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName()
                                            + ChatColor.GREEN + " has been renamed to " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GREEN + ".");
                                    arena.setName(args[2].toLowerCase());
                                    return true;
                                case "type":
                                    Arena.Type type = null;
                                    for(Arena.Type t : Arena.Type.values()) {
                                        if(t.name().equalsIgnoreCase(args[2])) {
                                            type = t;
                                        }
                                    }

                                    if(type == null) {
                                        List<String> list = new ArrayList<>();
                                        for(Arena.Type t : Arena.Type.values()) {
                                            list.add(t.name());
                                        }

                                        StringBuilder sb = new StringBuilder();
                                        final int size = list.size();
                                        int x = size;
                                        while(x != 0) {
                                            final String s = list.get(0);
                                            list.remove(s);
                                            sb.append(s);

                                            x--;
                                            if(x == 0) {
                                                sb.append(".");
                                            } else {
                                                sb.append(", ");
                                            }
                                        }

                                        player.sendMessage(ChatColor.RED + "Invalid arena type. Valid arena types: " + sb.toString());
                                        return true;
                                    }

                                    arena.setType(type);
                                    player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN
                                            + " type has been set to " + ChatColor.WHITE + type.name() + ChatColor.GREEN + ".");
                                    return true;
                                case "position":
                                    ArenaPosition position = null;
                                    Location location = player.getLocation();
                                    for(String s : arena.getType().getValidPositions()) {
                                        if(s.equalsIgnoreCase(args[2])) {
                                            position = new ArenaPosition(s, location);
                                            arena.getPositions().put(s, position);
                                            break;
                                        }
                                    }

                                    if(position == null) {
                                        List<String> list = new ArrayList<>(arena.getType().getValidPositions());

                                        StringBuilder sb = new StringBuilder();
                                        final int size = list.size();
                                        int x = size;
                                        while(x != 0) {
                                            final String s = list.get(0);
                                            list.remove(s);
                                            sb.append(s);

                                            x--;
                                            if(x == 0) {
                                                sb.append(".");
                                            } else {
                                                sb.append(", ");
                                            }
                                        }

                                        player.sendMessage(ChatColor.RED + "Invalid arena position. Valid positions for type '" + arena.getType().name() + "': " + sb.toString());
                                        return true;
                                    }

                                    player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " position "
                                            + ChatColor.WHITE + position.getPosition() + ChatColor.GREEN + " has been set to your current location.");
                                    return true;
                                case "teleport":
                                    position = arena.getPositions().get(args[2].toLowerCase());
                                    if(position == null) {
                                        player.sendMessage(ChatColor.RED + "The position you have specified has not been set or is invalid for this arena type.");
                                        return true;
                                    }

                                    player.teleport(position.getLocation());
                                    player.sendMessage(ChatColor.GREEN + "You have been teleported to " + ChatColor.WHITE + position.getPosition() + ChatColor.GREEN + ".");
                                    return true;
                            }
                        } else {
                            player.sendMessage(existsMessage);
                        }
                        break;
                }
            }

            StringBuilder help = new StringBuilder();
            help.append("&6&l/arena &r&6Help");
            help.append("\n&7<> Required, [] Optional");
            help.append("\n&6/arena create <name> &7- &fCreates a new arena.");
            help.append("\n&6/arena delete <name> &7- &fDeletes an arena.");
            help.append("\n&6/arena toggle <name> &7- &fToggles an arena enabled or disabled.");
            help.append("\n&6/arena info <name> &7- &fView all information about a specific arena.");
            help.append("\n&6/arena displayname <name> <display name> &7- &fSets an arena's display name.");
            help.append("\n&6/arena rename <name> <new name> &7- &fRenames an arena.");
            help.append("\n&6/arena type <name> [type] &7- &fGets or sets an arena type.");
            help.append("\n&6/arena position <name> <position> &7- &fSets an arena position at your current location.");
            help.append("\n&6/arena teleport <name> <position> &7- &fTeleports you to a specific arena position.");

            player.sendMessage(Colors.get(help.toString()));
        }

        return true;
    }
}
