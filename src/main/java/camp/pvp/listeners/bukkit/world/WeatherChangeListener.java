package camp.pvp.listeners.bukkit.world;

import camp.pvp.Practice;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherChangeListener implements Listener {

    private Practice plugin;
    public WeatherChangeListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> event.getWorld().setWeatherDuration(0), 1);
    }
}
