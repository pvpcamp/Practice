package camp.pvp.practice.arenas;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BrokenBlock {

    private Material material;
    private byte data;
    private Location location;
    public BrokenBlock(Block block) {
        this.material = block.getType();
        this.data = block.getData();
        this.location = block.getLocation();
    }

    public void replace() {
        Block block = location.getBlock();
        block.setType(material);

        Chunk chunk = block.getChunk();
        if(!chunk.isLoaded()) {
            chunk.load();
        }

        if(block.getData() != data) {
            block.setData(data);
        }
    }
}
