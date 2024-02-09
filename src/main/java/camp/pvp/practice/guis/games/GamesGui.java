package camp.pvp.practice.guis.games;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.paginated.PaginatedGui;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class GamesGui extends PaginatedGui {
    public GamesGui() {
        super("&6Active Games", 36);

        for(Game game : Practice.getInstance().getGameManager().getActiveGames()) {
            GuiButton button = new GuiButton(game.getKit().getIcon(), "&6&l" + game.getKit().getDisplayName() + " " + game.getClass().getSimpleName());

            List<String> lines = new ArrayList<>();
            lines.add("&6Spectating: &f" + game.getSpectators().size());
            lines.add("&6Arena: &f" + game.getArena().getDisplayName());

            if(game.getCurrentPlaying().size() < 8) {
                lines.add(" ");
                lines.add("&6Players:");
                for(GameParticipant p : game.getCurrentPlaying().values()) {
                    lines.add("&7> &f" + p.getName());
                }
            } else {
                lines.add("&6Players: &f" + game.getCurrentPlaying().size());
            }

            lines.add(" ");
            lines.add("&7Click to spectate.");

            button.setLore(lines);
            button.setAction(new GuiAction() {
                @Override
                public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                    GameProfile profile = Practice.getInstance().getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
                    if(profile.getState().equals(GameProfile.State.LOBBY) && game.getState().equals(Game.State.ACTIVE)) {
                        game.spectateStartRandom(player);
                        player.closeInventory();
                    } else {
                        player.sendMessage(ChatColor.RED + "You cannot spectate this game right now.");
                    }
                }
            });

            addButton(button, false);
        }
    }
}
