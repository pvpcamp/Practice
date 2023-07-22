package camp.pvp.practice.guis.profile;

import camp.pvp.practice.cosmetics.DeathAnimation;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.Colors;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import com.lunarclient.bukkitapi.LunarClientAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SettingsGui extends StandardGui {
    public SettingsGui(GameProfile gameProfile) {
        super("&6Settings", 36);
        Player player = gameProfile.getPlayer();

        this.setDefaultBackground();

        GuiButton spectatorVisibility = new GuiButton(Material.REDSTONE_TORCH_ON, "&6Spectator Visibility");
        spectatorVisibility.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
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
                        "&aCurrent Setting: &f" + (gameProfile.isSpectatorVisibility() ? "Enabled" : "Disabled"));
            }
        });
        spectatorVisibility.setSlot(10);
        this.addButton(spectatorVisibility, false);

        GuiButton lobbyVisibility = new GuiButton(Material.EYE_OF_ENDER, "&eLobby Visibility");
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
        this.addButton(lobbyVisibility, false);

        GuiButton comboMessages = new GuiButton(Material.EXP_BOTTLE, "&3Combo Messages and Sounds");
        comboMessages.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
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
                        "&aCurrent Setting: &f" + (gameProfile.isComboMessages() ? "Enabled" : "Disabled"));
            }
        });
        comboMessages.setSlot(12);
        this.addButton(comboMessages, false);

        GuiButton tournamentMessages = new GuiButton(Material.DIAMOND_SWORD, "&4Tournament Notifications");
        tournamentMessages.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
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
                        "&aCurrent Setting: &f" + (gameProfile.isTournamentNotifications() ? "Enabled" : "Disabled"));
            }
        });
        tournamentMessages.setSlot(13);
        this.addButton(tournamentMessages, false);

        GuiButton lunarCooldowns = new GuiButton(Material.BEACON, "&bLunar Cooldowns");
        lunarCooldowns.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
                gameProfile.setLunarCooldowns(!gameProfile.isLunarCooldowns());
                gui.updateGui();
            }
        });

        lunarCooldowns.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore(
                        "&7Would you like to utilize",
                        "&7Lunar Client cooldowns",
                        "&7instead of your XP bar?",
                        "&aCurrent Setting: &f" + (gameProfile.isLunarCooldowns() ? "Enabled (Recommended)" : "Disabled"));
            }
        });
        lunarCooldowns.setSlot(14);
        this.addButton(lunarCooldowns, false);

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
        playerTime.setSlot(15);
        this.addButton(playerTime, false);

        GuiButton deathAnimation = new GuiButton(Material.BLAZE_ROD, "&4Death Animation");
        deathAnimation.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
                if(player.hasPermission("practice.cosmetics.death_animation")) {
                    if (gameProfile.getDeathAnimation().ordinal() == DeathAnimation.values().length - 1) {
                        gameProfile.setDeathAnimation(DeathAnimation.DEFAULT);
                    } else {
                        gameProfile.setDeathAnimation(DeathAnimation.values()[gameProfile.getDeathAnimation().ordinal() + 1]);
                    }
                    gui.updateGui();
                } else {
                    player.sendMessage(Colors.get("&aThis feature is only available to players that have &5&lPlus Rank &aor higher." ));
                    player.sendMessage(Colors.get("&aIf you would like to support us, you can buy a rank here: &fstore.pvp.camp" ));
                }
            }
        });

        deathAnimation.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                DeathAnimation da = gameProfile.getDeathAnimation();
                guiButton.setType(da.getIcon().getType());
                guiButton.setLore(
                        "&7What would you like your",
                        "&7death animation to be?",
                        " ",
                        (da.equals(DeathAnimation.DEFAULT) ? "&6&l" : "&8") +" ● Default",
                        (da.equals(DeathAnimation.BLOOD) ? "&6&l" : "&8") +" ● Blood",
                        (da.equals(DeathAnimation.EXPLOSION) ? "&6&l" : "&8") +" ● Explosion");
            }
        });
        deathAnimation.setSlot(16);
        this.addButton(deathAnimation, false);

        GuiButton sidebarVisibility = new GuiButton(Material.EMPTY_MAP, "&aSidebar Visibility");
        sidebarVisibility.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
                gameProfile.setShowSidebar(!gameProfile.isShowSidebar());
                gui.updateGui();
            }
        });

        sidebarVisibility.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore(
                        "&7Would you to see your sidebar?",
                        "&aCurrent Setting: &f" + (gameProfile.isShowSidebar() ? "Enabled" : "Disabled"));
            }
        });
        sidebarVisibility.setSlot(19);
        this.addButton(sidebarVisibility, false);

        GuiButton sidebarSettings = new GuiButton(Material.BOAT, "&dSidebar Settings");
        sidebarSettings.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
                player.sendMessage(ChatColor.GREEN + "Coming soon!");
            }
        });

        sidebarSettings.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore("&7Click to customize your sidebar.");
            }
        });
        sidebarSettings.setSlot(20);
        this.addButton(sidebarSettings, false);

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
            debugMode.setSlot(35);
            this.addButton(debugMode, false);
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
            buildMode.setSlot(34);
            this.addButton(buildMode, false);
        }
    }
}
