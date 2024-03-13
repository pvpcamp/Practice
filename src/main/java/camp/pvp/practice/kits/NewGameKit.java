package camp.pvp.practice.kits;

import camp.pvp.practice.kits.impl.*;
import lombok.Getter;
import org.apache.commons.lang.WordUtils;

public enum NewGameKit {
    FIREBALL_FIGHT(new FireballFightKit()),
    NO_DEBUFF(new NoDebuffKit()),
    BOXING(new BoxingKit()),
    BED_FIGHT(new BedFightKit()),
    SUMO(new SumoKit()),
    DEBUFF(new DebuffKit()),
    ARCHER(new ArcherKit()),
    BUILD_UHC(new BuildUHCKit()),
    CLASSIC(new ClassicKit()),
    SOUP(new SoupKit()),
    INVADED(new InvadedKit()),
    SKYWARS(new SkywarsKit()),
    SPLEEF(new SpleefKit()),
    STRATEGY(new StrategyKit()),
    ONE_IN_THE_CHAMBER(new OneInTheChamberKit());

    @Getter private BaseKit baseKit;
    NewGameKit (BaseKit baseKit) {
        this.baseKit = baseKit;
    }

    public String getDisplayName() {
        switch(this) {
            case BUILD_UHC:
                return "Build UHC";
            default:
                String name = this.name();
                name = name.replace("_", " ");
                return WordUtils.capitalizeFully(name);
        }
    }
}
