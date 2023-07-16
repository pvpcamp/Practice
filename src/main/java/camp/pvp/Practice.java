package camp.pvp;

import camp.pvp.arenas.ArenaManager;
import camp.pvp.commands.*;
import camp.pvp.cooldowns.CooldownRunnable;
import camp.pvp.games.GameManager;
import camp.pvp.interactables.InteractableItems;
import camp.pvp.kits.EnergyRunnable;
import camp.pvp.listeners.bukkit.block.BlockBreakListener;
import camp.pvp.listeners.bukkit.block.BlockPlaceListener;
import camp.pvp.listeners.bukkit.entity.EntityDamageByEntityListener;
import camp.pvp.listeners.bukkit.entity.EntityDamageListener;
import camp.pvp.listeners.bukkit.entity.EntityRegainHealthListener;
import camp.pvp.listeners.bukkit.entity.EntitySpawnListener;
import camp.pvp.listeners.bukkit.inventory.InventoryClickListener;
import camp.pvp.listeners.bukkit.inventory.InventoryMoveItemListener;
import camp.pvp.listeners.bukkit.player.*;
import camp.pvp.listeners.bukkit.potion.PotionSplashListener;
import camp.pvp.listeners.bukkit.projectile.ProjectileHitListener;
import camp.pvp.listeners.bukkit.projectile.ProjectileLaunchListener;
import camp.pvp.listeners.bukkit.world.WeatherChangeListener;
import camp.pvp.listeners.packets.EnderpearlSound;
import camp.pvp.nametags.NameColorRunnable;
import camp.pvp.parties.PartyManager;
import camp.pvp.profiles.GameProfileManager;
import camp.pvp.queue.GameQueueManager;
import camp.pvp.sidebar.SidebarAdapter;
import camp.pvp.utils.EntityHider;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.lunarclient.bukkitapi.LunarClientAPI;
import io.github.thatkawaiisam.assemble.Assemble;
import io.github.thatkawaiisam.assemble.AssembleStyle;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

@Getter @Setter
public class Practice extends JavaPlugin {

    public static Practice instance;

    private ProtocolManager protocolManager;
    private EntityHider entityHider;

    private Assemble assemble;

    private LunarClientAPI lunarClientAPI;

    private Location lobbyLocation;
    private Location kitEditorLocation;

    private ArenaManager arenaManager;
    private GameManager gameManager;
    private GameQueueManager gameQueueManager;
    private GameProfileManager gameProfileManager;
    private PartyManager partyManager;

    private BukkitTask cooldownTask, energyTask, nameColorTask;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);

        this.arenaManager = new ArenaManager(this);
        this.gameManager = new GameManager(this);
        this.gameQueueManager = new GameQueueManager(this);
        this.gameProfileManager = new GameProfileManager(this);
        this.partyManager = new PartyManager(this);

        this.assemble = new Assemble(this, new SidebarAdapter(this));
        assemble.setTicks(5);
        assemble.setAssembleStyle(AssembleStyle.MODERN);
        assemble.setup();

        this.lunarClientAPI = LunarClientAPI.getInstance();

        cooldownTask = this.getServer().getScheduler().runTaskTimer(this, new CooldownRunnable(this), 2, 2);
        energyTask = this.getServer().getScheduler().runTaskTimer(this, new EnergyRunnable(this), 10, 10);
        nameColorTask = this.getServer().getScheduler().runTaskTimer(this, new NameColorRunnable(this), 20, 20);

        if(getConfig().get("locations.lobby") != null) {
            this.lobbyLocation = (Location) getConfig().get("locations.lobby", Location.class);
        }

        if(getConfig().get("locations.kit_editor") != null) {
            this.kitEditorLocation = (Location) getConfig().get("locations.kit_editor", Location.class);
        }

        for(Entity entity : getServer().getWorlds().get(0).getEntities()) {
            entity.remove();
        }

        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        cooldownTask.cancel();
        nameColorTask.cancel();

        assemble.cleanup();

        if(lobbyLocation != null) {
            getConfig().set("locations.lobby", lobbyLocation);
        }

        if(kitEditorLocation != null) {
            getConfig().set("locations.kit_editor", kitEditorLocation);
        }

        this.saveConfig();

        instance = null;
    }

    public void registerCommands() {
        new AcceptCommand(this);
        new ArenaCommand(this);
        new BuildCommand(this);
        new DuelCommand(this);
        new GamesCommand(this);
        new PartyCommand(this);
        new PingCommand(this);
        new PlayerTimeCommand(this);
        new PostGameInventoryCommand(this);
        new PracticeUtilCommand(this);
        new SpectateCommand(this);
        new StatisticsCommand(this);
    }

    public void registerListeners() {
        // Bukkit
        new BlockBreakListener(this);
        new BlockPlaceListener(this);

        new EntityDamageByEntityListener(this);
        new EntityDamageListener(this);
        new EntityRegainHealthListener(this);
        new EntitySpawnListener(this);

        new InventoryClickListener(this);
        new InventoryMoveItemListener(this);

        new FoodLevelChangeListener(this);
//        new PlayerBucketEmptyListener(this);
        new PlayerCommandPreprocessListener(this);
        new PlayerDeathListener(this);
        new PlayerDropItemListener(this);
        new PlayerInteractEntityListener(this);
        new PlayerInteractListener(this);
        new PlayerJoinLeaveListeners(this);
        new PlayerMoveListener(this);
        new PlayerPickupItemListener(this);
        new PlayerTeleportListener(this);

        new PotionSplashListener(this);

        new ProjectileHitListener(this);
        new ProjectileLaunchListener(this);

        new WeatherChangeListener(this);

        // Packets
        protocolManager.addPacketListener(new EnderpearlSound(this));
    }

    public void shutdown() {
        arenaManager.shutdown();
        gameManager.shutdown();
        gameProfileManager.shutdown();
    }
}
