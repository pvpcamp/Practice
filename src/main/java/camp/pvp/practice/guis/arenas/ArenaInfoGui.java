package camp.pvp.practice.guis.arenas;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaManager;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.practice.utils.Colors;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class ArenaInfoGui extends ArrangedGui {
    public ArenaInfoGui(ArenaManager arenaManager, Arena arena) {
        super("&6" + arena.getDisplayName() + " Info");

        setDefaultBorder();

        GuiButton list = new GuiButton(Material.BOOK, "&c&lGo to Arena List");
        list.setSlot(0);
        list.setAction((player, button, gui, click) -> {
            new ArenaListGui(arenaManager, arena.getType()).open(player);
        });

        list.setOverrideGuiArrangement(true);
        addButton(list);

        boolean hasCopies = !arenaManager.getArenaCopies(arena).isEmpty();

        GuiButton info = new GuiButton(Material.NETHER_STAR, "&6&l" + arena.getDisplayName());

        List<String> infoLore = new ArrayList<>();
        infoLore.add("&6Name: &f" + arena.getName());
        infoLore.add("&6In Use: &f" + (arena.isInUse() ? "Yes" : "No"));

        if(arena.getType().isBuild()) {
            infoLore.add("&6Build Limit: &f" + arena.getBuildLimit());
        }

        infoLore.add("&6Void Level: &f" + arena.getVoidLevel());
        infoLore.add(" ");
        infoLore.add("&6Rename:");
        infoLore.add("&f/arena rename " + arena.getName() + " <new name>");
        infoLore.add(" ");
        infoLore.add("&6Update Display Name:");
        infoLore.add("&f/arena displayname " + arena.getName() + " <display name>");
        info.setLore(infoLore);
        addButton(info);

        GuiButton type = new GuiButton(arena.getType().getGuiMaterial(), "&d&lSelected Type");
        List<String> typeLore = new ArrayList<>();
        typeLore.add("&6Current Type: &f" + arena.getType());
        if(!arena.isCopy()) {
            typeLore.add(" ");
            typeLore.add("&7Click to customize type.");
            type.setAction((player, button, gui, click) -> {
                new ArenaTypeGui(arenaManager, arena).open(player);
            });
        }

        type.setLore(typeLore);
        addButton(type);

        GuiButton copies = new GuiButton(Material.PAPER, "&e&lCopies");
        copies.setButtonUpdater((button, gui) -> {

            if(!arena.getType().isBuild()) {
                button.setLore("&cThis arena type does not need copies.");
                button.setType(Material.GLASS);
                return;
            }

            if(arena.isCopy()) {
                button.setLore(
                        "&7This arena is a copy.",
                        " ",
                        "&cRight Click to reset arena."
                );
                button.setType(Material.COBBLESTONE);
            } else {
                button.setLore(
                        "&6Current Copies: &f" + arenaManager.getArenaCopies(arena).size(),
                        " ",
                        "&aLeft Click to view copies.",
                        "&eMiddle Click to run arena scanner.",
                        "&cRight Click to update copies.");
                button.setType(Material.BOOKSHELF);
            }
        });

        copies.setAction((player, button, gui, click) -> {
            if(arena.isCopy() && click.isRightClick()) {
                arena.resetArena();
                player.sendMessage(Colors.get("&aReset arena &f" + arena.getName() + "&a."));
                return;
            }

            if(!arena.isCopy() && !arenaManager.getArenaCopies(arena).isEmpty()) {
                if(click.isLeftClick()) {
                    new ArenaCopiesGui(arenaManager, arena).open(player);
                    return;
                }

                if(click.equals(ClickType.MIDDLE)) {
                    arena.scanArena();
                    player.sendMessage(Colors.get("&aScanned arena &f" + arena.getName() + "&a."));
                    return;
                }

                if(click.isRightClick()) {
                    arenaManager.updateArenaCopies(arena, true);
                    player.sendMessage(Colors.get("&aUpdated " + arenaManager.getArenaCopies(arena).size() + " copy's positions, blocks, and other values for arena &f" + arena.getName() + "&a."));

                }
            }
        });

        addButton(copies);

        GuiButton positions = new GuiButton(Material.BEACON, "&c&lPositions");

        List<String> positionsLore = new ArrayList<>();

        for(String position : arena.getType().getValidPositions()) {
            ArenaPosition arenaPosition = arena.getPositions().get(position);
            boolean validPosition = arenaPosition != null;

            positionsLore.add((validPosition ? " &a✓" : " &c✗") + " " + position);
        }

        positionsLore.add(" ");
        positionsLore.add(arena.hasValidPositions() ? "&aAll positions are set." : "&cNot all positions are set!");
        positionsLore.add(" ");
        positionsLore.add("&aLeft click to teleport.");

        positionsLore.add(arena.isCopy() ? "&4You cannot change copy positions." : "&cRight click to customize.");

        positions.setLore(positionsLore);

        positions.setAction((player, button, gui, click) -> {
            if(click.isRightClick() && !arena.isCopy()) {
                new ArenaPositionsGui(arena).open(player);
            } else {
                new ArenaTeleportGui(arena).open(player);
            }
        });

        addButton(positions);

        if(arena.getType().isGenerateLoot()) {
            GuiButton loot = new GuiButton(Material.CHEST, "&6&lLoot");
            loot.setLore(
                    "&6Chests: &f" + arena.getLootChests().size(),
                    " ",
                    "&7Click to view loot chests."
            );

            loot.setAction((player, button, gui, click) -> {
                new ArenaLootGui(arena).open(player);
            });

            addButton(loot);
        }

        if(arena.getType().isRandomSpawnLocation()) {
            GuiButton randomSpawns = new GuiButton(Material.ENDER_PEARL, "&5&lRandom Spawns");
            randomSpawns.setLore(
                    "&6Current Random Spawns: &f" + arena.getRandomSpawnLocations().size(),
                    " ",
                    "&7Click to view random spawns."
            );

            randomSpawns.setAction((player, button, gui, click) -> {
                new ArenaRandomSpawnsGui(arenaManager, arena).open(player);
            });

            addButton(randomSpawns);
        }

        GuiButton queueable = new GuiButton(Material.REDSTONE, "&a&lQueueable");

        queueable.setButtonUpdater((button, gui) -> {
            button.setLore(
                    "&7Queueable arenas are also",
                    "&7used for duel requests.",
                    " ",
                    "&aCurrent Setting: &f" + (arena.isEnabled() ? "Yes" : "No"));
            button.setType(arena.isEnabled() ? Material.EMERALD : Material.REDSTONE);
        });

        queueable.setAction((player, button, gui, click) -> {
            if(!arena.isEnabled() && !arena.hasValidPositions()) {
                player.sendMessage(Colors.get("&cYou must set all positions before enabling the arena."));
                return;
            }

            arena.setEnabled(!arena.isEnabled());
            gui.updateGui();
        });

        addButton(queueable);

        GuiButton delete = new GuiButton(Material.TNT, "&4&lDelete Arena");

        List<String> deleteLore = new ArrayList<>();

        if(hasCopies) {
            deleteLore.add("&cThis will also delete");
            deleteLore.add("&c" + arenaManager.getArenaCopies(arena).size() + " copies of this arena.");
            deleteLore.add(" ");
        }

        deleteLore.add("&7Click to delete this arena.");
        delete.setLore(deleteLore);
        delete.setAction((player, button, gui, click) -> {
            StandardGui confirm = new StandardGui("&4&lConfirm Deletion", 9);
            confirm.setDefaultBorder();

            GuiButton confirmButton = new GuiButton(Material.TNT, "&4&lConfirm Deletion");

            if(hasCopies) {
                confirmButton.setLore(
                        "&7This process will also delete",
                        "&7" + arenaManager.getArenaCopies(arena).size() + " copies of this arena.",
                        "&7The copied arena's blocks will",
                        "&7be set to air. This could lag!",
                        " ",
                        "&cThis action cannot be undone.");
            } else {
                confirmButton.setLore("&cThis action cannot be undone.");
            }

            confirmButton.setSlot(3);
            confirmButton.setCloseOnClick(true);
            confirmButton.setAction((p, b, g, c) -> {
                arenaManager.deleteArena(arena);
                p.sendMessage(Colors.get("&aDeleted arena &f" + arena.getName() + "&a" + (hasCopies ? " and its " + arenaManager.getArenaCopies(arena).size() + " copies" : "") +"."));
            });
            confirm.addButton(confirmButton);

            GuiButton cancelButton = new GuiButton(Material.ARROW, "&c&lGo Back");
            cancelButton.setAction((p, b, g, c) -> {
                gui.open(p);
            });
            cancelButton.setSlot(5);
            confirm.addButton(cancelButton);

            confirm.open(player);
        });
        addButton(delete);

    }
}
