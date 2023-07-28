package camp.pvp.practice.games.impl.events;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.profiles.GameProfile;

import java.util.List;
import java.util.UUID;

public class GameEvent extends Game {
    protected GameEvent(Practice plugin, UUID uuid) {
        super(plugin, uuid);
    }

    @Override
    public List<String> getScoreboard(GameProfile profile) {
        return null;
    }

    @Override
    public List<String> getSpectatorScoreboard(GameProfile profile) {
        return null;
    }

    @Override
    public void start() {

    }

    @Override
    public void end() {

    }
}
