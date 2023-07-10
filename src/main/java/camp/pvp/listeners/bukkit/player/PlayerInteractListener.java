package camp.pvp.listeners.bukkit.player;

import camp.pvp.Practice;
import camp.pvp.cooldowns.PlayerCooldown;
import camp.pvp.games.Game;
import camp.pvp.games.GameParticipant;
import camp.pvp.interactables.InteractableItem;
import camp.pvp.interactables.InteractableItems;
import camp.pvp.profiles.GameProfile;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.*;

public class PlayerInteractListener implements Listener {

    private Practice plugin;
    public PlayerInteractListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        GameProfile.State state = profile.getState();
        Action action = event.getAction();
        Block block = event.getClickedBlock();

        ItemStack item = event.getItem();

        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            for(InteractableItems i : InteractableItems.values()) {
                InteractableItem ii = i.getItem();
                if(i.getState().equals(state) && ii.getItem().isSimilar(item)) {
                    ii.getInteract().onInteract(player, profile);
                    event.setCancelled(true);
                    return;
                }
            }

            Game game = profile.getGame();
            if (game != null) {
                GameParticipant participant = game.getAlive().get(player.getUniqueId());
                switch(player.getItemInHand().getType()) {
                    case ENDER_PEARL:
                        PlayerCooldown cooldown = participant.getCooldowns().get(PlayerCooldown.Type.ENDER_PEARL);
                        if(cooldown != null){
                            if(!cooldown.isExpired()) {
                                player.sendMessage(cooldown.getBlockedMessage());
                                player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
                            }
                        }
                        break;
//                    case ENCHANTED_BOOK:
//                        if(participant != null && !participant.isKitApplied()) {
//                            int slot = player.getInventory().getHeldItemSlot();
//                            CustomKit customKit = profile.getCustomKits().get(game.getKit().getUuid()).get(slot + 1);
//                            if(customKit != null) {
//                                customKit.apply(player, true);
//                                game.getKit().applyArmor(player);
//                                participant.setKitApplied(true);
//                            }
//                        }
//                        break;
//                    case BOOK:
//                        if(participant != null && !participant.isKitApplied()) {
//                            game.getKit().apply(player);
//                            participant.setKitApplied(true);
//                        }
//                        break;
                }

                if(profile.getGame().getCurrentPlaying().contains(player)) {
                    if(block != null) {
                        BlockState blockState = block.getState();
                        MaterialData data = blockState.getData();
                        if (data instanceof Door) {
                            event.setCancelled(true);
                        } else if (data instanceof TrapDoor) {
                            event.setCancelled(true);
                        } else if (data instanceof Gate) {
                            event.setCancelled(true);
                        } else if(data instanceof Lever) {
                            event.setCancelled(true);
                        }
                    }
                } else {
                    event.setCancelled(true);
                }
            } else if(!profile.isBuildMode()) {
                event.setCancelled(true);
            }
        }
    }
}
