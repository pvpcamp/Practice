package camp.pvp.practice.listeners.bukkit.player;

import camp.pvp.practice.Practice;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlayerTeleportListener implements Listener {

    private Practice plugin;
    public PlayerTeleportListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEvent(PlayerTeleportEvent event)
    {
        if(event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL))
        {
            event.getTo().setX(Math.floor(event.getTo().getX())+0.5f);
            event.getTo().setY(Math.floor(event.getTo().getY())+0.5f);
            event.getTo().setZ(Math.floor(event.getTo().getZ())+0.5f);
            BlockCheck landing = new BlockCheck(event.getTo().getBlock());
            boolean cancelTeleport = true;
            if ((event.getFrom().getWorld() == event.getTo().getWorld()) &&  (event.getFrom().distanceSquared(event.getTo()) < 32768)) {
                cancelTeleport = false;
                if (landing.isSafe) { event.getTo().setY(Math.floor(event.getTo().getY())+landing.adjustY); }
                else {
                    cancelTeleport=true;
                    double xMin = Math.min(event.getFrom().getX(), event.getTo().getX());
                    double xMax = Math.max(event.getFrom().getX(), event.getTo().getX());
                    double yMin = Math.min(event.getFrom().getY(), event.getTo().getY());
                    double yMax = Math.max(event.getFrom().getY(), event.getTo().getY());
                    double zMin = Math.min(event.getFrom().getZ(), event.getTo().getZ());
                    double zMax = Math.max(event.getFrom().getZ(), event.getTo().getZ());
                    List<Location> locations = new ArrayList<>();
                    for (double x=xMin; x<xMax; x++)
                    { for (double y=yMin; y<yMax; y++)
                    { for (double z=zMin; z<zMax; z++)
                    {
                        locations.add(new Location(event.getTo().getWorld(), Math.floor(x)+0.5f, Math.floor(y)+0.5f, Math.floor(z)+0.5f));
                    }
                    }
                    }
                    locations.sort(Comparator.comparing(location -> event.getTo().distanceSquared(location)));
                    for (Location location : locations)
                    {
                        BlockCheck blockCheck = new BlockCheck(location.getBlock());
                        if (blockCheck.isSafe)
                        {
                            location.setYaw(event.getTo().getYaw());
                            location.setPitch(event.getTo().getPitch());
                            location.setY(Math.floor(location.getY())+blockCheck.adjustY);
                            event.setTo(location);
                            cancelTeleport = false;
                            break;
                        }
                    }
                }
            }
            if ((cancelTeleport) || (event.getTo().equals(event.getFrom()))) { event.setCancelled(true); event.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL,1)); event.getPlayer().updateInventory(); }
        }
    }
}

