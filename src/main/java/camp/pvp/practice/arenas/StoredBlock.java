package camp.pvp.practice.arenas;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

@Data
public class StoredBlock {

    private final Location location;
    private final Material type;

    public StoredBlock(Location location, Material type) {
        this.location = location;
        this.type = type;
    }

    public StoredBlock(Block block) {
        this.location = block.getLocation();
        this.type = block.getType();
    }

    public void replaceBlock(Material type, BlockState state) {
        Block block = location.getBlock();
        block.setType(type);

        if(state != null) {
            BlockState newState = block.getState();
            newState.setType(state.getType());
            newState.setData(state.getData());
            newState.update();
        }
    }
}
