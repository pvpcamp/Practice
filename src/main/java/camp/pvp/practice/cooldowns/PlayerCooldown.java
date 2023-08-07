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
        ENDER_PEARL, ENERGY_JUMP, ENERGY_REGEN, ENERGY_RESISTANCE, ENERGY_SPEED, ENERGY_STRENGTH;

        public int getDuration() {
            switch(this) {
                case ENDER_PEARL:
                    return 16;
                case ENERGY_JUMP:
                    return 30;
                case ENERGY_STRENGTH:
                case ENERGY_RESISTANCE:
                case ENERGY_REGEN:
                    return 60;
                case ENERGY_SPEED:
                    return 45;
                default:
                    return 0;
            }
        }

        public String blockedMessage() {
            switch(this) {
                case ENDER_PEARL:
                    return Colors.get("&cYou must wait <time> second(s) before pearling again.");
                case ENERGY_JUMP:
                    return Colors.get("&cYou must wait <time> second(s) before using your Jump Boost ability again.");
                case ENERGY_REGEN:
                    return Colors.get("&cYou must wait <time> second(s) before using your Regeneration ability again.");
                case ENERGY_RESISTANCE:
                    return Colors.get("&cYou must wait <time> second(s) before using your Resistance ability again.");
                case ENERGY_STRENGTH:
                    return Colors.get("&cYou must wait <time> second(s) before using your Strength ability again.");
                case ENERGY_SPEED:
                    return Colors.get("&cYou must wait <time> second(s) before using your Speed ability again.");
                default:
                    return null;
            }
        }

        public String expireMessage() {
            switch(this) {
                case ENDER_PEARL:
                    return Colors.get("&aYou can now use ender pearls again.");
                case ENERGY_JUMP:
                    return Colors.get("&aYou can now use your Jump Boost ability again.");
                case ENERGY_REGEN:
                    return Colors.get("&aYou can now use your Regeneration ability again.");
                case ENERGY_RESISTANCE:
                    return Colors.get("&aYou can now use your Resistance ability again.");
                case ENERGY_STRENGTH:
                    return Colors.get("&aYou can now use your Strength ability again.");
                case ENERGY_SPEED:
                    return Colors.get("&aYou can now use your Speed ability again.");
                default:
                    return null;
            }
        }

        public LCCooldown getLCCooldown() {
            switch(this) {
                case ENDER_PEARL:
                    return new LCCooldown("pearl", this.getDuration(), Material.ENDER_PEARL);
                case ENERGY_JUMP:
                    return new LCCooldown("jump", this.getDuration(), Material.FEATHER);
                case ENERGY_REGEN:
                    return new LCCooldown("regen", this.getDuration(), Material.GHAST_TEAR);
                case ENERGY_RESISTANCE:
                    return new LCCooldown("resistance", this.getDuration(), Material.IRON_INGOT);
                case ENERGY_STRENGTH:
                    return new LCCooldown("strength", this.getDuration(), Material.BLAZE_POWDER);
                case ENERGY_SPEED:
                    return new LCCooldown("speed", this.getDuration(), Material.SUGAR);
                default:
                    return null;
            }
        }
    }

    private final Type type;
    private final Date issued;
    private final GameParticipant participant;
    private final Player player;
    private boolean expired = false;

    public PlayerCooldown(Type type, GameParticipant participant, Player player) {
        this.type = type;
        this.participant = participant;
        this.player = player;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, type.getDuration());
        this.issued = calendar.getTime();

        LunarClientAPI lcApi = LunarClientAPI.getInstance();

        if(lcApi.isRunningLunarClient(player)) {
            LunarClientAPI.getInstance().sendPacket(player, type.getLCCooldown().getPacket());
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
        return Colors.get(type.blockedMessage().replace("<time>", String.valueOf(seconds + 1)));
    }

    public void check() {
        if(!expired) {
            if(getIssued().before(new Date())) {
                expired = true;
                expire();
            } else {
                if(getType().equals(Type.ENDER_PEARL)) {
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

        if(getType().equals(Type.ENDER_PEARL)) {
            player.setExp(0);
            player.setLevel(0);
        }
    }
}
