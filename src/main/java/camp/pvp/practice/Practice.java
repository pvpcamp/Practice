package camp.pvp.practice;

import camp.pvp.NetworkHelper;
import camp.pvp.command.CommandHandler;
import camp.pvp.practice.arenas.ArenaManager;
import camp.pvp.practice.cooldowns.CooldownRunnable;
import camp.pvp.practice.games.GameManager;
import camp.pvp.practice.guis.statistics.LeaderboardsGui;
import camp.pvp.practice.kits.EnergyRunnable;
import camp.pvp.practice.listeners.bukkit.block.BlockBreakListener;
import camp.pvp.practice.listeners.bukkit.block.BlockPlaceListener;
import camp.pvp.practice.listeners.bukkit.entity.*;
import camp.pvp.practice.listeners.bukkit.inventory.InventoryClickListener;
import camp.pvp.practice.listeners.bukkit.inventory.InventoryMoveItemListener;
import camp.pvp.practice.listeners.bukkit.potion.PotionSplashListener;
import camp.pvp.practice.listeners.bukkit.projectile.ProjectileHitListener;
import camp.pvp.practice.listeners.bukkit.projectile.ProjectileLaunchListener;
import camp.pvp.practice.listeners.bukkit.world.WeatherChangeListener;
import camp.pvp.practice.listeners.citizens.NPCRightClickListener;
import camp.pvp.practice.listeners.packets.EnderpearlSound;
import camp.pvp.practice.nametags.NameColorRunnable;
import camp.pvp.practice.parties.PartyManager;
import camp.pvp.practice.queue.GameQueueManager;
import camp.pvp.practice.sidebar.SidebarAdapter;
import camp.pvp.practice.utils.EntityHider;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.practice.commands.*;
import camp.pvp.practice.listeners.bukkit.player.*;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.sk89q.worldedit.WorldEdit;
import io.github.thatkawaiisam.assemble.Assemble;
import io.github.thatkawaiisam.assemble.AssembleStyle;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

@Getter @Setter
public class Practice extends JavaPlugin {

    public static Practice instance;

    private ProtocolManager protocolManager;
    private EntityHider entityHider;

    private Assemble assemble;

    private LunarClientAPI lunarClientAPI;

    private WorldEdit worldEdit;

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

        this.lunarClientAPI = LunarClientAPI.getInstance();

        this.worldEdit = WorldEdit.getInstance();

        this.arenaManager = new ArenaManager(this);
        this.gameManager = new GameManager(this);
        this.gameQueueManager = new GameQueueManager(this);
        this.gameProfileManager = new GameProfileManager(this);
        this.partyManager = new PartyManager(this);

        this.assemble = new Assemble(this, new SidebarAdapter(this));
        assemble.setTicks(4);
        assemble.setAssembleStyle(AssembleStyle.MODERN);
        assemble.setup();

        cooldownTask = this.getServer().getScheduler().runTaskTimer(this, new CooldownRunnable(this), 2, 2);
        energyTask = this.getServer().getScheduler().runTaskTimer(this, new EnergyRunnable(this), 0, 20);
        nameColorTask = this.getServer().getScheduler().runTaskTimer(this, new NameColorRunnable(this), 0, 20);

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
        new EventCommand(this);
        new GamesCommand(this);
        new LeaderboardsCommand(this);
        new PartyCommand(this);
        new PingCommand(this);
        new PlayerTimeCommand(this);
        new PostGameInventoryCommand(this);
        new PracticeUtilCommand(this);
        new RideCommand(this);
        new SettingsCommand(this);
        new SpectateCommand(this);
        new StatisticsCommand(this);
        new TournamentCommand(this);

        CommandHandler commandHandler = NetworkHelper.getInstance().getCommandHandler();
        commandHandler.registerCommand(new EloManagerCommand(this));
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
        new HCFPlayerInteractListener(this);
//        new PlayerBucketEmptyListener(this);
        new PlayerChatListener(this);
        new PlayerCommandPreprocessListener(this);
        new PlayerDeathListener(this);
        new PlayerDropItemListener(this);
        new PlayerInteractEntityListener(this);
        new PlayerInteractListener(this);
        new PlayerJoinLeaveListeners(this);
        new PlayerMoveListener(this);
        new PlayerPickupItemListener(this);

        new PotionSplashListener(this);

        new ProjectileHitListener(this);
        new ProjectileLaunchListener(this);

        new WeatherChangeListener(this);

        // Citizens
        new NPCRightClickListener(this);

        // Packets
        protocolManager.addPacketListener(new EnderpearlSound(this));
    }

    public void shutdown() {
        arenaManager.shutdown();
        gameManager.shutdown();
        gameProfileManager.shutdown();
    }
}
