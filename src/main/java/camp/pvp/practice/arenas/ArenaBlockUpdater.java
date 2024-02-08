package camp.pvp.practice.arenas;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

@Getter @Setter
public class ArenaBlockUpdater implements Runnable{

    private Arena arena;
    private Queue<StoredBlock> blocks;
    private long started, ended;
    private int taskId;

    public ArenaBlockUpdater(ArenaManager arenaManager, Arena arena) {
        this.arena = arena;
        this.blocks = new LinkedList<>();

        ArenaPosition corner1 = arena.getPositions().get("corner1");
        ArenaPosition corner2 = arena.getPositions().get("corner2");

        Location c1  = corner1.getLocation();
        Location c2 = corner2.getLocation();

        World world = c1.getWorld();

        int minX, minY, minZ, maxX, maxY, maxZ;
        minX = Math.min(c1.getBlockX(), c2.getBlockX());
        minY = Math.min(c1.getBlockY(), c2.getBlockY());
        minZ = Math.min(c1.getBlockZ(), c2.getBlockZ());
        maxX = Math.max(c1.getBlockX(), c2.getBlockX());
        maxY = Math.max(c1.getBlockY(), c2.getBlockY());
        maxZ = Math.max(c1.getBlockZ(), c2.getBlockZ());

        Set<Arena> copies = arenaManager.getArenaCopies(arena);

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    Location location = new Location(world, x, y, z);
                    Block block = location.getBlock();
                    for(Arena a : copies) {
                        int xDif = a.getXDifference();
                        int zDif = a.getZDifference();

                        Location l = new Location(world, (xDif * 16) + x, y, (zDif * 16)+ z);
                        Block b = l.getBlock();

                        if(!b.getType().equals(block.getType())) {
                            blocks.add(new StoredBlock(block, l));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run() {

        if(started == 0) {
            started = System.currentTimeMillis();
        }

        for(int i = 0; i < 2000; i++) {
            if(!blocks.isEmpty()) {
                StoredBlock block = blocks.poll();
                block.replace();
            } else {
                ended = System.currentTimeMillis();
                if(taskId != 0) {
                    Bukkit.getScheduler().cancelTask(taskId);
                }
                return;
            }
        }
    }
}
