package camp.pvp.practice.arenas;

import java.util.LinkedList;
import java.util.Queue;

public class ArenaBlockRestorer implements Runnable {

    private Queue<Arena> arenaQueue;
    public ArenaBlockRestorer() {
        this.arenaQueue = new LinkedList<>();
    }

    public void addArena(Arena arena) {
        this.arenaQueue.add(arena);
    }

    @Override
    public void run() {
        for(int i = 0; i < 1000; i++) {
            if(arenaQueue.isEmpty()) {
                return;
            }

            Arena arena = arenaQueue.peek();
            if(arena != null) {
                if(arena.getRestoreBlockQueue().isEmpty()) {
                    arena.setInUse(false);
                    arenaQueue.poll();
                    return;
                }

                arena.getRestoreBlockQueue().poll().restore();
            }
        }
    }
}
