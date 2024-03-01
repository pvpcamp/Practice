package camp.pvp.practice.guis.profile;

import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class SidebarSettingsGui extends StandardGui {
    public SidebarSettingsGui(GameProfile profile) {
        super("&6Sidebar Settings", 27);

        setDefaultBorder();

        GuiButton visibilityInGame = new GuiButton(Material.DIAMOND_SWORD, "&a&lVisible In Game");
        visibilityInGame.setAction(new GuiAction() {
            @Override
            public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                profile.setSidebarInGame(!profile.isSidebarInGame());
                gui.updateGui();
            }
        });

        visibilityInGame.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore(
                        "&7Would you like to see your",
                        "&7sidebar during a game?",
                        " ",
                        "&aCurrent Setting: &f" + (profile.isSidebarInGame() ? "Enabled" : "Disabled"));
            }
        });
        visibilityInGame.setSlot(11);
        addButton(visibilityInGame);

        GuiButton sidebarShowDuration = new GuiButton(Material.WATCH, "&a&lShow Game Duration");
        sidebarShowDuration.setAction(new GuiAction() {
            @Override
            public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                profile.setSidebarShowDuration(!profile.isSidebarShowDuration());
                gui.updateGui();
            }
        });

        sidebarShowDuration.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore(
                        "&7Would you like the see the",
                        "&7duration of the game you're in?",
                        " ",
                        "&aCurrent Setting: &f" + (profile.isSidebarShowDuration() ? "Enabled" : "Disabled"));
            }
        });
        sidebarShowDuration.setSlot(12);
        addButton(sidebarShowDuration);

        GuiButton sidebarShowCps = new GuiButton(Material.LEASH, "&a&lShow CPS");
        sidebarShowCps.setAction(new GuiAction() {
            @Override
            public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                profile.setSidebarShowCps(!profile.isSidebarShowCps());
                gui.updateGui();
            }
        });

        sidebarShowCps.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore(
                        "&7Would you like the see the",
                        "&7CPS of players in the game?",
                        " ",
                        "&aCurrent Setting: &f" + (profile.isSidebarShowCps() ? "Enabled" : "Disabled"));
            }
        });
        sidebarShowCps.setSlot(13);
        addButton(sidebarShowCps);

        GuiButton sidebarShowLines = new GuiButton(Material.EMPTY_MAP, "&a&lShow Lines");
        sidebarShowLines.setAction(new GuiAction() {
            @Override
            public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                profile.setSidebarShowLines(!profile.isSidebarShowLines());
                gui.updateGui();
            }
        });

        sidebarShowLines.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore(
                        "&7Would you like the see the",
                        "&7lines on the sidebar?",
                        " ",
                        "&aCurrent Setting: &f" + (profile.isSidebarShowLines() ? "Enabled" : "Disabled"));
            }
        });
        sidebarShowLines.setSlot(14);
        addButton(sidebarShowLines);

        GuiButton sidebarShowPing = new GuiButton(Material.BEACON, "&a&lShow Ping");
        sidebarShowPing.setAction(new GuiAction() {
            @Override
            public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                profile.setSidebarShowPing(!profile.isSidebarShowPing());
                gui.updateGui();
            }
        });

        sidebarShowPing.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore(
                        "&7Would you like the see",
                        "&7ping on the sidebar?",
                        " ",
                        "&aCurrent Setting: &f" + (profile.isSidebarShowPing() ? "Enabled" : "Disabled"));
            }
        });
        sidebarShowPing.setSlot(15);
        addButton(sidebarShowPing);

        GuiButton back = new GuiButton(Material.ARROW, "&c&lBack");
        back.setAction(new GuiAction() {
            @Override
            public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                new SettingsGui(profile).open(player);
            }
        });
        back.setSlot(0);
        addButton(back);
    }
}
