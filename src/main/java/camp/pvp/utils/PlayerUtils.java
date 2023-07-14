package camp.pvp.utils;

import camp.pvp.Practice;
import camp.pvp.interactables.InteractableItem;
import camp.pvp.interactables.InteractableItems;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.InvocationTargetException;

public class PlayerUtils {

    public static void reset(Player player) {
        player.setFoodLevel(20);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setGameMode(GameMode.SURVIVAL);
        player.setLevel(0);
        player.setExp(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setWalkSpeed(0.2F);
        player.setFlySpeed(0.1F);
        player.setFireTicks(0);
        player.setSaturation(13);
        for(PotionEffect effect : player.getActivePotionEffects()){
            player.removePotionEffect(effect.getType());
        }
    }

    public static void giveInteractableItems(Player player) {
        PlayerInventory pi = player.getInventory();

        pi.clear();
        for(InteractableItems i : InteractableItems.getInteractableItems(Practice.instance.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId()))) {
            InteractableItem ii = i.getItem();
            pi.setItem(ii.getSlot(), ii.getItem().clone());
        }

        player.updateInventory();
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
