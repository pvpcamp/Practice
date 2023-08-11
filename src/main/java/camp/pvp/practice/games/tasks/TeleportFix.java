package camp.pvp.practice.games.tasks;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TeleportFix implements Runnable{

    private Game game;
    public TeleportFix(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        if(game.getState().equals(Game.State.STARTING)) {
            for(Player player : game.getCurrentPlayersPlaying()) {

                Location location = player.getLocation();
                Block block = location.getBlock();
                if(location.getBlock() != null) {
                    Material material = block.getType();
                    if(!material.equals(Material.AIR)) {
                        player.teleport(new Location(location.getWorld(), location.getX(), location.getY() + 1, location.getZ()));
                    }
                }
            }
        }
    }
}
