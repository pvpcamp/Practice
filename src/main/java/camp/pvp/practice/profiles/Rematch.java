package camp.pvp.practice.profiles;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.DuelKit;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class Rematch {

    private GameProfile profile;
    private final UUID uuid;
    private final String name;
    private final DuelKit kit;

    public Rematch(GameProfile profile, UUID uuid, String name, DuelKit kit) {
        this.profile = profile;
        this.uuid = uuid;
        this.name = name;
        this.kit = kit;
    }

    public void send() {
        profile.setRematch(null);

        if(profile.getGiveItemsTask() != null) {
            profile.getGiveItemsTask().cancel();
            profile.givePlayerItems();
        }

        Player player = profile.getPlayer();
        Player target = Bukkit.getPlayer(uuid);
        if(target != null) {
            GameProfile targetProfile = Practice.instance.getGameProfileManager().getLoadedProfiles().get(target.getUniqueId());
            if(!targetProfile.getState().equals(GameProfile.State.LOBBY)) {
                player.sendMessage(ChatColor.RED + "This player is currently busy.");
            }

            DuelRequest duelRequest = new DuelRequest(profile, targetProfile);
            duelRequest.setKit(kit);
            duelRequest.send();
        } else {
            player.sendMessage(ChatColor.RED + "This player is no longer online.");
        }
    }
}
