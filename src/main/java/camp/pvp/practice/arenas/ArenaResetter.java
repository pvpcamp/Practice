package camp.pvp.practice.arenas;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.LinkedList;
import java.util.Queue;

public class ArenaResetter implements Runnable{

    private ArenaManager arenaManager;
    private Queue<Arena> arenas;
    public ArenaResetter(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
        this.arenas = new LinkedList<>();
    }

    public void addArena(Arena arena) {
        arenas.add(arena);
    }

    public int queueSize() {
        return arenas.size();
    }

    @Override
    public void run() {
        Arena arena = arenas.peek();
        if(arena != null) {
            for (int i = 0; i < 500; i++) {
                if (arena.getPlacedBlocks().size() > 0) {
                    final Block block = arena.getPlacedBlocks().get(0);
                    Chunk chunk = block.getChunk();
                    if(!chunk.isLoaded()) {
                        chunk.load();
                    }

                    block.setType(Material.AIR);
                    arena.getPlacedBlocks().remove(block);
                } else if(arena.getBrokenBlocks().size() > 0) {
                    BrokenBlock brokenBlock = arena.getBrokenBlocks().get(0);
                    brokenBlock.replace();
                    arena.getBrokenBlocks().remove(brokenBlock);
                }
            }

            if (arena.getPlacedBlocks().isEmpty() && arena.getBrokenBlocks().isEmpty()) {
                arena.setInUse(false);
                arenas.poll();
            }
        }
    }
}
