package camp.pvp;

import camp.pvp.arenas.ArenaManager;
import camp.pvp.commands.PlayerTimeCommand;
import camp.pvp.listeners.bukkit.PlayerJoinLeaveListeners;
import camp.pvp.profiles.GameProfileManager;
import camp.pvp.sidebar.SidebarAdapter;
import camp.pvp.utils.EntityHider;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.thatkawaiisam.assemble.Assemble;
import io.github.thatkawaiisam.assemble.AssembleStyle;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Practice extends JavaPlugin {

    public @Getter static Practice instance;

    private @Getter ProtocolManager protocolManager;
    private @Getter EntityHider entityHider;

    private Assemble assemble;

    private @Getter ArenaManager arenaManager;
    private @Getter GameProfileManager gameProfileManager;


    @Override
    public void onEnable() {
        instance = this;

        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);

        this.arenaManager = new ArenaManager(this);
        this.gameProfileManager = new GameProfileManager(this);

        this.assemble = new Assemble(this, new SidebarAdapter(this));
        assemble.setTicks(10);
        assemble.setAssembleStyle(AssembleStyle.MODERN);
        assemble.setup();

        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        arenaManager.shutdown();
        gameProfileManager.shutdown();

        assemble.cleanup();

        instance = null;
    }

    public void registerCommands() {
        new PlayerTimeCommand(this);
    }

    public void registerListeners() {
        new PlayerJoinLeaveListeners(this);
    }
}
