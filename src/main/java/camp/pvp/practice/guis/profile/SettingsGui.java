package camp.pvp.practice.guis.profile;

import camp.pvp.practice.cosmetics.DeathAnimation;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.Colors;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class SettingsGui extends StandardGui {
    public SettingsGui(GameProfile gameProfile) {
        super("&6Settings", 36);
        Player player = gameProfile.getPlayer();

        setDefaultBorder();

        GuiButton spectatorVisibility = new GuiButton(Material.REDSTONE_TORCH_ON, "&6&lSpectator Visibility");
        spectatorVisibility.setAction(new GuiAction() {
            @Override
            public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                gameProfile.setSpectatorVisibility(!gameProfile.isSpectatorVisibility());
                gameProfile.updatePlayerVisibility();
                gui.updateGui();
            }
        });

        spectatorVisibility.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore(
                        "&7Would you like to see other",
                        "&7spectators while spectating?",
                        " ",
                        "&aCurrent Setting: &f" + (gameProfile.isSpectatorVisibility() ? "Enabled" : "Disabled"));
            }
        });
        spectatorVisibility.setSlot(10);
        this.addButton(spectatorVisibility, false);

        GuiButton lobbyVisibility = new GuiButton(Material.EYE_OF_ENDER, "&e&lLobby Visibility");
        lobbyVisibility.setAction(new GuiAction() {
            @Override
            public void run(Player player, GuiButton button, Gui gui, ClickType click) {
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
                        " ",
                        "&aCurrent Setting: &f" + (gameProfile.isLobbyVisibility() ? "Enabled" : "Disabled"));
            }
        });
        lobbyVisibility.setSlot(11);
        this.addButton(lobbyVisibility, false);

        GuiButton comboMessages = new GuiButton(Material.EXP_BOTTLE, "&3&lCombo Messages and Sounds");
        comboMessages.setAction(new GuiAction() {
            @Override
            public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                gameProfile.setComboMessages(!gameProfile.isComboMessages());
                gui.updateGui();
            }
        });

        comboMessages.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore(
                        "&7Would you like to receive combo",
                        "&7messages and sounds?",
                        " ",
                        "&aCurrent Setting: &f" + (gameProfile.isComboMessages() ? "Enabled" : "Disabled"));
            }
        });
        comboMessages.setSlot(12);
        this.addButton(comboMessages, false);

        GuiButton tournamentMessages = new GuiButton(Material.DIAMOND_SWORD, "&4&lTournament Notifications");
        tournamentMessages.setAction(new GuiAction() {
            @Override
            public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                gameProfile.setTournamentNotifications(!gameProfile.isTournamentNotifications());
                gui.updateGui();
            }
        });

        tournamentMessages.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore(
                        "&7Would you like to receive tournament",
                        "&7updates in chat, even when you're",
                        "&7not in the tournament?",
                        " ",
                        "&aCurrent Setting: &f" + (gameProfile.isTournamentNotifications() ? "Enabled" : "Disabled"));
            }
        });
        tournamentMessages.setSlot(13);
        this.addButton(tournamentMessages, false);

        GuiButton playerTime = new GuiButton(Material.WATCH, "&5&lPlayer Time");
        playerTime.setAction(new GuiAction() {
            @Override
            public void run(Player player, GuiButton button, Gui gui, ClickType click) {
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
        playerTime.setSlot(14);
        this.addButton(playerTime, false);

        GuiButton sidebarVisibility = new GuiButton(Material.EMPTY_MAP, "&a&lSidebar Visibility");
        sidebarVisibility.setAction(new GuiAction() {
            @Override
            public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                gameProfile.setShowSidebar(!gameProfile.isShowSidebar());
                gui.updateGui();
            }
        });

        sidebarVisibility.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore(
                        "&7Would you to see your sidebar?",
                        " ",
                        "&aCurrent Setting: &f" + (gameProfile.isShowSidebar() ? "Enabled" : "Disabled"));
            }
        });
        sidebarVisibility.setSlot(15);
        this.addButton(sidebarVisibility, false);

        GuiButton hotbarSlot = new GuiButton(Material.GLASS_BOTTLE, "&a&lNo Drop Item Slot");
        hotbarSlot.setAction(new GuiAction() {
            @Override
            public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                new HotbarSlotGui(gameProfile).open(player);
            }
        });

        hotbarSlot.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore(
                        "&7What hotbar slot would you",
                        "&7like to block item dropping?",
                        " ",
                        "&aCurrent Setting: &f" + (gameProfile.getNoDropHotbarSlot() == -1 ? "None" : "Slot #" + (gameProfile.getNoDropHotbarSlot() + 1)),
                        " ",
                        "&7Click to customize your hotbar slot.");
            }
        });

        hotbarSlot.setSlot(16);
        this.addButton(hotbarSlot, false);

        GuiButton sidebarSettings = new GuiButton(Material.BOAT, "&d&lSidebar Settings");
        sidebarSettings.setAction(new GuiAction() {
            @Override
            public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                if(player.hasPermission("practice.cosmetics.sidebar_settings")) {
                    new SidebarSettingsGui(gameProfile).open(player);
                }
            }
        });

        sidebarSettings.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                if(player.hasPermission("practice.cosmetics.sidebar_settings")) {
                    guiButton.setLore("&7Click to customize your sidebar.");
                } else {
                    guiButton.setLore(
                            "&7This feature is only",
                            "&7available to donators.",
                            " ",
                            "&6Purchase a rank here: ",
                            "&fstore.pvp.camp");
                }
            }
        });
        sidebarSettings.setSlot(19);
        this.addButton(sidebarSettings, false);

        if(player.hasPermission("practice.staff.debug_mode")) {
            GuiButton debugMode = new GuiButton(Material.COMMAND, "&4&l&oDebug Mode");
            debugMode.setAction(new GuiAction() {
                @Override
                public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                    gameProfile.setDebugMode(!gameProfile.isDebugMode());
                    gui.updateGui();
                }
            });

            debugMode.setButtonUpdater(new AbstractButtonUpdater() {
                @Override
                public void update(GuiButton guiButton, Gui gui) {
                    guiButton.setLore(
                            "&7Would you like to enable",
                            "&7debug mode?",
                            " ",
                            "&aCurrent Setting: &f" + (gameProfile.isDebugMode() ? "Enabled" : "Disabled"));
                }
            });
            debugMode.setSlot(23);
            this.addButton(debugMode);
        }

        if(player.hasPermission("practice.staff.build_mode")) {
            GuiButton buildMode = new GuiButton(Material.GRASS, "&b&l&oBuild Mode");
            buildMode.setAction(new GuiAction() {
                @Override
                public void run(Player player, GuiButton button, Gui gui, ClickType click) {
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
                            " ",
                            "&aCurrent Setting: &f" + (gameProfile.isBuildMode() ? "Enabled" : "Disabled"));
                }
            });
            buildMode.setSlot(24);
            this.addButton(buildMode);
        }

        if(player.hasPermission("practice.staff")) {
            GuiButton staffMode = new GuiButton(Material.DIAMOND, "&d&l&oStaff Mode");
            staffMode.setAction(new GuiAction() {
                @Override
                public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                    gameProfile.setStaffMode(!gameProfile.isStaffMode());
                    gui.updateGui();
                }
            });

            staffMode.setButtonUpdater(new AbstractButtonUpdater() {
                @Override
                public void update(GuiButton guiButton, Gui gui) {
                    guiButton.setLore(
                            "&7Staff mode disables spectating",
                            "&7messages, hides you from other",
                            "&7spectators, and makes you hidden",
                            "&7in the lobby.",
                            " ",
                            "&aCurrent Setting: &f" + (gameProfile.isStaffMode() ? "Enabled" : "Disabled"));
                }
            });
            staffMode.setSlot(25);
            this.addButton(staffMode);
        }
    }
}
