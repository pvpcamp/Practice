package camp.pvp.practice.commands;

import camp.pvp.practice.arenas.*;
import camp.pvp.practice.guis.arenas.ArenaInfoGui;
import camp.pvp.practice.guis.arenas.ArenaListChooseTypeGui;
import camp.pvp.practice.guis.arenas.ArenaPositionsGui;
import camp.pvp.practice.guis.arenas.ArenaTeleportGui;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.Practice;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class ArenaCommand implements CommandExecutor, TabCompleter {

    private Practice plugin;
    private static List<String> subCommands = Arrays.asList("list", "create", "info", "rename", "positions", "displayname", "buildlimit", "voidlevel", "teleport", "copy");
    public ArenaCommand(Practice plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getServer().getPluginCommand("arena");
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) return true;

        if(args.length == 0) {
            sender.sendMessage(getHelp());
            return true;
        }

        Player player = (Player) sender;

        switch(args[0].toLowerCase()) {
            case "list" -> {
                list(player);
            }
            case "create" -> {
                create(player, args);
            }
            case "info" -> {
                info(player, args);
            }
            case "name" -> {
                rename(player, args);
            }
            case "positions" -> {
                positions(player, args);
            }
            case "displayname" -> {
                displayName(player, args);
            }
            case "buildlimit", "bl" -> {
                buildlimit(player, args);
            }
            case "void", "vl", "voidlevel" -> {
                voidLevel(player, args);
            }
            case "teleport", "tp" -> {
                teleport(player, args);
            }
            case "addchest" -> {
                addChest(player, args);
            }
            case "removechest", "delchest" -> {
                removeChest(player, args);
            }
            case "copy", "duplicate" -> {
                copy(player, args);
            }
            default -> {
                player.sendMessage(getHelp());
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        final List<String> completions = new ArrayList<>();

        List<String> arenas = new ArrayList<>();
        for(Arena arena : plugin.getArenaManager().getOriginalArenas()) {
            arenas.add(arena.getName());
        }

        if(args.length == 1) {
            StringUtil.copyPartialMatches(args[0], subCommands, completions);
            return completions;
        }

        if(args.length == 2) {
            StringUtil.copyPartialMatches(args[1], arenas, completions);
            return completions;
        }

        return null;
    }

    private void list(Player player) {
        new ArenaListChooseTypeGui().open(player);
    }

    private void create(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /arena create <name>");
            return;
        }

        Arena arena = plugin.getArenaManager().getArenaFromName(args[1]);
        if(arena != null) {
            player.sendMessage(ChatColor.RED + "The arena you specified already exists.");
            return;
        }

        String name = args[1];
        if(!name.matches("[a-zA-Z]+")) {
            player.sendMessage(ChatColor.RED + "Arena names can only contain letters A-Z.");
            return;
        }

        arena = new Arena(name.toLowerCase());
        arena.setDisplayName(name);
        plugin.getArenaManager().getArenas().add(arena);
        player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " has been created.");

        Location location = plugin.getArenaManager().getNextAvailableArenaLocation(player.getWorld());
        if(location != null) {
            player.teleport(location);
            player.sendMessage(Colors.get(
                    "&aThe arena manager has found a location for you to set up your arena automatically.",
                    "&7When setting up your arena, please keep it as close to X: " + location.getBlockX() + " and Z: " + location.getBlockZ() + " as possible."
            ));
        } else {
            player.sendMessage(ChatColor.RED + "The arena manager could not find a valid location. You will need to find a safe location to set up your arena.");
        }

        new ArenaInfoGui(plugin.getArenaManager(), arena).open(player);
    }

    private void info(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /arena info <arena>");
            return;
        }

        Arena arena = plugin.getArenaManager().getArenaFromName(args[1]);
        if(arena == null) {
            player.sendMessage(ChatColor.RED + "The arena you specified does not exist.");
            return;
        }

        new ArenaInfoGui(plugin.getArenaManager(), arena).open(player);
    }

    private void positions(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /arena positions <arena>");
            return;
        }

        Arena arena = plugin.getArenaManager().getArenaFromName(args[1]);
        if(arena == null) {
            player.sendMessage(ChatColor.RED + "The arena you specified does not exist.");
            return;
        }

        if(arena.isCopy()) {
            player.sendMessage(ChatColor.RED + "You cannot update the positions of a copy manually.");
            return;
        }

        new ArenaPositionsGui(arena).open(player);
    }

    private void rename(Player player, String[] args) {
        if(args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /arena name <arena> <new name>");
            return;
        }

        Arena arena = plugin.getArenaManager().getArenaFromName(args[1]);
        if(arena == null) {
            player.sendMessage(ChatColor.RED + "The arena you specified does not exist.");
            return;
        }

        if(plugin.getArenaManager().getArenaFromName(args[2]) != null) {
            player.sendMessage(ChatColor.RED + "The new name you are trying to set is already assigned to an arena.");
            return;
        }

        String name = args[2];
        if(!name.matches("[a-zA-Z]+")) {
            player.sendMessage(ChatColor.RED + "Arena names can only contain letters A-Z.");
            return;
        }

        arena.setName(args[2].toLowerCase());
        int copies = 0;
        for(Arena a : plugin.getArenaManager().getArenaCopies(arena)) {
            String cn = a.getName().replace(arena.getName() + "_copy_", "");
            a.setName(name.toLowerCase() + "_copy_" + cn);
            a.setParentName(name.toLowerCase());
            copies++;
        }

        if(copies == 0) {
            player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " has been renamed to " + ChatColor.WHITE + name.toLowerCase() + ChatColor.GREEN + ".");
        } else {
            player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " and its " + copies + " copies have been renamed to " + ChatColor.WHITE + name.toLowerCase() + ChatColor.GREEN + ".");
        }
    }

    private void buildlimit(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /arena buildlimit <arena> <limit>");
            return;
        }

        Arena arena = plugin.getArenaManager().getArenaFromName(args[1]);
        if(arena == null) {
            player.sendMessage(ChatColor.RED + "The arena you specified does not exist.");
            return;
        }

        int level = 0;
        try {
            level = Integer.parseInt(args[2]);
        } catch(NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "You must provide a valid number for the build limit.");
        }

        arena.setBuildLimit(level);
        plugin.getArenaManager().updateArenaCopies(arena, false);

        int copies = plugin.getArenaManager().getArenaCopies(arena).size();

        if(copies == 0) {
            player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " now has a build limit of " + ChatColor.WHITE + level + ChatColor.GREEN + ".");
        } else {
            player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " and its " + copies + " copies now have a build limit of " + ChatColor.WHITE + level + ChatColor.GREEN + ".");
        }
    }

    private void voidLevel(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /arena voidlevel <arena> <level>");
            return;
        }

        Arena arena = plugin.getArenaManager().getArenaFromName(args[1]);
        if(arena == null) {
            player.sendMessage(ChatColor.RED + "The arena you specified does not exist.");
            return;
        }

        int level = 0;
        try {
            level = Integer.parseInt(args[2]);
        } catch(NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "You must provide a valid number for the void level.");
        }

        arena.setVoidLevel(level);
        plugin.getArenaManager().updateArenaCopies(arena, false);

        int copies = plugin.getArenaManager().getArenaCopies(arena).size();

        if(copies == 0) {
            player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " now has a void level of " + ChatColor.WHITE + level + ChatColor.GREEN + ".");
        } else {
            player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " and its " + copies + " copies now have a void level of " + ChatColor.WHITE + level + ChatColor.GREEN + ".");
        }
    }

    private void displayName(Player player, String[] args) {
        if(args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /arena displayname <arena> <name>");
            return;
        }

        Arena arena = plugin.getArenaManager().getArenaFromName(args[1]);
        if(arena == null) {
            player.sendMessage(ChatColor.RED + "The arena you specified does not exist.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for(int i = 2; i < args.length; i++) {
            sb.append(args[i]);
            if(i + 1 != args.length) {
                sb.append(" ");
            }
        }

        arena.setDisplayName(sb.toString());
        int copies = 0;
        for(Arena a : plugin.getArenaManager().getArenaCopies(arena)) {
            a.setDisplayName(sb.toString());
            copies++;
        }

        if(copies == 0) {
            player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " now has the display name " + Colors.get(arena.getDisplayName()) + ChatColor.GREEN + ".");
        } else {
            player.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " and its " + copies + " copies now have the display name " + Colors.get(arena.getDisplayName()) + ChatColor.GREEN + ".");
        }
    }

    private void addChest(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /arena " + args[0].toLowerCase() + " <arena>");
            return;
        }

        GameProfile profile = plugin.getGameProfileManager().getLoadedProfile(player.getUniqueId());

        if(profile.getSelectedLocation() == null) {
            player.sendMessage(ChatColor.RED + "You must select a location first.");
            return;
        }

        Arena arena = plugin.getArenaManager().getArenaFromName(args[1]);
        if(arena == null) {
            player.sendMessage(ChatColor.RED + "The arena you specified does not exist.");
            return;
        }

        if(!arena.getType().isGenerateLoot()) {
            player.sendMessage(ChatColor.RED + "This arena type does not support loot chests.");
            return;
        }

        for(LootChest lootChest : arena.getLootChests()) {
            if(lootChest.getLocation().equals(profile.getSelectedLocation())) {
                player.sendMessage(ChatColor.RED + "A loot chest already exists at this location.");
                return;
            }
        }

        ArrangedGui gui = new ArrangedGui("&6Loot Chest Category");
        for(LootChest.Category c : LootChest.Category.values()) {
            GuiButton categoryButton = new GuiButton(Material.CHEST, "&6&l" + c.name());
            categoryButton.setCloseOnClick(true);
            categoryButton.setLore("&7Click to set chest", "&7category to &f" + c.name() + "&7.");
            categoryButton.setAction((p, b, g, click) -> {
                LootChest chest = new LootChest(profile.getSelectedLocation(), c);
                arena.getLootChests().add(chest);
                player.sendMessage(ChatColor.GREEN + "Loot chest added to arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + ".");
            });
            gui.addButton(categoryButton);
        }

        gui.open(player);
    }

    private void removeChest(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /arena " + args[0].toLowerCase() + " <arena>");
            return;
        }

        GameProfile profile = plugin.getGameProfileManager().getLoadedProfile(player.getUniqueId());

        if(profile.getSelectedLocation() == null) {
            player.sendMessage(ChatColor.RED + "You must select a location first.");
            return;
        }

        Arena arena = plugin.getArenaManager().getArenaFromName(args[1]);
        if(arena == null) {
            player.sendMessage(ChatColor.RED + "The arena you specified does not exist.");
            return;
        }

        if(!arena.getType().isGenerateLoot()) {
            player.sendMessage(ChatColor.RED + "This arena type does not support loot chests.");
            return;
        }

        LootChest chest = null;
        for(LootChest lootChest : arena.getLootChests()) {
            if(lootChest.getLocation().equals(profile.getSelectedLocation())) {
                chest = lootChest;
                break;
            }
        }

        if(chest == null) {
            player.sendMessage(ChatColor.RED + "There is no loot chest at this location.");
            return;
        }

        arena.getLootChests().remove(chest);
        player.sendMessage(ChatColor.GREEN + "Loot chest removed from arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + ".");
    }

    private void teleport(Player player, String[] args) {

        if(args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /arena teleport <arena>");
            return;
        }

        Arena arena = plugin.getArenaManager().getArenaFromName(args[1]);
        if(arena == null) {
            player.sendMessage(ChatColor.RED + "The arena you specified does not exist.");
            return;
        }

        new ArenaTeleportGui(arena).open(player);
    }

    private void copy(Player player, String[] args) {

        if(args.length < 5) {
            player.sendMessage(ChatColor.RED + "Usage: /arena copy <arena> <xD> <zD> <x times>");
            return;
        }

        int xD, zD, times;

        try {
            xD = Integer.parseInt(args[2]);
            zD = Integer.parseInt(args[3]);
            times = Integer.parseInt(args[4]);
        } catch(NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "You must provide valid X and Z differences.");
            return;
        }

        if(xD < 500 && zD < 500) {
            player.sendMessage(ChatColor.RED + "Arenas should be at a minimum 500 blocks apart.");
            return;
        }

        Arena arena = plugin.getArenaManager().getArenaFromName(args[1]);
        if(arena == null) {
            player.sendMessage(ChatColor.RED + "The arena you specified does not exist.");
            return;
        }

        Arena furthest = null;
        for(Arena a : plugin.getArenaManager().getArenaCopies(arena)) {
            if(furthest == null || (xD > 0 && a.getXDifference() > furthest.getXDifference()) || (zD > 0 && a.getZDifference() > furthest.getZDifference())) {
                furthest = a;
            }
        }

        Arena startFromArena = furthest;
        if(furthest == null) startFromArena = arena;

        arena.scanArena();

        for(int i = 0; i < times; i++) {
            Arena copy = plugin.getArenaManager().createCopy(arena, startFromArena.getXDifference(), startFromArena.getZDifference(), xD * (i + 1), zD * (i + 1));
            plugin.getArenaManager().getArenas().add(copy);
        }

        player.sendMessage(ChatColor.GREEN + "Created " + times + " copies of arena " + ChatColor.WHITE + startFromArena.getName() + ChatColor.GREEN + " with X and Z differences of " + ChatColor.WHITE + xD + ChatColor.GREEN + " and " + ChatColor.WHITE + zD + ChatColor.GREEN + ".");
    }

    public String getHelp() {
        StringBuilder help = new StringBuilder();
        help.append("&6&l/arena &r&6Help");
        help.append("\n&7<> Required, [] Optional");
        help.append("\n&6/arena list &7- &fView a list of arenas.");
        help.append("\n&6/arena create <name> &7- &fCreates a new arena.");
        help.append("\n&6/arena info <name> &7- &fView, toggle, and update an arena's settings.");
        help.append("\n&6/arena positions <name> &7- &fUpdate an arena's positions.");
        help.append("\n&6/arena teleport <name> &7- &fTeleport to an arena.");
        help.append("\n&6/arena addchest <name> &7- &fAdd a loot chest to an arena.");
        help.append("\n&6/arena removechest <name> &7- &fRemove a loot chest from an arena.");
        help.append("\n&6/arena displayname <name> <display name> &7- &fSets an arena's display name.");
        help.append("\n&6/arena rename <name> <new name> &7- &fRenames an arena.");
        help.append("\n&6/arena buildlimit <name> <limit> &7- &fSets the build limit for an arena.");
        help.append("\n&6/arena voidlevel <name> <level> &7- &fSets the void level for an arena.");
        help.append("\n&6/arena copy <name> <x> <z> <times> &7- &fCopies an arena to another location." +
                "\n&eX and Z are blocks away from the original." +
                "\n&eTimes will duplicate x amount of times." +
                "\n&eStart from will start copying from a specified arena.");

        return Colors.get(help.toString());
    }
}
