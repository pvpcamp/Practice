package camp.pvp.practice.profiles;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.games.impl.Duel;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.utils.Colors;
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
import java.util.UUID;

@Getter
public class DuelRequest {

    private final UUID sender, opponent;
    private final DuelKit kit;
    private final Arena arena;
    private Date expires;

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


            StringBuilder sb = new StringBuilder();
            sb.append("\n&6&lNew Duel Request\n");
            sb.append("\n &7● &6From: &f" + senderPlayer.getName());
            sb.append("\n &7● &6Kit: &f" + kit.getColor() + kit.getDisplayName());
            sb.append("\n &7● &6Arena: &f" + (arena == null ? "Random" : arena.getDisplayName()));
            sb.append("\n");
            opponentPlayer.sendMessage(Colors.get(sb.toString()));

            TextComponent msg = new TextComponent(Colors.get("&6[Click to accept this duel]"));
            TextComponent spacer = new TextComponent("\n ");

            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept " + senderPlayer.getName()));
            msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Colors.get("&a/accept " + senderPlayer.getName())).create()));

            opponentPlayer.spigot().sendMessage(msg, spacer);

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

            this.expires = new Date();

            duel.start();
        }
    }

    public boolean isExpired() {
        return expires.before(new Date());
    }
}
