package camp.pvp.practice.listeners.packets;

import camp.pvp.practice.cooldowns.PlayerCooldown;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class EnderpearlSound extends PacketAdapter {

    public Practice plugin;
    public EnderpearlSound(Practice plugin) {
        super(plugin, PacketType.Play.Server.NAMED_SOUND_EFFECT);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        PacketContainer packet = e.getPacket();
        Player player = e.getPlayer();
        Location soundLocation = new Location(player.getWorld(), packet.getIntegers().read(0) / 8.0, packet.getIntegers().read(1) / 8.0, packet.getIntegers().read(2) / 8.0);
        String soundName = packet.getStrings().read(0);

        if(soundName.equalsIgnoreCase("random.bow")) {
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            Player closest = null;
            double bestDistance = Double.MAX_VALUE;

            for (Player p : player.getWorld().getPlayers()) {
                if (p.getLocation().distance(soundLocation) < bestDistance) {
                    bestDistance = p.getLocation().distance(soundLocation);
                    closest = p;
                }
            }

            if (!player.canSee(closest)) {
                e.setCancelled(true);
            }

            if (closest.getItemInHand().getType().equals(Material.ENDER_PEARL)) {
                Game game = profile.getGame();
                if(game != null && game.getState().equals(Game.State.ACTIVE)) {
                    GameParticipant participant = game.getParticipants().get(closest.getUniqueId());
                    if(participant != null) {
                        PlayerCooldown cooldown = participant.getCooldowns().get(PlayerCooldown.Type.ENDER_PEARL);
                        if(cooldown != null) {
                            if(!cooldown.isExpired()) {
                                e.setCancelled(true);
                            }
                        }
                    } else {
                        e.setCancelled(true);
                    }
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }
}
