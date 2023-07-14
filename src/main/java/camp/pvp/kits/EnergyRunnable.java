package camp.pvp.kits;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import camp.pvp.games.GameParticipant;
import camp.pvp.games.impl.HCFTeams;

public class EnergyRunnable implements Runnable{

    private Practice plugin;
    public EnergyRunnable(Practice plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for(Game game : plugin.getGameManager().getActiveGames()) {
            if(game instanceof HCFTeams) {
                for(GameParticipant participant : game.getAlive().values()) {
                    if(participant.getAppliedHcfKit() != null && (participant.getAppliedHcfKit().equals(HCFKit.ARCHER) || participant.getAppliedHcfKit().equals(HCFKit.BARD))) {
                        int energy = participant.getEnergy();
                        if(energy < 100) {
                            energy += 1;
                            participant.setEnergy(energy);
                        }
                    }
                }
            }
        }
    }
}
