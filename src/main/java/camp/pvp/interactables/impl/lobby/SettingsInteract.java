package camp.pvp.interactables.impl.lobby;

import camp.pvp.interactables.ItemInteract;
import camp.pvp.profiles.GameProfile;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SettingsInteract implements ItemInteract {

    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        StandardGui gui = new StandardGui("Settings", 27);
        gui.setDefaultBackground();

        GuiButton spectatorVisibility = new GuiButton(Material.REDSTONE_TORCH_ON, "&6Spectator Visibility");
        spectatorVisibility.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
                gameProfile.setSpectatorVisibility(!gameProfile.isSpectatorVisibility());
                gui.updateGui();
            }
        });

        spectatorVisibility.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore(
                        "&7Would you like to see other",
                        "&7spectators while spectating?",
                        "&aCurrent Setting: &f" + (gameProfile.isSpectatorVisibility() ? "Enabled" : "Disabled"));
            }
        });
        spectatorVisibility.setSlot(10);
        gui.addButton(spectatorVisibility, false);

        GuiButton lobbyVisibility = new GuiButton(Material.TORCH, "&eLobby Visibility");
        lobbyVisibility.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
                gameProfile.setLobbyVisibility(!gameProfile.isLobbyVisibility());
                gameProfile.updatePlayerVisibility();
                gui.updateGui();
            }
        });

        lobbyVisibility.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore(
                        "&7Would you like to see other",
                        "&7players in the lobby?",
                        "&aCurrent Setting: &f" + (gameProfile.isLobbyVisibility() ? "Enabled" : "Disabled"));
            }
        });
        lobbyVisibility.setSlot(11);
        gui.addButton(lobbyVisibility, false);

        GuiButton playerTime = new GuiButton(Material.WATCH, "&5Player Time");
        playerTime.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
                if(gameProfile.getTime().ordinal() == GameProfile.Time.NIGHT.ordinal()) {
                    gameProfile.setTime(GameProfile.Time.DAY);
                } else {
                    gameProfile.setTime(GameProfile.Time.values()[gameProfile.getTime().ordinal() + 1]);
                }

                player.setPlayerTime(gameProfile.getTime().getTime(), false);
                gui.updateGui();
            }
        });

        playerTime.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                GameProfile.Time time = gameProfile.getTime();
                guiButton.setLore(
                        "&7What time of day would",
                        "&7you like to have set?",
                        " ",
                        (time.equals(GameProfile.Time.DAY) ? "&6&l" : "&8") +" ● Day",
                        (time.equals(GameProfile.Time.SUNSET) ? "&6&l" : "&8") +" ● Sunset",
                        (time.equals(GameProfile.Time.NIGHT) ? "&6&l" : "&8") +" ● Night");
            }
        });
        playerTime.setSlot(12);
        gui.addButton(playerTime, false);

        if(player.hasPermission("practice.staff.debug_mode")) {
            GuiButton debugMode = new GuiButton(Material.COMMAND, "&c&l&oDebug Mode");
            debugMode.setAction(new GuiAction() {
                @Override
                public void run(Player player, Gui gui) {
                    gameProfile.setDebugMode(!gameProfile.isDebugMode());
                    gui.updateGui();
                }
            });

            debugMode.setButtonUpdater(new AbstractButtonUpdater() {
                @Override
                public void update(GuiButton guiButton, Gui gui) {
                    guiButton.setLore(
                            "&7If you see this, you should ",
                            "&7know what this does.",
                            "&aCurrent Setting: &f" + (gameProfile.isDebugMode() ? "Enabled" : "Disabled"));
                }
            });
            debugMode.setSlot(26);
            gui.addButton(debugMode, false);
        }

        if(player.hasPermission("practice.staff.build_mode")) {
            GuiButton buildMode = new GuiButton(Material.GRASS, "&b&lBuild Mode");
            buildMode.setAction(new GuiAction() {
                @Override
                public void run(Player player, Gui gui) {
                    gameProfile.setBuildMode(!gameProfile.isBuildMode());
                    gui.updateGui();
                }
            });

            buildMode.setButtonUpdater(new AbstractButtonUpdater() {
                @Override
                public void update(GuiButton guiButton, Gui gui) {
                    guiButton.setLore(
                            "&7Would you like to enable",
                            "&7build mode?",
                            "&aCurrent Setting: &f" + (gameProfile.isBuildMode() ? "Enabled" : "Disabled"));
                }
            });
            buildMode.setSlot(25);
            gui.addButton(buildMode, false);
        }

        gui.open(player);
    }
}
