package camp.pvp.practice.guis.statistics;

import camp.pvp.practice.Practice;
import camp.pvp.practice.guis.profile.MyProfileGui;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.profiles.stats.ProfileELO;
import camp.pvp.practice.profiles.stats.ProfileStatistics;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class StatisticsGui extends ArrangedGui {
    public StatisticsGui(Player opener, ProfileELO profileELO, ProfileStatistics statistics) {
        super("&6" + profileELO.getName() + " Statistics");

        setDefaultNavigationBar();

        if(opener.getUniqueId().equals(profileELO.getUuid())) {
            GuiButton myProfile = new GuiButton(Material.SKULL_ITEM, "&6&lGo to My Profile");
            myProfile.setDurability((short) 3);
            SkullMeta meta = (SkullMeta) myProfile.getItemMeta();
            meta.setOwner(opener.getName());
            myProfile.setItemMeta(meta);

            myProfile.setAction((p, b, g, click) -> {
                new MyProfileGui(Practice.getInstance().getGameProfileManager().getLoadedProfiles().get(opener.getUniqueId())).open(p);
            });

            myProfile.setSlot(0);
            myProfile.setOverrideGuiArrangement(true);
            addButton(myProfile);
        }

        GuiButton globalStats = new GuiButton(Material.NETHER_STAR, "&6&lGlobal Statistics");
        List<String> globalStatsLore = new ArrayList<>();
        globalStatsLore.add(" &7● &6Kills: &f" + statistics.getGlobal().getKills());
        globalStatsLore.add(" &7● &6Deaths: &f" + statistics.getGlobal().getDeaths());
        globalStatsLore.add(" &7● &6Wins: &f" + statistics.getGlobal().getWins());
        globalStatsLore.add(" &7● &6Current Win Streak: &f" + statistics.getGlobal().getWinStreak());
        globalStatsLore.add(" &7● &6Best Win Streak: &f" + statistics.getGlobal().getBestWinStreak());
        globalStats.setLore(globalStatsLore);
        globalStats.setSlot(4);
        globalStats.setOverrideGuiArrangement(true);
        addButton(globalStats);

        for(GameKit kit : GameKit.values()) {
            if(!kit.isDuelKit()) continue;

            List<String> lines = new ArrayList<>();
            lines.add("&e&lUnranked:");
            lines.add(" &7● &eKills: &f" + statistics.getUnranked().get(kit).getKills());
            lines.add(" &7● &eDeaths: &f" + statistics.getUnranked().get(kit).getDeaths());
            lines.add(" &7● &eWins: &f" + statistics.getUnranked().get(kit).getWins());
            lines.add(" &7● &eCurrent Win Streak: &f" + statistics.getUnranked().get(kit).getWinStreak());
            lines.add(" &7● &eBest Win Streak: &f" + statistics.getUnranked().get(kit).getBestWinStreak());

            if(kit.isRanked()) {
                lines.add(" ");
                lines.add("&6&lRanked:");
                lines.add(" &7● &6ELO: &f" + profileELO.getRatings().get(kit));
                lines.add(" &7● &6Kills: &f" + statistics.getRanked().get(kit).getKills());
                lines.add(" &7● &6Deaths: &f" + statistics.getRanked().get(kit).getDeaths());
                lines.add(" &7● &6Wins: &f" + statistics.getRanked().get(kit).getWins());
                lines.add(" &7● &6Current Win Streak: &f" + statistics.getRanked().get(kit).getWinStreak());
                lines.add(" &7● &6Best Win Streak: &f" + statistics.getRanked().get(kit).getBestWinStreak());
            }

            GuiButton button = new GuiButton(kit.getIcon(), "&6&l" + kit.getDisplayName());
            button.setLore(lines);
            addButton(button);
        }
    }
}
