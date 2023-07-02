package camp.pvp.profiles;

import camp.pvp.games.Game;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameProfile {

    public enum State {
        LOBBY, LOBBY_QUEUE, LOBBY_PARTY, KIT_EDITOR, IN_GAME, IN_GAME_WAITING, SPECTATING
    }

    private @Getter final UUID uuid;
    private @Getter @Setter String name;
    private @Getter @Setter Game game;

    public GameProfile(UUID uuid) {
        this.uuid = uuid;
    }

    public void documentImport(Document document) {
        this.name = document.getString("name");
    }

    public Map<String, Object> export() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);

        return values;
    }
}
