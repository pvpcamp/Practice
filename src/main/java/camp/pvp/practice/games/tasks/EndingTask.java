package camp.pvp.practice.games.tasks;

import camp.pvp.practice.Practice;
import camp.pvp.practice.cooldowns.PlayerCooldown;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.GameSpectator;
import camp.pvp.practice.games.impl.Duel;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.PreviousQueue;
import camp.pvp.practice.profiles.Rematch;
import camp.pvp.practice.queue.GameQueue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class EndingTask implements Runnable{

    private Game game;
    public EndingTask(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        for(Map.Entry<UUID, GameParticipant> entry : game.getParticipants().entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            GameParticipant participant = entry.getValue();
            GameProfile profile = game.getPlugin().getGameProfileManager().getLoadedProfiles().get(entry.getKey());

            if(player != null) {
                if(game instanceof Duel) {
                    Duel duel = (Duel) game;
                    GameQueue.Type queueType = duel.getQueueType();
                    boolean delayItems = false;
                    if(queueType.equals(GameQueue.Type.UNRANKED) || queueType.equals(GameQueue.Type.RANKED) || queueType.equals(GameQueue.Type.PRIVATE)) {
                        if(!queueType.equals(GameQueue.Type.PRIVATE)) {
                            PreviousQueue previousQueue = new PreviousQueue(game.getKit(), queueType);
                            profile.setPreviousQueue(previousQueue);
                            delayItems = true;
                        }

                        Rematch rematch;
                        for(GameParticipant p : game.getParticipants().values()) {
                            if(p.getUuid() != participant.getUuid() && p.getPlayer() != null && p.getPlayer().isOnline()) {
                                rematch = new Rematch(profile, p.getUuid(), p.getName(), duel.getKit());
                                profile.setRematch(rematch);
                                delayItems = true;
                            }
                        }
                    }

                    if(delayItems) {
                        profile.delayGiveItemsTask();
                    }
                }

                if(entry.getValue().isAlive()) {

                    for(PlayerCooldown cooldown : participant.getCooldowns().values()) {
                        cooldown.remove();
                    }

                    profile.setGame(null);
                    profile.playerUpdate(true);
                }
            }
        }

        for(GameSpectator spectator : new ArrayList<>(game.getSpectators().values())) {
            Player player = Bukkit.getPlayer(spectator.getUuid());
            game.spectateEnd(player);
        }

        Practice.instance.getGameProfileManager().updateGlobalPlayerVisibility();

        game.clearEntities();
        game.getArena().resetBlocks();

        // TODO: Replace built blocks from build duel.

//            for(Block block : this.getPlacedBlocks()) {
//                block.setType(Material.AIR);
//            }
//
//            for(BrokenBlock block : this.getBrokenBlocks()) {
//                Block b = block.getBlock();
//                b.setType(block.getMaterial());
//                b.setData(block.getData());
//            }

//            if(this.getKit().getType().equals(Kit.Type.BUILD)) {
//                this.getArena().setInUse(false);
//            }
    }
}
