package camp.pvp.practice.profiles;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.games.impl.Duel;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.utils.Colors;
import lombok.Getter;
import lombok.Setter;
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

@Getter @Setter
public class DuelRequest {

    private final GameProfile sender, opponent;
    private DuelKit kit;
    private Arena arena;
    private Date expires;

    public DuelRequest(GameProfile sender, GameProfile opponent) {
        this.sender = sender;
        this.opponent = opponent;
    }

    public void send() {
        Player senderPlayer = sender.getPlayer();
        Player opponentPlayer = opponent.getPlayer();

        if(senderPlayer != null && opponentPlayer != null) {
            if(sender.getState().equals(GameProfile.State.LOBBY) && opponent.getState().equals(GameProfile.State.LOBBY)) {

                DuelRequest oldRequest = opponent.getDuelRequests().get(sender.getUuid());
                if(oldRequest != null && oldRequest.getKit().equals(kit) && !oldRequest.isExpired()) {
                    senderPlayer.sendMessage(ChatColor.RED + "You already sent this player a duel request recently for this same kit.");
                    return;
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.SECOND, 30);
                this.expires = calendar.getTime();

                opponent.getDuelRequests().put(sender.getUuid(), this);

                senderPlayer.sendMessage(ChatColor.GREEN + "You sent a duel request to " + opponentPlayer.getName() + ".");

                StringBuilder sb = new StringBuilder();
                sb.append("\n&6&lNew Duel Request\n");
                sb.append("\n &7● &6From: &f" + senderPlayer.getName());
                sb.append("\n &7● &6Kit: &f" + kit.getDisplayName());
                sb.append("\n &7● &6Arena: &f" + (arena == null ? "Random" : arena.getDisplayName()));
                sb.append("\n");
                opponentPlayer.sendMessage(Colors.get(sb.toString()));

                TextComponent msg = new TextComponent(Colors.get("&6[Click to accept this duel]"));

                msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept " + senderPlayer.getName()));
                msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Colors.get("&a/accept " + senderPlayer.getName())).create()));

                opponentPlayer.spigot().sendMessage(msg);
                opponentPlayer.sendMessage(" ");
            } else {
                senderPlayer.sendMessage(ChatColor.RED + "This player is currently busy.");
            }
        }
    }

    public void startGame() {
        Player senderPlayer = sender.getPlayer();
        Player opponentPlayer = opponent.getPlayer();

        if(senderPlayer != null && opponentPlayer != null) {
            if(arena != null) {
                if (arena.getType().equals(Arena.Type.DUEL_BUILD)) {
                    for (Arena a : Practice.instance.getArenaManager().getArenaCopies(arena)) {
                        if (!a.isInUse()) {
                            arena = a;
                            break;
                        }
                    }
                }
            }

            Duel duel = new Duel(Practice.instance, UUID.randomUUID());

            duel.setQueueType(GameQueue.Type.PRIVATE);
            duel.setKit(kit);
            duel.setArena(arena);

            duel.join(senderPlayer);
            duel.join(opponentPlayer);

            this.expires = new Date();

            duel.start();
        }
    }

    public boolean isExpired() {
        return expires.before(new Date());
    }
}
