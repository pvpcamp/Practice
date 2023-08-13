package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
import org.bukkit.Chunk;

import java.util.LinkedList;
import java.util.Queue;

public class ArenaResetter implements Runnable{

    private Practice plugin;
    private Queue<Arena> arenas;
    public ArenaResetter(Practice plugin) {
        this.plugin = plugin;
        this.arenas = new LinkedList<>();
    }

    public void addToQueue(Arena arena) {
        this.arenas.add(arena);
    }

    @Override
    public void run() {
        while(!arenas.isEmpty()) {
            final Arena arena = arenas.peek();
            for(int i = 0; i < 8; i++) {
                if(!arena.getChunkQueue().isEmpty()) {
                    final Chunk chunk = arena.getChunkQueue().poll();
                    if(chunk.isLoaded()) {
                        chunk.unload(false);
                    }
                } else {
                    arena.setInUse(false);
                    arenas.poll();
                    return;
                }
            }
        }
    }
}
