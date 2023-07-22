package camp.pvp.practice.cooldowns;

import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.utils.Colors;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.cooldown.LCCooldown;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Getter @Setter
public class PlayerCooldown {

    public enum Type {
        ENDER_PEARL;

        public int getDuration() {
            switch(this) {
                case ENDER_PEARL:
                    return 16;
                default:
                    return 0;
            }
        }

        public String blockedMessage() {
            switch(this) {
                case ENDER_PEARL:
                    return Colors.get("&cYou must wait <time> second(s) before pearling again.");
                default:
                    return null;
            }
        }

        public String expireMessage() {
            switch(this) {
                case ENDER_PEARL:
                    return Colors.get("&aYou can now use ender pearls again.");
                default:
                    return null;
            }
        }
    }

    private final Type type;
    private final Date issued;
    private final GameParticipant participant;
    private final Player player;
    private boolean expired = false, lunar;

    public PlayerCooldown(Type type, GameParticipant participant, Player player) {
        this.type = type;
        this.participant = participant;
        this.player = player;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, type.getDuration());
        this.issued = calendar.getTime();

        LunarClientAPI lcApi = LunarClientAPI.getInstance();
        this.lunar = lcApi.isRunningLunarClient(player);

        if(lunar && participant.isLunarCooldowns()) {
            LunarClientAPI.getInstance().sendPacket(player, new LCCooldown("Pearl Cooldown", 16, Material.ENDER_PEARL).getPacket());
        }
    }

    public long getRemaining() {
        return issued.getTime() - new Date().getTime();
    }

    public Double getTicksRemaining() {
        long duration = getRemaining();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;
        long milliseconds = TimeUnit.MILLISECONDS.toMillis(duration) % 1000;

        return (double) (Math.round((float) milliseconds / 50) + (seconds * 20));
    }

    public String getBlockedMessage() {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(getRemaining()) % 60;
        return type.blockedMessage().replace("<time>", String.valueOf(seconds + 1));
    }

    public void check() {
        if(!expired) {
            if(getIssued().before(new Date())) {
                expired = true;
                expire();
            } else {
                if(getType().equals(Type.ENDER_PEARL) && !lunar) {
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(getRemaining()) % 60;
                    player.setLevel((int) seconds + 1);
                    player.setExp((getTicksRemaining().floatValue() / (float) (getType().getDuration() * 20)));
                }
            }
        }
    }

    public void expire() {
        player.sendMessage(Colors.get(getType().expireMessage()));
        remove();
    }

    public void remove() {
        expired = true;

        if(!lunar) {
            player.setExp(0);
            player.setLevel(0);
        }
    }
}
