package camp.pvp.practice.kits;

import camp.pvp.practice.kits.impl.*;
import lombok.Getter;
import org.apache.commons.lang.WordUtils;

public enum GameKit {
    FIREBALL_FIGHT,
    NO_DEBUFF,
    BOXING,
    BED_FIGHT,
    SUMO,
    DEBUFF,
    ARCHER,
    BUILD_UHC,
    CLASSIC,
    SOUP,
    INVADED,
    SKYWARS,
    SPLEEF,
    STRATEGY,
    ONE_IN_THE_CHAMBER;

    public BaseKit getBaseKit() {
        switch(this) {
            case FIREBALL_FIGHT:
                return new FireballFightKit();
            case NO_DEBUFF:
                return new NoDebuffKit();
            case BOXING:
                return new BoxingKit();
            case BED_FIGHT:
                return new BedFightKit();
            case SUMO:
                return new SumoKit();
            case DEBUFF:
                return new DebuffKit();
            case ARCHER:
                return new ArcherKit();
            case BUILD_UHC:
                return new BuildUHCKit();
            case CLASSIC:
                return new ClassicKit();
            case SOUP:
                return new SoupKit();
            case INVADED:
                return new InvadedKit();
            case SKYWARS:
                return new SkywarsKit();
            case SPLEEF:
                return new SpleefKit();
            case STRATEGY:
                return new StrategyKit();
            case ONE_IN_THE_CHAMBER:
                return new OneInTheChamberKit();
        }
        return null;
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
