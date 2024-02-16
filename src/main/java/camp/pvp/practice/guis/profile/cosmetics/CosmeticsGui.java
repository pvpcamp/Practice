package camp.pvp.practice.guis.profile.cosmetics;

import camp.pvp.core.Core;
import camp.pvp.core.guis.cosmetics.FlightEffectsGui;
import camp.pvp.core.guis.cosmetics.LobbyArmorGui;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.profiles.FlightEffect;
import camp.pvp.core.profiles.LobbyArmor;
import camp.pvp.practice.Practice;
import camp.pvp.practice.cosmetics.DeathAnimation;
import camp.pvp.practice.guis.profile.MyProfileGui;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.Colors;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class CosmeticsGui extends StandardGui {
    public CosmeticsGui(GameProfile profile) {
        super("&6Cosmetics", 27);

        setDefaultBorder();

        CoreProfile coreProfile = Core.getApi().getLoadedProfile(profile.getUuid());

        GuiButton myProfile = new GuiButton(Material.SKULL_ITEM, "&6&lGo to My Profile");
        myProfile.setDurability((short) 3);
        SkullMeta meta = (SkullMeta) myProfile.getItemMeta();
        meta.setOwner(profile.getName());
        myProfile.setItemMeta(meta);

        myProfile.setAction((p, b, g, click) -> {
            new MyProfileGui(profile).open(p);
        });

        myProfile.setSlot(0);
        myProfile.setOverrideGuiArrangement(true);
        addButton(myProfile);

        GuiButton lobbyArmor = new GuiButton(Material.DIAMOND_CHESTPLATE, "&6&lLobby Armor");
        lobbyArmor.setLore("&7Click to customize your lobby armor.", " ", "&6Currently Applied: &f" + coreProfile.getAppliedLobbyArmor().toString());

        lobbyArmor.setAction((player, button, gui, click) -> {
            LobbyArmorGui lobbyArmorGui = new LobbyArmorGui(player);

            GuiButton back = new GuiButton(Material.ARROW, "&c&lBack");
            back.setAction((p, b, g, c) -> this.open(player));
            back.setLore("&7Click to return to", "&7the cosmetics menu.");
            back.setOverrideGuiArrangement(true);
            back.setSlot(0);
            lobbyArmorGui.addButton(back);

            lobbyArmorGui.setCloseAction((p, g) -> {
                if(profile.getState().isLobby()) {
                    p.getInventory().setArmorContents(coreProfile.getAppliedLobbyArmor().getArmor());
                }
            });

            lobbyArmorGui.open(player);
        });

        lobbyArmor.setSlot(11);
        addButton(lobbyArmor);

        GuiButton flightEffect = new GuiButton(Material.FEATHER, "&6&lFlight Effects");
        flightEffect.setLore("&7Click to customize your flight effect.", " ", "&6Currently Applied: &f" + coreProfile.getAppliedFlightEffect().toString());

        flightEffect.setAction((player, button, gui, click) -> {
            FlightEffectsGui flightEffectsGui = new FlightEffectsGui(player);

            GuiButton back = new GuiButton(Material.ARROW, "&c&lBack");
            back.setAction((p, b, g, c) -> this.open(player));
            back.setLore("&7Click to return to", "&7the cosmetics menu.");
            back.setOverrideGuiArrangement(true);
            back.setSlot(0);
            flightEffectsGui.addButton(back);

            flightEffectsGui.open(player);
        });
        flightEffect.setSlot(13);
        addButton(flightEffect);

        GuiButton deathAnimation = new GuiButton(profile.getDeathAnimation().getIcon().getType(), "&4&lDeath Animation");
        deathAnimation.setAction(new GuiAction() {
            @Override
            public void run(Player player, GuiButton button, Gui gui, ClickType click) {

                if(!player.hasPermission("practice.cosmetics.death_animation")) {
                    player.sendMessage(Colors.get("&aThis feature is only available to players that have &5&lPlus Rank &aor higher." ));
                    player.sendMessage(Colors.get("&aIf you would like to support us, you can buy a rank here: &fstore.pvp.camp" ));
                    return;
                }

                if (profile.getDeathAnimation().ordinal() == DeathAnimation.values().length - 1) {
                    profile.setDeathAnimation(DeathAnimation.DEFAULT);
                } else {
                    profile.setDeathAnimation(DeathAnimation.values()[profile.getDeathAnimation().ordinal() + 1]);
                }

                gui.updateGui();
            }
        });

        deathAnimation.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                DeathAnimation da = profile.getDeathAnimation();
                guiButton.setType(da.getIcon().getType());

                List<String> lore = new ArrayList<>();
                lore.add("&7What would you like your");
                lore.add("&7death animation to be?");
                lore.add(" ");

                for(DeathAnimation deathAnimation : DeathAnimation.values()) {
                    lore.add((da.equals(deathAnimation) ? "&6&l" : "&8") +" ● " + deathAnimation);
                }

                guiButton.setLore(lore);
            }
        });
        deathAnimation.setSlot(15);
        this.addButton(deathAnimation, false);
    }
}
