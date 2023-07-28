package camp.pvp.practice.guis.events;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.impl.events.SumoEvent;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HostEventGui extends StandardGui {

    public HostEventGui() {
        super("&6&lHost an Event", 27);

        this.setDefaultBackground();

        GuiButton sumoEvent = new GuiButton(Material.LEASH, "&6Sumo Event");
        sumoEvent.setCloseOnClick(true);
        sumoEvent.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
                SumoEvent event = new SumoEvent(Practice.instance, UUID.randomUUID());
                event.start();
            }
        });

        sumoEvent.setSlot(13);
        this.addButton(sumoEvent, false);
    }
}
