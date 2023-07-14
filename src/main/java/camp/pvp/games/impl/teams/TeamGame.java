package camp.pvp.games.impl.teams;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import camp.pvp.games.GameParticipant;
import camp.pvp.games.GameTeam;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.UUID;

public abstract class TeamGame extends Game {

    private @Getter GameTeam blue, red;
    protected TeamGame(Practice plugin, UUID uuid) {
        super(plugin, uuid);
        this.blue = new GameTeam(GameTeam.Color.BLUE, this);
        this.red = new GameTeam(GameTeam.Color.RED, this);
    }

    @Override
    public void eliminate(Player player, boolean leftGame) {
        super.eliminate(player, leftGame);

        if(getBlue().isEliminated() || getRed().isEliminated()) {
            this.end();
        }
    }

    @Override
    public void handleHit(Player victim, Player attacker, EntityDamageByEntityEvent event) {
        GameParticipant victimParticipant = this.getParticipants().get(victim.getUniqueId());
        GameParticipant participant = this.getParticipants().get(attacker.getUniqueId());
        if(victimParticipant != null && participant != null) {
            if(victimParticipant.isAlive() && participant.isAlive()) {
                if(!victimParticipant.getTeam().equals(participant.getTeam())) {
                    victimParticipant.setAttacker(attacker.getUniqueId());

                    participant.setHealth(Math.round(victim.getHealth()));
                    participant.setMaxHealth(Math.round(victim.getMaxHealth()));
                    participant.setHunger(victim.getFoodLevel());
                    participant.setPotionEffects(new ArrayList<>(victim.getActivePotionEffects()));
                    participant.hits++;
                    participant.currentCombo++;

                    if (participant.currentCombo > participant.longestCombo) {
                        participant.longestCombo = participant.currentCombo;
                    }
                } else {
                    attacker.sendMessage(ChatColor.RED + victim.getName() + " is your teammate.");
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }
}
