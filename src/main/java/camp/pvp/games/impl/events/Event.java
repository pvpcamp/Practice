package camp.pvp.games.impl.events;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import camp.pvp.profiles.GameProfile;

import java.util.List;
import java.util.UUID;

public class Event extends Game {
    protected Event(Practice plugin, UUID uuid) {
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
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    public void end() {

    }

    @Override
    public void forceEnd() {

    }
}
