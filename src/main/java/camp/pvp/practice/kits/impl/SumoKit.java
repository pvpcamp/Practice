package camp.pvp.practice.kits.impl;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.kits.BaseDuelKit;
import camp.pvp.practice.kits.GameKit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

public class SumoKit extends BaseDuelKit {
    public SumoKit() {
        super(GameKit.SUMO);

        setFfa(true);
        setDieInWater(true);
        setTakeDamage(false);
        setHunger(false);
        setMoveOnStart(false);
        setDropItemsOnDeath(false);
        setFallDamage(false);
        setEditable(false);
        setTournament(false);

        setArenaTypes(Collections.singletonList(Arena.Type.DUEL_SUMO));

        setIcon(new ItemStack(Material.LEASH));

        PotionEffect jump = new PotionEffect(PotionEffectType.JUMP, 10, 245, true, false);
        getPotionEffects().add(jump);
    }
}
