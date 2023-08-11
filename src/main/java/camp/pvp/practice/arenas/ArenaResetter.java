package camp.pvp.practice.arenas;

import camp.pvp.practice.utils.Colors;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.LinkedList;
import java.util.Queue;

@Getter
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
                if (!arena.getPlacedBlocks().isEmpty()) {
                    final ModifiedBlock mb = arena.getPlacedBlocks().get(0);
                    mb.replace();
                    arena.getPlacedBlocks().remove(mb);
                } else if(!arena.getModifiedBlocks().isEmpty()) {
                    final ModifiedBlock block = arena.getModifiedBlocks().get(0);
                    block.replace();
                    arena.getModifiedBlocks().remove(block);
                } else {
                    arena.setInUse(false);
                    arenas.poll();
                }
            }
        }
    }
}
