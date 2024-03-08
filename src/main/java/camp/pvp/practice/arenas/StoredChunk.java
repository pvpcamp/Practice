package camp.pvp.practice.arenas;

import lombok.Data;
import org.bukkit.Chunk;
import org.bukkit.World;

public @Data class StoredChunk{

    int x, z;
    World world;
    public StoredChunk(int x, int z, World world){
        this.x = x;
        this.z = z;
        this.world = world;
    }

    public Chunk getBukkitChunk() {
        return world.getChunkAt(x, z);
    }
}
