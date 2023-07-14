package camp.pvp.parties;

import camp.pvp.utils.Colors;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Date;

@Getter
public class PartyInvite {

    private final Party party;
    private final Date expires;
    private final Player to, from;

    public PartyInvite(Party party, Player to, Player from) {
        this.party = party;
        this.to = to;
        this.from = from;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, 30);
        this.expires = calendar.getTime();
    }

    public void send() {
        from.sendMessage(ChatColor.GREEN + "You sent a party invite to " + ChatColor.WHITE + to.getName() + ChatColor.GREEN + ".");

        TextComponent msg = new TextComponent(Colors.get("&aYou received a party invite from &f" + from.getName() + "&e, click to accept."));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + from.getName()));
        msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Colors.get("&a/party join " + from.getName())).create()));
        to.spigot().sendMessage(msg);
    }

    public boolean isExpired() {
        return this.expires.before(new Date());
    }
}
