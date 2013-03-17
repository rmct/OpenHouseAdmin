package com.gmail.imustek.oha.Helpers;

import com.gmail.imustek.oha.jake;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @author Mustek
 */
public class RegionHelper {

    Plugin plugin = null;

    public RegionHelper(jake plugin) {
        this.plugin = plugin;
    }

    public List<Player> getPlayersRegion(Player sender, ProtectedRegion region) {

        // Get points of the region
        BlockVector2D points0 = region.getPoints().get(0);
        BlockVector2D points1 = region.getPoints().get(3);

        // Iterate through players to see if they match the region
        List players = new ArrayList<Player>();

        for (Player onPlayer : plugin.getServer().getOnlinePlayers()) {
            Location playerLoc = onPlayer.getLocation();
            if (playerLoc.getX() >= points0.getX() && playerLoc.getX() <= points1.getX() + 1) {   // Check if player is between X cords.
                if (playerLoc.getZ() >= points0.getZ() && playerLoc.getZ() <= points1.getZ() + 1) { // Check if player is between Z cords.
                    if (onPlayer.getGameMode() == GameMode.ADVENTURE) {
                        players.add(onPlayer);
                    }
                }

            }
        }

        return players;
    }

    // Calculate the center of a region and return the location
    public Location getRegionCenter(Player sender, ProtectedRegion region) {
        int pointX = (region.getPoints().get(0).getBlockX() + region.getPoints().get(3).getBlockX()) / 2;
        int pointZ = (region.getPoints().get(0).getBlockZ() + region.getPoints().get(3).getBlockZ()) / 2;
        int pointY = sender.getWorld().getHighestBlockAt(pointX, pointZ).getY();

        return new Location(sender.getWorld(), pointX, pointY + 1, pointZ);
    }
}
