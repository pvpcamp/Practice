package camp.pvp.practice.cooldowns;

import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.utils.Colors;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.icon.ItemStackIcon;
import com.lunarclient.apollo.module.cooldown.Cooldown;
import com.lunarclient.apollo.module.cooldown.CooldownModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
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

        public Cooldown getApolloCooldown() {
            switch(this) {
                case ENDER_PEARL:
                    return Cooldown.builder()
                            .name("pearl")
                            .icon(ItemStackIcon.builder()
                                    .itemId(Material.ENDER_PEARL.getId())
                                    .build())
                            .duration(Duration.ofSeconds(this.getDuration()))
                            .build();
                case ENERGY_JUMP:
                    return Cooldown.builder()
                            .name("jump")
                            .icon(ItemStackIcon.builder()
                                    .itemId(Material.FEATHER.getId())
                                    .build())
                            .duration(Duration.ofSeconds(this.getDuration()))
                            .build();
                case ENERGY_REGEN:
                    return Cooldown.builder()
                            .name("regen")
                            .icon(ItemStackIcon.builder()
                                    .itemId(Material.GHAST_TEAR.getId())
                                    .build())
                            .duration(Duration.ofSeconds(this.getDuration()))
                            .build();
                case ENERGY_RESISTANCE:
                    return Cooldown.builder()
                            .name("resistance")
                            .icon(ItemStackIcon.builder()
                                    .itemId(Material.IRON_INGOT.getId())
                                    .build())
                            .duration(Duration.ofSeconds(this.getDuration()))
                            .build();
                case ENERGY_STRENGTH:
                    return Cooldown.builder()
                            .name("strength")
                            .icon(ItemStackIcon.builder()
                                    .itemId(Material.BLAZE_POWDER.getId())
                                    .build())
                            .duration(Duration.ofSeconds(this.getDuration()))
                            .build();
                case ENERGY_SPEED:
                    return Cooldown.builder()
                            .name("speed")
                            .icon(ItemStackIcon.builder()
                                    .itemId(Material.SUGAR.getId())
                                    .build())
                            .duration(Duration.ofSeconds(this.getDuration()))
                            .build();
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

        if(Apollo.getPlayerManager().hasSupport(player.getUniqueId())) {

            CooldownModule cooldownModule = Apollo.getModuleManager().getModule(CooldownModule.class);

            Optional<ApolloPlayer> apolloPlayer = Apollo.getPlayerManager().getPlayer(player.getUniqueId());
            apolloPlayer.ifPresent(p -> {
                cooldownModule.displayCooldown(p, type.getApolloCooldown());
            });
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