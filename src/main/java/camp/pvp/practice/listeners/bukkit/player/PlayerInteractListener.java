package camp.pvp.practice.listeners.bukkit.player;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.cooldowns.PlayerCooldown;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.interactables.InteractableItem;
import camp.pvp.practice.interactables.InteractableItems;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.kits.CustomDuelKit;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.*;

import java.util.Map;

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
        Action action = event.getAction();
        Block block = event.getClickedBlock();

        ItemStack item = event.getItem();

        if(event.getAction().equals(Action.PHYSICAL)){
            Material mat = block.getType();
            if(mat == Material.STONE_PLATE || mat == Material.WOOD_PLATE || mat == Material.IRON_PLATE || mat == Material.GOLD_PLATE) {
                event.setCancelled(true);
                event.setUseInteractedBlock(Event.Result.DENY);
            }
            return;
        }

        if(profile.isBuildMode() && item != null && item.getType().equals(Material.GOLD_AXE)) {

            if(block == null || block.getType().equals(Material.AIR)) {
                Location location = player.getLocation();
                profile.setSelectedLocation(location);
                player.sendMessage(ChatColor.GREEN + "Updated arena position selection to your current location. "
                        + ChatColor.GRAY + "(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")");
            } else {

                profile.setSelectedLocation(block.getLocation());

                player.sendMessage(ChatColor.GREEN + "Updated arena position selection. " + ChatColor.GRAY + "(" + block.getX() + ", " + block.getY() + ", " + block.getZ() + ")");

            }
            event.setCancelled(true);
            return;
        }

        if(action.equals(Action.LEFT_CLICK_AIR)) {
            profile.addClick();
        }

        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if(profile.isBuildMode()) {
                return;
            }

            for(InteractableItems i : InteractableItems.getInteractableItems(profile)) {
                InteractableItem ii = i.getItem();
                if(ii != null && ii.getSlot() == player.getInventory().getHeldItemSlot()) {
                    ii.getInteract().onInteract(player, profile);
                    event.setCancelled(true);
                    return;
                }
            }

            Game game = profile.getGame();
            if (game != null) {
                GameParticipant participant = game.getCurrentPlaying().get(player.getUniqueId());
                if(participant != null) {
                    if(participant.isKitApplied()) {

                        switch(player.getItemInHand().getType()) {
                            case MUSHROOM_SOUP:
                                if(player.getHealth() != player.getMaxHealth()) {
                                    double d = player.getMaxHealth() - player.getHealth() < 7 ? player.getMaxHealth() - player.getHealth() : 7;
                                    player.setHealth(player.getHealth() + d);
                                    player.setFoodLevel(20);
                                    player.setSaturation(20);
                                    item.setType(Material.BOWL);
                                    event.setCancelled(true);
                                }
                                break;
                        }
                    } else {
                        GameKit kit = game.getKit();
                        switch(player.getItemInHand().getType()) {
                            case ENCHANTED_BOOK:
                                int slot = player.getInventory().getHeldItemSlot() + 1;
                                CustomDuelKit cdk = profile.getCustomDuelKits().get(kit).get(slot);
                                if(cdk != null) {
                                    cdk.apply(participant);
                                    participant.setAppliedCustomKit(cdk);
                                    player.updateInventory();
                                }
                                break;
                            case BOOK:
                                kit.apply(participant);
                                player.updateInventory();
                                break;
                        }
                    }

                    if(game.getKit().isIssueCooldowns()) {
                        switch (player.getItemInHand().getType()) {
                            case ENDER_PEARL:
                                PlayerCooldown cooldown = participant.getCooldowns().get(PlayerCooldown.Type.ENDER_PEARL);
                                if (cooldown != null) {
                                    if (!cooldown.isExpired()) {
                                        player.sendMessage(cooldown.getBlockedMessage());
                                        event.setCancelled(true);
                                        player.updateInventory();
                                    }
                                }
                                break;
                        }
                    }

                    if(block != null) {
                        if (game.getState().equals(Game.State.ACTIVE)) {
                            event.setCancelled(isCancelled(block, game.getArena()));
                        }
                    }
                } else {
                    event.setCancelled(true);
                }
            } else if(profile.getState().equals(GameProfile.State.KIT_EDITOR)) {
                if(block != null) {
                    GameKit editingKit = profile.getEditingKit();
                    switch (block.getType()) {
                        case CHEST:
                            if(editingKit.getMoreItems() != null) {
                                Inventory inventory = Bukkit.createInventory(player, 36, "More Items");
                                for(ItemStack i : editingKit.getMoreItems()) {
                                    if(i != null && !i.getType().equals(Material.AIR)) {
                                        inventory.addItem(i);
                                    }
                                }

                                player.openInventory(inventory);
                            }
                            break;
                        case ANVIL:
                            StandardGui gui = new StandardGui("Editing " + editingKit.getDisplayName(), 36);
                            Map<Integer, CustomDuelKit> customKits = profile.getCustomDuelKits().get(editingKit);
                            int x = 1;
                            while (x < 6) {
                                CustomDuelKit cdk = customKits.get(x);
                                if (cdk == null) {
                                    GuiButton createButton = new GuiButton(Material.CHEST, "&6Create new " + editingKit.getDisplayName() + " kit.");
                                    createButton.setSlot(1 + x);
                                    createButton.setCloseOnClick(true);

                                    int finalX = x;
                                    createButton.setAction(new GuiAction() {
                                        @Override
                                        public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                                            CustomDuelKit cdk = new CustomDuelKit(editingKit, finalX, false);
                                            cdk.setItems(player.getInventory().getContents());
                                            customKits.put(finalX, cdk);
                                            player.sendMessage(Colors.get("&aYour &f" + editingKit.getDisplayName() + "&a has been created and saved as " + cdk.getName() + "&a."));
                                        }
                                    });

                                    gui.addButton(createButton, false);
                                } else {
                                    GuiButton loadButton = new GuiButton(Material.BOOK, "&aLoad Kit " + cdk.getName());
                                    loadButton.setSlot(1 + x);
                                    loadButton.setAction(new GuiAction() {
                                        @Override
                                        public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                                            player.getInventory().clear();
                                            cdk.apply(player);
                                            player.getInventory().setArmorContents(null);
                                            player.sendMessage(Colors.get("&aCustom kit " + cdk.getName() + " &ahas been loaded."));
                                        }
                                    });
                                    gui.addButton(loadButton, false);

                                    GuiButton saveButton = new GuiButton(Material.NETHER_STAR, "&6Save Kit " + cdk.getName());
                                    saveButton.setSlot(10 + x);
                                    saveButton.setCloseOnClick(true);
                                    saveButton.setAction(new GuiAction() {
                                        @Override
                                        public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                                            cdk.setItems(player.getInventory().getContents());
                                            player.sendMessage(Colors.get("&aCustom kit " + cdk.getName() + " &ahas been saved."));
                                        }
                                    });

                                    gui.addButton(saveButton, false);

                                    GuiButton renameButton = new GuiButton(Material.NAME_TAG, "&9Rename Kit " + cdk.getName());
                                    renameButton.setSlot(19 + x);
                                    renameButton.setCloseOnClick(true);
                                    renameButton.setAction(new GuiAction() {
                                        @Override
                                        public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                                            profile.setEditingCustomKit(cdk);
                                            player.sendMessage(ChatColor.GREEN + "Type the desired name of your kit in chat.");
                                        }
                                    });
                                    gui.addButton(renameButton, false);

                                    GuiButton deleteButton = new GuiButton(Material.TNT, "&cDelete Kit " + cdk.getName());
                                    deleteButton.setSlot(28 + x);
                                    deleteButton.setCloseOnClick(true);
                                    int finalX = x;
                                    deleteButton.setAction(new GuiAction() {
                                        @Override
                                        public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                                            customKits.remove(finalX);
                                        }
                                    });
                                    gui.addButton(deleteButton, false);
                                }
                                x++;
                            }

                            gui.open(player);
                            break;
                        case SIGN:
                        case WALL_SIGN:
                            profile.setEditingKit(null);
                            profile.setEditingCustomKit(null);
                            profile.playerUpdate(true);
                            break;
                    }
                }

                event.setCancelled(true);
            } else {
                event.setCancelled(true);
            }
        }
    }

    public boolean isCancelled(Block block, Arena arena) {

        BlockState state = block.getState();

        if(arena != null && arena.getType().canModifyArena()) {
            return false;
        } else {
            if (state instanceof Chest) {
                return true;
            } else if (state instanceof Lever) {
                return true;
            } else if (state instanceof Button) {
                return true;
            } else if (state instanceof PressurePlate) {
                return true;
            } else if (state instanceof Furnace) {
                return true;
            } else if (state instanceof Gate) {
                return true;
            } else return state instanceof TrapDoor;
        }
    }
}
