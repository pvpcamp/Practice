package camp.pvp.practice.commands;

import camp.pvp.practice.Practice;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnnounceCommand implements CommandExecutor {

    private Practice plugin;
    private long lastUse;
    private String lastUserName;
    public AnnounceCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getCommand("announce").setExecutor(this);
        lastUse = 0;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player player)) return true;

        GameProfile profile = plugin.getGameProfileManager().getLoadedProfile(player.getUniqueId());

        GameQueue queue = plugin.getGameQueueManager().getQueue(player);
        if(!profile.getState().equals(GameProfile.State.LOBBY_QUEUE) || queue.getGameType().equals(GameQueue.GameType.DUEL)) {
            player.sendMessage(ChatColor.RED + "You can only use this command if you are in a minigame queue.");
            return true;
        }

        if(System.currentTimeMillis() - lastUse < 30 * 1000 && !player.hasPermission("practice.minigame.announce.bypass")) {
            player.sendMessage(ChatColor.RED + lastUserName + " has already used this command in the last 30 seconds, please wait before using it again.");
            return true;
        }

        String announcement = """
                
                &6&l%s &awants to play &6&l%s &7(%s/%s)
                &aJoin the Minigame Queue to play!
                 \n
                """.formatted(
                        profile.getName(),
                        queue.getMinigameType().toString(),
                        queue.getQueueMembers().size(),
                        queue.getMinigameType().getMaxPlayers()
                );

        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(Colors.get(announcement));
        }

        this.lastUse = System.currentTimeMillis();
        this.lastUserName = profile.getName();

        return true;
    }
}
