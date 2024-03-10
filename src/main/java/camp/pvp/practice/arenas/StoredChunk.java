package camp.pvp.practice.arenas;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.UUID;

public @Data class StoredChunk{

    private int x, z;
    private UUID worldId;
    public StoredChunk(int x, int z, UUID worldId){
        this.x = x;
        this.z = z;
        this.worldId = worldId;
    }

    public Chunk getBukkitChunk() {
        return getWorld().getChunkAt(x, z);
    }

    public World getWorld() {
        return Bukkit.getWorld(worldId);
    }
}
