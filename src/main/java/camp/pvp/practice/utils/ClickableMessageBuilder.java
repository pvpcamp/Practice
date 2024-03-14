package camp.pvp.practice.utils;

import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;

public class ClickableMessageBuilder {

    private String[] messages;
    private String command, hoverMessage;

    public ClickableMessageBuilder() {
        this.messages = new String[0];
    }

    public ClickableMessageBuilder setLines(String... lines) {
        this.messages = lines;
        return this;
    }

    public ClickableMessageBuilder setCommand(String command) {
        this.command = command;
        return this;
    }

    public ClickableMessageBuilder setHoverMessage(String hoverMessage) {
        this.hoverMessage = hoverMessage;
        return this;
    }

    public BaseComponent[] build() {
        BaseComponent[] components = new BaseComponent[messages.length];
        for (int i = 0; i < messages.length; i++) {
            TextComponent msg = new TextComponent(Colors.get(messages[i]));

            if (command != null) {
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
            }

            if (hoverMessage != null) {
                msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Colors.get(hoverMessage)).create()));
            }

            components[i] = msg;
        }

        return components;
    }

    public void sendToPlayer(Player player) {
        for(BaseComponent component : build()) {
            player.spigot().sendMessage(component);
        }
    }
}
