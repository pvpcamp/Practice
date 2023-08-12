package camp.pvp.practice.arenas;

import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public class StoredBlock {

    private Material type;
    private BlockState blockState;
    @Getter private final Location location;
    public StoredBlock(Block block) {
        this.type = block.getType();
        this.blockState = block.getState();
        this.location = block.getLocation();
    }

    public StoredBlock(Location location) {
        this.type = Material.AIR;
        this.location = location;
    }

    public StoredBlock(Block block, Location location) {
        this.type = block.getType();
        this.blockState = block.getState();
        this.location = location;
    }

    public void replace() {
        Block block = location.getBlock();

        Chunk chunk = block.getChunk();
        if(!chunk.isLoaded()) {
            chunk.load();
        }

        if(blockState != null && block.getState().getData() != blockState.getData()) {

            block.setType(Material.AIR);

            block.setType(blockState.getType());

            BlockState bs = block.getState();
            bs.setType(blockState.getType());
            bs.setData(blockState.getData());
            bs.update(true, false);
        } else {
            block.setType(type);
        }
    }
}
