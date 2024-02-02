package camp.pvp.practice.utils;

import camp.pvp.practice.interactables.InteractableItems;
import camp.pvp.practice.Practice;
import camp.pvp.practice.interactables.InteractableItem;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
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

    public static void giveInteractableItems(Player player) {
        PlayerInventory pi = player.getInventory();
        GameProfile profile = Practice.instance.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

        pi.clear();
        for(InteractableItems i : InteractableItems.getInteractableItems(Practice.instance.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId()))) {
            InteractableItem ii = i.getItem();

            if(ii.getItemUpdater() != null) {
                ii.getItemUpdater().onUpdate(ii, profile);
            }

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
