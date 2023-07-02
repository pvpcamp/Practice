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

    public enum Time {
        SUNRISE, DAY, SUNSET, NIGHT;

        public long getTime() {
            switch(this) {
                case SUNRISE: return 0;
                case DAY: return 6000;
                case SUNSET: return 12000;
                case NIGHT: return 18000;
                default: return 1337;
            }

        }
    }

    // Stored DB values.
    private @Getter final UUID uuid;
    private @Getter @Setter String name;
    private @Getter @Setter Time time;
    private @Getter @Setter State state;

    private @Getter @Setter Game game;

    public GameProfile(UUID uuid) {
        this.uuid = uuid;

        this.state = State.LOBBY;
        this.time = Time.DAY;
    }

    public void documentImport(Document document) {
        this.name = document.getString("name");
    }

    public Map<String, Object> export() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("time", time.toString());

        return values;
    }
}
