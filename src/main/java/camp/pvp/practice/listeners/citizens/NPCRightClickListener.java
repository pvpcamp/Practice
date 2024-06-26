package camp.pvp.practice.listeners.citizens;

import camp.pvp.practice.Practice;
import camp.pvp.practice.guis.queue.DuelQueueGui;
import camp.pvp.practice.guis.queue.MinigameQueueGui;
import camp.pvp.practice.guis.statistics.LeaderboardsGui;
import camp.pvp.practice.guis.statistics.StatisticsGui;
import camp.pvp.practice.profiles.GameProfile;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCRightClickListener implements Listener {

    private Practice plugin;
    public NPCRightClickListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getClicker();
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        int id = npc.getId();

        if(profile != null) {
            NPCClickable clickable = NPCClickable.getForId(id);

            if (clickable != null) {
                switch (clickable) {
                    case DUEL_QUEUE:
                        if(profile.getState().equals(GameProfile.State.LOBBY)) {
                            new DuelQueueGui(profile.getLastSelectedQueueType(), profile).open(player);
                        }
                        break;
                    case MINIGAME_QUEUE:
                        if(profile.getState().equals(GameProfile.State.LOBBY)) {
                            new MinigameQueueGui(profile).open(player);
                        }
                        break;
                    case LEADERBOARDS:
                        new LeaderboardsGui().open(player);
                        break;
                    case STATISTICS:
                        new StatisticsGui(player, profile.getProfileStatistics()).open(player);
                        break;
                    default:
                        player.sendMessage(ChatColor.GREEN + "Coming soon!");
                }
            }
        }
    }
}
