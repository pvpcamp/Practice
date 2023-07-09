package camp.pvp;

import camp.pvp.arenas.ArenaManager;
import camp.pvp.commands.BuildCommand;
import camp.pvp.commands.PlayerTimeCommand;
import camp.pvp.cooldowns.CooldownRunnable;
import camp.pvp.games.GameManager;
import camp.pvp.listeners.bukkit.player.PlayerJoinLeaveListeners;
import camp.pvp.listeners.packets.EnderpearlSound;
import camp.pvp.nametags.NameColorRunnable;
import camp.pvp.profiles.GameProfileManager;
import camp.pvp.queue.GameQueueManager;
import camp.pvp.sidebar.SidebarAdapter;
import camp.pvp.utils.EntityHider;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.thatkawaiisam.assemble.Assemble;
import io.github.thatkawaiisam.assemble.AssembleStyle;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

@Getter @Setter
public class Practice extends JavaPlugin {

    public static Practice instance;

    private ProtocolManager protocolManager;
    private EntityHider entityHider;

    private Location lobbyLocation;
    private Location kitEditorLocation;

    private Assemble assemble;

    private ArenaManager arenaManager;
    private GameManager gameManager;
    private GameQueueManager gameQueueManager;
    private GameProfileManager gameProfileManager;

    private BukkitTask cooldownTask, nameColorTask;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);

        this.assemble = new Assemble(this, new SidebarAdapter(this));
        assemble.setTicks(10);
        assemble.setAssembleStyle(AssembleStyle.MODERN);
        assemble.setup();

        this.arenaManager = new ArenaManager(this);
        this.gameManager = new GameManager(this);
        this.gameQueueManager = new GameQueueManager(this);
        this.gameProfileManager = new GameProfileManager(this);

        cooldownTask = this.getServer().getScheduler().runTaskTimer(this, new CooldownRunnable(this), 2, 2);
        nameColorTask = this.getServer().getScheduler().runTaskTimer(this, new NameColorRunnable(this), 10, 10);

        if(getConfig().get("locations.lobby") != null) {
            this.lobbyLocation = (Location) getConfig().get("locations.lobby", Location.class);
        }

        if(getConfig().get("locations.kit_editor") != null) {
            this.kitEditorLocation = (Location) getConfig().get("locations.kit_editor", Location.class);
        }

        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        arenaManager.shutdown();
        gameManager.shutdown();
        gameProfileManager.shutdown();

        cooldownTask.cancel();
        nameColorTask.cancel();

        assemble.cleanup();

        if(lobbyLocation != null) {
            getConfig().set("locations.lobby", lobbyLocation);
        }

        if(kitEditorLocation != null) {
            getConfig().set("locations.kit_editor", kitEditorLocation);
        }

        instance = null;
    }

    public void registerCommands() {
        new BuildCommand(this);
        new PlayerTimeCommand(this);
    }

    public void registerListeners() {
        // Bukkit
        new PlayerJoinLeaveListeners(this);

        // Packets
        new EnderpearlSound(this);
    }
}
