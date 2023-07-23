package camp.pvp.practice.commands;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaManager;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.Practice;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

            if(args.length > 0) {

                if(args[0].equalsIgnoreCase("list")) {
                    List<Arena> arenas = new ArrayList<>(plugin.getArenaManager().getArenas());
                    Collections.sort(arenas);

                    TextComponent[] components = new TextComponent[arenas.size() + 1];
                    TextComponent title = new TextComponent(Colors.get("&6Arenas &7(" + arenas.size() + "&7): &f"));

                    components[0] = title;

                    for(int i = 0; i < arenas.size(); i++) {
                        Arena a = arenas.get(i);
                        String name = (a.isEnabled() ? "&e" : "&c") + a.getName();

                        String spacer;
                        if(i + 1 == arenas.size()) {
                            spacer = "&7.";
                        } else {
                            spacer = "&7, ";
                        }

                        TextComponent component = new TextComponent(name + spacer);
                        ComponentBuilder builder = new ComponentBuilder(Colors.get("&6Arena: &f" + a.getName()));
                        builder.append(Colors.get("&6Enabled: &f" + a.isEnabled()));
                        builder.append(Colors.get("&6Type: &f" + a.getType().name()));

                        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, builder.create()));

                        components[i + 1] = component;
                    }

                    player.spigot().sendMessage(components);
                    return true;
                }

                if(args.length < 2) {
                    player.sendMessage(getHelp());
                    return true;
                }

                String arenaName = args[1];
                if(!arenaName.matches("[a-z]+")) {
                    player.sendMessage(ChatColor.RED + "Arena names can only contain letters A-Z.");
                    return true;
                }

                ArenaManager arenaManager = plugin.getArenaManager();
                Arena arena = arenaManager.getArenaFromName(args[1]);

                boolean exists = arena != null;
                String existsMessage = ChatColor.RED + "The arena you specified does not exist.";

                switch(args[0].toLowerCase()) {
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

                        if(args.length > 2) {
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

                                Collections.sort(list);

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
                        } else {
                            if(!exists) {
                                player.sendMessage(existsMessage);
                                return true;
                            }

                            player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " type is currently set to " + ChatColor.WHITE + arena.getType().name() + ChatColor.GREEN + ".");
                        }
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
                    case "displayname":
                        if(!exists) {
                            player.sendMessage(existsMessage);
                            return true;
                        }

                        if(args.length > 2) {
                            arena.setDisplayName(args[2]);
                            player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName()
                                    + ChatColor.GREEN + " display name has been set to " + Colors.get(arena.getDisplayName()) + ChatColor.GREEN + ".");
                            return true;
                        }
                        break;
                    case "rename":
                        if(!exists) {
                            player.sendMessage(existsMessage);
                            return true;
                        }

                        if(args.length > 2) {

                            if (!args[2].matches("[a-zA-Z]+")) {
                                player.sendMessage(ChatColor.RED + "Arena names can only contain letters A-Z.");
                                return true;
                            }

                            Arena oldArena = plugin.getArenaManager().getArenaFromName(args[2].toLowerCase());
                            if(oldArena != null) {
                                player.sendMessage(ChatColor.RED + "The new name you are trying to set is already assigned to an arena.");
                                return true;
                            }

                            player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName()
                                    + ChatColor.GREEN + " has been renamed to " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GREEN + ".");
                            arena.setName(args[2].toLowerCase());
                        }
                        break;
                    case "position":
                        if(!exists) {
                            player.sendMessage(existsMessage);
                            return true;
                        }

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

                            sb = new StringBuilder();
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
                        if(!exists) {
                            player.sendMessage(existsMessage);
                            return true;
                        }

                        if(args.length > 2) {
                            position = arena.getPositions().get(args[2].toLowerCase());
                            if (position == null) {
                                player.sendMessage(ChatColor.RED + "The position you have specified has not been set or is invalid for this arena type.");
                                return true;
                            }

                            player.teleport(position.getLocation());
                            player.sendMessage(ChatColor.GREEN + "You have been teleported to " + ChatColor.WHITE + position.getPosition() + ChatColor.GREEN + ".");
                        }
                        return true;
                    case "copy":
                    case "getcopies":
                        player.sendMessage(ChatColor.GREEN + "Come soon!");
                        return true;
                }
            }

            player.sendMessage(getHelp());
        }

        return true;
    }

    public String getHelp() {
        StringBuilder help = new StringBuilder();
        help.append("&6&l/arena &r&6Help");
        help.append("\n&7<> Required, [] Optional");
        help.append("\n&6/arena list &7- &fView a list of arenas.");
        help.append("\n&6/arena create <name> &7- &fCreates a new arena.");
        help.append("\n&6/arena delete <name> &7- &fDeletes an arena.");
        help.append("\n&6/arena toggle <name> &7- &fToggles an arena enabled or disabled.");
        help.append("\n&6/arena info <name> &7- &fView all information about a specific arena.");
        help.append("\n&6/arena displayname <name> <display name> &7- &fSets an arena's display name.");
        help.append("\n&6/arena rename <name> <new name> &7- &fRenames an arena.");
        help.append("\n&6/arena type <name> [type] &7- &fGets or sets an arena type.");
        help.append("\n&6/arena position <name> <position> &7- &fSets an arena position at your current location.");
        help.append("\n&6/arena teleport <name> <position> &7- &fTeleports you to a specific arena position.");

        return Colors.get(help.toString());
    }
}
