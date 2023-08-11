package camp.pvp.practice.arenas;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.LinkedList;
import java.util.Queue;

public class ArenaDeleter implements Runnable{

    private final ArenaManager arenaManager;
    private @Getter final Arena arena;
    private final Location corner1, corner2;
    private @Getter long started, ended;
    private @Getter @Setter int taskId;
    private @Getter Queue<Block> blocks;

    public ArenaDeleter(ArenaManager arenaManager, Arena arena) {
        this.arenaManager = arenaManager;
        this.arena = arena;
        this.corner1 = arena.getPositions().get("corner1").getLocation();
        this.corner2 = arena.getPositions().get("corner2").getLocation();
        this.blocks = new LinkedList<>();

        World world = corner1.getWorld();

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
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        if(started == 0) {
            this.started = System.currentTimeMillis();
        }

        for(int i = 0; i < 5000; i++) {
            if(!blocks.isEmpty()) {
                Block block = blocks.poll();
                block.setType(Material.AIR);
            } else {
                ended = System.currentTimeMillis();
                arenaManager.getArenas().remove(arena);

                if(taskId != 0) {
                    Bukkit.getScheduler().cancelTask(taskId);
                }
                return;
            }
        }
    }
}
