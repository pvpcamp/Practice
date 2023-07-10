package camp.pvp.profiles;

import camp.pvp.Practice;
import camp.pvp.arenas.Arena;
import camp.pvp.games.impl.Duel;
import camp.pvp.kits.DuelKit;
import camp.pvp.queue.GameQueue;
import camp.pvp.utils.Colors;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class DuelRequest {

    private final UUID sender, opponent;
    private final DuelKit kit;
    private final Arena arena;
    private final Date expires;

    public DuelRequest(UUID sender, UUID opponent, DuelKit kit, Arena arena, int secondsUntilExpired) {
        this.sender = sender;
        this.opponent = opponent;
        this.kit = kit;
        this.arena = arena;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, secondsUntilExpired);
        this.expires = calendar.getTime();
    }

    public void send() {
        Player senderPlayer = Bukkit.getPlayer(sender);
        Player opponentPlayer = Bukkit.getPlayer(opponent);

        if(senderPlayer != null && opponentPlayer != null) {
            senderPlayer.sendMessage(ChatColor.GREEN + "You sent a duel request to " + opponentPlayer.getName() + ".");

            TextComponent msg = new TextComponent(Colors.get("&aYou received a duel request from " + senderPlayer.getName() + ", click to accept."));
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept " + senderPlayer.getName()));
            msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Colors.get("&a/accept " + senderPlayer.getName())).create()));
            opponentPlayer.spigot().sendMessage(msg);
        }
    }

    public void startGame() {
        Player senderPlayer = Bukkit.getPlayer(sender);
        Player opponentPlayer = Bukkit.getPlayer(opponent);

        if(senderPlayer != null && opponentPlayer != null) {
            Duel duel = new Duel(Practice.instance, UUID.randomUUID());

            duel.setQueueType(GameQueue.Type.PRIVATE);
            duel.setKit(kit);

            duel.join(senderPlayer);
            duel.join(opponentPlayer);

            Practice.instance.getGameManager().addGame(duel);

            duel.start();
        }
    }

    public boolean isExpired() {
        return expires.before(new Date());
    }
}
