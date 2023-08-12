package camp.pvp.practice.arenas;

import com.sk89q.worldedit.internal.annotation.Direction;
import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Directional;

public class ModifiedBlock {

    private Material type;
    private BlockState blockState;
    private Directional directional;
    @Getter private final Location location;
    public ModifiedBlock(Block block) {
        this.type = block.getType();
        this.blockState = block.getState();
        this.location = block.getLocation();

        if(blockState instanceof Directional) {
            directional = (Directional) blockState;
        }
    }

    public ModifiedBlock(Location location) {
        this.type = Material.AIR;
        this.location = location;
    }

    public ModifiedBlock(Block block, Location location) {
        this.type = block.getType();
        this.blockState = block.getState();
        this.location = location;

        if(blockState instanceof Directional) {
            directional = (Directional) blockState;
        }
    }

    public void replace() {
        Block block = location.getBlock();

        Chunk chunk = block.getChunk();
        if(!chunk.isLoaded()) {
            chunk.load();
        }

        if(blockState != null) {
            block.setType(blockState.getType());

            BlockState bs = block.getState();
            bs.setType(blockState.getType());
            bs.setData(blockState.getData());
            bs.update(true);

            if(directional != null) {
                Directional newDir = (Directional) bs;
                newDir.setFacingDirection(directional.getFacing());
            }
        } else {
            block.setType(type);
        }
    }
}
