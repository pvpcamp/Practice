package camp.pvp.practice.arenas;

import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

public class ModifiedBlock {

    private final Material material;
    private final MaterialData materialData;
    @Getter private final Location location;
    public ModifiedBlock(Block block) {
        this.material = block.getType();
        this.materialData = block.getState().getData();
        this.location = block.getLocation();
    }

    public void replace() {
        Block block = location.getBlock();
        block.setType(material);

        Chunk chunk = block.getChunk();
        if(!chunk.isLoaded()) {
            chunk.load();
        }

        block.getState().setData(materialData);
    }
}
