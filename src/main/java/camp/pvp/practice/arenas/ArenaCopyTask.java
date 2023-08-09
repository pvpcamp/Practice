package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
import camp.pvp.practice.profiles.GameProfile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter @Setter
public class ArenaCopyTask implements Runnable {

    private final Practice plugin;
    private final Player player;
    private final GameProfile profile;
    private final Arena arena, newArena;
    private final int xDifference;
    private final int zDifference;
    private int taskId;
    private Location corner1, corner2;
    private World world;
    private long started, ended;
    private Queue<Block> blocks;

    public ArenaCopyTask(Practice plugin, Player player, GameProfile profile, Arena arena, Arena newArena, int xDifference, int zDifference) {
        this.plugin = plugin;
        this.player = player;
        this.profile = profile;
        this.arena = arena;
        this.newArena = newArena;
        this.xDifference = xDifference;
        this.zDifference = zDifference;
        this.blocks = new LinkedList<>();
    }

    public boolean init() {
        corner1 = arena.getPositions().get("corner1").getLocation();
        corner2 = arena.getPositions().get("corner2").getLocation();

        world = corner1.getWorld();

        int minX, minY, minZ, maxX, maxY, maxZ;
        minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    Location location = new Location(world, x, y, z);
                    Block block = location.getBlock();
                    if(!block.isEmpty()) {
                        blocks.add(location.getBlock());

                        Location newLocation = new Location(world, x + xDifference, y, z + zDifference);
                        Block newBlock = newLocation.getBlock();
                        if (!newBlock.isEmpty()) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public void run() {
        if(started == 0) {
            this.started = System.currentTimeMillis();
            player.sendMessage(ChatColor.GREEN + "Copy task for arena " + ChatColor.WHITE + newArena.getName() + ChatColor.GREEN + " has started. " + ChatColor.GRAY + "(" + blocks.size() + " blocks)");
            profile.setArenaCopyTask(this);
        }

        for(int i = 0; i < 1000; i++) {
            if(!blocks.isEmpty()) {
                Block block = blocks.poll();
                Location newLocation = new Location(block.getWorld(), block.getX() + xDifference, block.getY(), block.getZ() + zDifference);
                Block newBlock = newLocation.getBlock();
                if (block.isEmpty()) {
                    i--;
                } else {
                    newBlock.setType(block.getType());
                    if(newBlock.getData() != block.getData()) {
                        newBlock.setData(block.getData());
                    }
                }
            } else {
                ended = System.currentTimeMillis();
                player.sendMessage(ChatColor.GREEN + "Copy task for arena " + ChatColor.WHITE + newArena.getName() + ChatColor.GREEN
                        + " has completed in " + ChatColor.WHITE + (TimeUnit.MILLISECONDS.toSeconds(ended - started) % 60) + " second(s)" + ChatColor.GREEN + ".");
                profile.setArenaCopyTask(null);
                plugin.getArenaManager().getArenas().add(newArena);
                return;
            }
        }
    }
}
