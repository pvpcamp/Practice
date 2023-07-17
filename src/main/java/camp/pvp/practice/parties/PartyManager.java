package camp.pvp.practice.parties;

import camp.pvp.practice.Practice;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class PartyManager {

    private Practice plugin;
    public List<Party> parties;
    public PartyManager(Practice plugin) {
        this.plugin = plugin;
        this.parties = new ArrayList<>();
    }
}
