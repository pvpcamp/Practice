package camp.pvp.practice.arenas;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

public class RestoreBlock {

    private int type, data;
    private Location location;

    public RestoreBlock(Location location, int type, int data) {
        this.location = location;
        this.type = type;
        this.data = data;
    }

    public void restore() {
        Chunk chunk = location.getChunk();

        if(!chunk.isLoaded()) chunk.load();

        location.getBlock().setTypeIdAndData(type, (byte) data, false);
    }
}
