package camp.pvp.interactables.impl.party;

import camp.pvp.Practice;
import camp.pvp.games.GameParticipant;
import camp.pvp.games.impl.FreeForAll;
import camp.pvp.games.impl.TeamDuel;
import camp.pvp.interactables.ItemInteract;
import camp.pvp.kits.DuelKit;
import camp.pvp.parties.Party;
import camp.pvp.parties.PartyMember;
import camp.pvp.profiles.GameProfile;
import camp.pvp.profiles.GameProfileManager;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PartyEventInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        Party party = gameProfile.getParty();
        if(party.getLeader().getUuid().equals(player.getUniqueId())) {
            StandardGui gui = new StandardGui("Choose an Event", 9);

            GuiButton ffaEvent = new GuiButton(Material.GOLD_AXE, "&6Free For All");
            ffaEvent.setAction(new GuiAction() {
                @Override
                public void run(Player player, Gui gui) {
                    StandardGui kitGui = new StandardGui("Choose a Kit", 9);

                    int x = 0;
                    for(DuelKit kit : DuelKit.values()) {
                        if(kit.isFfa()) {
                            GuiButton button = new GuiButton(kit.getIcon(), kit.getColor() + kit.getDisplayName());
                            button.setCloseOnClick(true);
                            button.setLore(
                                    "&7Click to start " + kit.getColor() + kit.getDisplayName() + " &7FFA event!");
                            button.setAction(new GuiAction() {
                                @Override
                                public void run(Player player, Gui gui) {
                                    Practice plugin = Practice.instance;

                                    List<PartyMember> members = new ArrayList<>(), kickedMembers = new ArrayList<>();
                                    GameProfileManager gpm = plugin.getGameProfileManager();
                                    for (PartyMember member : party.getMembers().values()) {
                                        if (gpm.getLoadedProfiles().get(member.getUuid()).getGame() == null) {
                                            members.add(member);
                                        } else {
                                            kickedMembers.add(member);
                                        }
                                    }

                                    if (members.size() > 1) {
                                        FreeForAll ffa = new FreeForAll(plugin, UUID.randomUUID());
                                        for (PartyMember member : kickedMembers) {
                                            Player p = member.getPlayer();
                                            p.sendMessage(ChatColor.RED + "You have been kicked from the party since you were not able to play in the event.");
                                            party.leave(member.getPlayer());
                                        }

                                        for (PartyMember member : members) {
                                            ffa.join(member.getPlayer());
                                        }

                                        ffa.setParty(party);
                                        ffa.setKit(kit);

                                        ffa.start();
                                        plugin.getGameManager().addGame(ffa);
                                    } else {
                                        player.sendMessage(ChatColor.RED + "You do not have enough players in your party to participate in this event.");
                                    }
                                }
                            });

                            button.setSlot(x);
                            kitGui.addButton(button, false);
                            x++;
                        }
                    }

                    kitGui.open(player);
                }
            });

            ffaEvent.setSlot(3);
            gui.addButton(ffaEvent, false);

            GuiButton splitEvent = new GuiButton(Material.IRON_SWORD, "&6Split Teams");
            splitEvent.setAction(new GuiAction() {
                @Override
                public void run(Player player, Gui gui) {
                    StandardGui kitGui = new StandardGui("Choose a Kit", 9);

                    int x = 0;
                    for(DuelKit kit : DuelKit.values()) {
                        if(kit.isQueueable()) {
                            GuiButton button = new GuiButton(kit.getIcon(), kit.getColor() + kit.getDisplayName());
                            button.setCloseOnClick(true);
                            button.setLore(
                                    "&7Click to start " + kit.getColor() + kit.getDisplayName() + " &7Split Teams event!");
                            button.setAction(new GuiAction() {
                                @Override
                                public void run(Player player, Gui gui) {
                                    Practice plugin = Practice.instance;

                                    List<PartyMember> members = new ArrayList<>(), kickedMembers = new ArrayList<>();
                                    GameProfileManager gpm = plugin.getGameProfileManager();
                                    for (PartyMember member : party.getMembers().values()) {
                                        if (gpm.getLoadedProfiles().get(member.getUuid()).getGame() == null) {
                                            members.add(member);
                                        } else {
                                            kickedMembers.add(member);
                                        }
                                    }

                                    if (members.size() > 1) {
                                        TeamDuel teamDuel = new TeamDuel(plugin, UUID.randomUUID());
                                        for (PartyMember member : kickedMembers) {
                                            Player p = member.getPlayer();
                                            p.sendMessage(ChatColor.RED + "You have been kicked from the party since you were not able to play in the event.");
                                            party.leave(member.getPlayer());
                                        }

                                        List<PartyMember> shuffledMembers = new ArrayList<>(members);
                                        Collections.shuffle(shuffledMembers);

                                        int x = 0;
                                        for (PartyMember member : shuffledMembers) {
                                            GameParticipant p = teamDuel.join(member.getPlayer());

                                            if(x % 2 == 0) {
                                                p.setTeam(teamDuel.getBlue());
                                            } else {
                                                p.setTeam(teamDuel.getRed());
                                            }
                                            x++;
                                        }

                                        teamDuel.setKit(kit);
                                        teamDuel.setParty(party);

                                        teamDuel.start();
                                        plugin.getGameManager().addGame(teamDuel);
                                    } else {
                                        player.sendMessage(ChatColor.RED + "You do not have enough players in your party to participate in this event.");
                                    }
                                }
                            });

                            button.setSlot(x);
                            kitGui.addButton(button, false);
                            x++;
                        }
                    }

                    kitGui.open(player);
                }
            });

            splitEvent.setSlot(5);
            gui.addButton(splitEvent, false);

            gui.open(player);
        } else {
            player.sendMessage(ChatColor.RED + "You cannot host events since you are not the party leader.");
        }
    }
}
