package com.gmail.imustek.oha.commands;

import com.gmail.imustek.oha.Helpers.MessageHelper;
import com.gmail.imustek.oha.Helpers.RegionHelper;
import com.gmail.imustek.oha.jake;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Mustek
 */
public class JakeCommandExecutor implements CommandExecutor {

    private jake plugin;
    private RegionHelper regionHelper;
    private World lobbyWorld;

    // Init the class
    public JakeCommandExecutor(jake plugin) {
        this.plugin = plugin;
        this.regionHelper = new RegionHelper(plugin);
        this.lobbyWorld = plugin.getServer().getWorld("lobby");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Regional teleport
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("tp")) {
                    if (args.length < 3 || args.length > 3) {
                        MessageHelper.sendMessage(player, "&cFormat: /c tp <Region From> <Region To>");
                        return true;
                    }

                    ProtectedRegion regionFrom, regionTo;

                    if (plugin.getRegionManager().hasRegion(args[1])) {
                        regionFrom = plugin.getRegionManager().getRegion(args[1]);
                    } else {
                        MessageHelper.sendMessage(player, "&cRegion &e" + args[1] + " &cdoes not exist.");
                        return true;
                    }
                    if (plugin.getRegionManager().hasRegion(args[2])) {
                        regionTo = plugin.getRegionManager().getRegion(args[2]);
                    } else {
                        MessageHelper.sendMessage(player, "&cRegion &e" + args[2] + " &cdoes not exist.");
                        return true;
                    }


                    List<Player> playersFrom = regionHelper.getPlayersRegion(player, regionFrom);
                    Location locationTo = regionHelper.getRegionCenter(player, regionTo);

                    // Teleport everyone in the region
                    for (Player onPlayer : playersFrom) {
                        onPlayer.teleport(locationTo);
                        MessageHelper.sendMessage(onPlayer, "&aYou have been teleported by &e" + player.getName() + "&a.");
                    }

                    MessageHelper.sendMessage(player, "&aPlayers have been teleported from &e" + regionFrom.getId() + " &ato &e" + regionTo.getId() + "&a.");

                    // Return a list of all available regions to the player
                } else if (args[0].equalsIgnoreCase("regions")) {
                    Map<String, ProtectedRegion> regions = plugin.getRegionManager().getRegions();
                    StringBuilder sb = new StringBuilder().append("&aAvailable Regions: ").append(ChatColor.WHITE);

                    for (Map.Entry<String, ProtectedRegion> entry : regions.entrySet()) {
                        sb.append(entry.getValue().getId()).append(", ");
                    }

                    MessageHelper.sendMessage(player, sb.delete(sb.length() - 2, sb.length()).toString());

                    // Get a list of all players in a region
                } else if (args[0].equalsIgnoreCase("region")) {
                    if (args.length < 2 || args.length > 2) {
                        MessageHelper.sendMessage(player, "&cFormat: /c region <region name>");
                        return true;
                    }
                    try {
                        ProtectedRegion protectedRegion = plugin.getRegionManager().getRegion(args[1]);
                        String baseStr = "&aRegion &e" + protectedRegion.getId() + "&a";
                        String playerStr = "";

                        List<Player> players = regionHelper.getPlayersRegion(player, protectedRegion);
                        for (Player onPlayer : players) {
                            playerStr += onPlayer.getDisplayName() + ", ";
                        }

                        if (players.isEmpty()) {
                            playerStr = "None  ";
                        }
                        baseStr += " [" + players.size() + "]" + ": &f";
                        MessageHelper.sendMessage(player, baseStr + playerStr.substring(0, playerStr.length() - 2));
                    } catch (Exception e) {
                        MessageHelper.sendMessage(player, "&cThat region does not exist.");
                    }


                } else if (args[0].equalsIgnoreCase("bring")) {
                    if (args.length < 2 || args.length > 2) {
                        MessageHelper.sendMessage(player, "&cFormat: /c bring <Region From>");
                        return true;
                    }
                    ProtectedRegion regionFrom;

                    if (plugin.getWorldGuard().getRegionManager(lobbyWorld).hasRegion(args[1])) {
                        regionFrom = plugin.getWorldGuard().getRegionManager(lobbyWorld).getRegion(args[1]);
                    } else {
                        MessageHelper.sendMessage(player, "&cRegion &e" + args[1] + " &cdoes not exist.");
                        return true;
                    }

                    List<Player> playersFrom = regionHelper.getPlayersRegion(player, regionFrom);
                    Location locationTo = player.getLocation();

                    for (Player onPlayer : playersFrom) {
                        onPlayer.teleport(locationTo);
                        MessageHelper.sendMessage(onPlayer, "&aYou have been teleported by &e" + player.getName() + "&a.");
                    }

                    MessageHelper.sendMessage(player, "&aPlayers have been teleported from &e" + regionFrom.getId() + "&a.");
                }
            } else {
                MessageHelper.sendMessage(player, "Commands: bring, regions, region, tp");
            }
        } else {
            sender.sendMessage("Please execute this command from in the game");
            return false;
        }
        return true;
    }
}
