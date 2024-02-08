package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Directional;

import java.util.*;

@Getter @Setter
public class ArenaCopier implements Runnable {

    private final Practice plugin;
    private final Arena arena, newArena, startFrom;
    private final int xDifference;
    private final int zDifference;
    private int taskId;
    private Location corner1, corner2;
    private World world;
    private long started, ended;
    private Queue<Block> blocks;

    public ArenaCopier(Practice plugin, Arena arena, Arena newArena, int xDifference, int zDifference, Arena startFrom) {
        this.plugin = plugin;
        this.arena = arena;
        this.newArena = newArena;
        this.xDifference = xDifference;
        this.zDifference = zDifference;
        this.blocks = new LinkedList<>();
        this.startFrom = startFrom;
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

                        Location newLocation = new Location(world, x + startFrom.getXDifference() + xDifference * 16, y, z + startFrom.getZDifference() + zDifference * 16);
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
        }

        for(int i = 0; i < 5000; i++) {
            if(!blocks.isEmpty()) {
                Block block = blocks.poll();
                Location newLocation = new Location(block.getWorld(), block.getX() + ((startFrom.getXDifference() + xDifference) * 16), block.getY(), block.getZ() + ((startFrom.getZDifference() + zDifference) * 16));
                Block newBlock = newLocation.getBlock();
                if (block.isEmpty()) {
                    i--;
                } else {
                    Chunk chunk = block.getChunk();
                    if(!chunk.isLoaded()) {
                        chunk.load();
                    }

                    newBlock.setType(block.getType());

                    BlockState oldState = block.getState();
                    BlockState newState = newBlock.getState();

                    newState.setType(oldState.getType());
                    newState.setData(oldState.getData());
                    newState.update(true);
                }
            } else {
                ended = System.currentTimeMillis();
                plugin.getArenaManager().getArenas().add(newArena);

                if(taskId != 0) {
                    Bukkit.getScheduler().cancelTask(taskId);
                }
                return;
            }
        }
    }
}
