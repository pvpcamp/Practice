package camp.pvp.practice.utils;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.InvocationTargetException;

public class PlayerUtils {

    public static void reset(Player player, boolean allowFlight) {
        player.setFoodLevel(20);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setGameMode(GameMode.SURVIVAL);
        player.setLevel(0);
        player.setExp(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setWalkSpeed(0.2F);
        player.setFlySpeed(0.1F);
        player.setFireTicks(0);
        player.setSaturation(13);
        player.setFallDistance(0F);
        player.setItemOnCursor(null);
        player.closeInventory();
        player.eject();

        if(player.getPassenger() != null && player.getPassenger() instanceof Player) {
            Player passenger = (Player) player.getPassenger();
            passenger.eject();
        }

        for(PotionEffect effect : player.getActivePotionEffects()){
            player.removePotionEffect(effect.getType());
        }

        player.setAllowFlight(allowFlight);
        player.setFlying(allowFlight);
    }

    public static int getPing(Player player) {
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            return (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
