package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Torch;

public class ModifiedBlock {

    private BlockState blockState;
    @Getter private final Location location;
    public ModifiedBlock(Block block) {
        this.blockState = block.getState();
        this.location = block.getLocation();
    }

    public ModifiedBlock(Location location) {
        this.location = location;
    }

    public ModifiedBlock(Block block, Location location) {
        this.blockState = block.getState();
        this.location = location;
    }

    public ModifiedBlock(BlockState blockState, Location location) {
        this.blockState = blockState;
        this.location = location;
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
//            if(blockState.getData() instanceof Directional) {
//                Directional directional = (Directional) blockState.getData();
//
//                Directional newDirectional = ((Directional) bs.getData());
//                newDirectional.setFacingDirection(directional.getFacing());
//                block.getState().update(true);
//            } else {
//                bs.setData(blockState.getData());
//            }
        } else {
            block.setType(Material.AIR);
        }
    }
}
