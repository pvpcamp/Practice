package camp.pvp.practice.parties;

import camp.pvp.practice.games.GameTeam;
import camp.pvp.practice.kits.HCFKit;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter @Setter
public class PartyMember {

    private final UUID uuid;
    private final String name;
    private HCFKit hcfKit;
    private boolean leader;

    public PartyMember(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.hcfKit = HCFKit.DIAMOND;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
