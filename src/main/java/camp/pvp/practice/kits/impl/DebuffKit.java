package camp.pvp.practice.kits.impl;

import camp.pvp.practice.kits.GameKit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class DebuffKit extends NoDebuffKit {
    public DebuffKit() {
        super();
        setGameKit(GameKit.DEBUFF);

        ItemStack[] inv = getItems(), moreItems = getMoreItems();

        Potion poison = new Potion(PotionType.POISON, 1);
        poison.setSplash(true);

        Potion slowness = new Potion(PotionType.SLOWNESS, 1);
        slowness.setSplash(true);

        setIcon(poison.toItemStack(1));

        inv[9] = poison.toItemStack(1);
        inv[10] = slowness.toItemStack(1);
        inv[18] = poison.toItemStack(1);
        inv[19] = slowness.toItemStack(1);
        inv[27] = poison.toItemStack(1);
        inv[28] = slowness.toItemStack(1);

        moreItems[9] = poison.toItemStack(1);
        moreItems[10] = slowness.toItemStack(1);
        moreItems[18] = poison.toItemStack(1);
        moreItems[19] = slowness.toItemStack(1);
        moreItems[27] = poison.toItemStack(1);
        moreItems[28] = slowness.toItemStack(1);
    }
}
