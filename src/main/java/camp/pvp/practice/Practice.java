package camp.pvp.practice;

import camp.pvp.practice.arenas.ArenaManager;
import camp.pvp.practice.commands.*;
import camp.pvp.practice.cooldowns.CooldownRunnable;
import camp.pvp.practice.games.GameManager;
import camp.pvp.practice.kits.EnergyRunnable;
import camp.pvp.practice.listeners.bukkit.block.BlockBreakListener;
import camp.pvp.practice.listeners.bukkit.block.BlockBurnListener;
import camp.pvp.practice.listeners.bukkit.block.BlockFromToListener;
import camp.pvp.practice.listeners.bukkit.block.BlockPlaceListener;
import camp.pvp.practice.listeners.bukkit.entity.*;
import camp.pvp.practice.listeners.bukkit.inventory.InventoryClickListener;
import camp.pvp.practice.listeners.bukkit.inventory.InventoryMoveItemListener;
import camp.pvp.practice.listeners.bukkit.player.*;
import camp.pvp.practice.listeners.bukkit.potion.PotionSplashListener;
import camp.pvp.practice.listeners.bukkit.projectile.ProjectileHitListener;
import camp.pvp.practice.listeners.bukkit.projectile.ProjectileLaunchListener;
import camp.pvp.practice.listeners.bukkit.world.WeatherChangeListener;
import camp.pvp.practice.listeners.citizens.NPCRightClickListener;
import camp.pvp.practice.listeners.packets.EnderpearlSound;
import camp.pvp.practice.nametags.NameColorRunnable;
import camp.pvp.practice.parties.PartyManager;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.practice.queue.GameQueueManager;
import camp.pvp.practice.sidebar.SidebarAdapter;
import camp.pvp.practice.tasks.TickNumberCounter;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.utils.EntityHider;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
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

    @Getter public static Practice instance;

    private ProtocolManager protocolManager;
    private EntityHider entityHider;

    private Assemble assemble;

    private Location lobbyLocation;
    private Location kitEditorLocation;

    private ArenaManager arenaManager;
    private GameManager gameManager;
    private GameQueueManager gameQueueManager;
    private GameProfileManager gameProfileManager;
    private PartyManager partyManager;

    private BukkitTask cooldownTask, energyTask, nameColorTask, tickNumberTask;
    private TickNumberCounter tickNumberCounter;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);

        this.gameProfileManager = new GameProfileManager(this);
        this.arenaManager = new ArenaManager(this);

        this.gameManager = new GameManager(this);
        this.gameQueueManager = new GameQueueManager(this);
        this.partyManager = new PartyManager(this);

        this.assemble = new Assemble(this, new SidebarAdapter(this));
        assemble.setTicks(4);
        assemble.setAssembleStyle(AssembleStyle.MODERN);
        assemble.setup();

        cooldownTask = this.getServer().getScheduler().runTaskTimer(this, new CooldownRunnable(this), 2, 2);
        energyTask = this.getServer().getScheduler().runTaskTimer(this, new EnergyRunnable(this), 0, 20);
        nameColorTask = this.getServer().getScheduler().runTaskTimer(this, new NameColorRunnable(this), 0, 20);

        tickNumberCounter = new TickNumberCounter();
        tickNumberTask = Bukkit.getScheduler().runTaskTimer(this, tickNumberCounter, 0, 1);

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

        shutdown();

        instance = null;
    }

    public void registerCommands() {
        new AcceptCommand(this);
        new AnnounceCommand(this);
        new ArenaCommand(this);
        new ArenaPositionCommand(this);
        new BuildCommand(this);
        new DuelCommand(this);
        new EloManagerCommand(this);
        new EventCommand(this);
        new ExplodeCommand(this);
        new GameDebugCommand(this);
        new GamesCommand(this);
        new LeaderboardsCommand(this);
        new MatchesCommand(this);
        new PartyCommand(this);
        new PingCommand(this);
        new PlayerTimeCommand(this);
        new PostGameInventoryCommand(this);
        new PracticeUtilCommand(this);
        new RideCommand(this);
        new SettingsCommand(this);
        new SpectateCommand(this);
        new StatisticsCommand(this);
        new SurrenderCommand(this);
        new TournamentCommand(this);
        new WhereAmICommand(this);
    }

    public void registerListeners() {
        // Bukkit
        new BlockBreakListener(this);
        new BlockBurnListener(this);
        new BlockFromToListener(this);
        new BlockPlaceListener(this);

        new EntityDamageByEntityListener(this);
        new EntityDamageListener(this);
        new EntityExplodeListener(this);
        new EntityRegainHealthListener(this);
        new EntitySpawnListener(this);

        new InventoryClickListener(this);
        new InventoryMoveItemListener(this);

        new FoodLevelChangeListener(this);
        new PlayerBucketEmptyListener(this);
        new PlayerBucketFillListener(this);
        new PlayerChatListener(this);
        new PlayerCommandPreprocessListener(this);
        new PlayerDeathListener(this);
        new PlayerDropItemListener(this);
        new PlayerInteractEntityListener(this);
        new PlayerInteractListener(this);
        new PlayerItemConsumeListener(this);
        new PlayerJoinLeaveListeners(this);
        new PlayerMoveListener(this);
        new PlayerPickupItemListener(this);
        new PlayerTeleportListener(this);

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
        for(World world : Bukkit.getWorlds()) {
            for(Entity entity : world.getEntities()) {
                if(!(entity instanceof Player)) {
                    entity.remove();
                }
            }
        }

        arenaManager.shutdown();
        gameManager.shutdown();
        gameProfileManager.shutdown();
    }

    public void sendDebugMessage(String message) {
        for(GameProfile profile : getGameProfileManager().getLoadedProfiles().values()) {
            if(profile.getPlayer() == null) continue;
            if(!profile.isDebugMode()) continue;

            profile.getPlayer().sendMessage(Colors.get("&4[" + getName() + " Debug] &f" + message));
        }
    }
}
