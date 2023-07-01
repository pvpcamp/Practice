package camp.pvp;

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

    public ProtocolManager protocolManager;
    public EntityHider entityHider;

    public Assemble assemble;



    @Override
    public void onEnable() {
        instance = this;

        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);

        this.assemble = new Assemble(this, new SidebarAdapter(this));
        assemble.setTicks(5);
        assemble.setAssembleStyle(AssembleStyle.MODERN);
        assemble.setup();
    }

    @Override
    public void onDisable() {
        instance = null;
    }
}
