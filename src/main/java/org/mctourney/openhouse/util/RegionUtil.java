package org.mctourney.openhouse.util;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.mctourney.openhouse.OpenHouseAdmin;

import com.google.common.collect.Sets;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;


/**
 * @author Mustek
 * @author authorblues
 */
public class RegionUtil
{
	public static Set<Player> getPlayersRegion(ProtectedRegion region)
	{
		// Get points of the region
		BlockVector min = region.getMinimumPoint();
		BlockVector max = region.getMaximumPoint();

		// Iterate through players to see if they match the region
		Set<Player> players = Sets.newHashSet();

		for (Player onPlayer : Bukkit.getOnlinePlayers())
			if (onPlayer.getGameMode() == GameMode.ADVENTURE)
			{
				Location playerLoc = onPlayer.getLocation();
				if (playerLoc.getX() >= min.getBlockX() && playerLoc.getX() <= max.getBlockX() + 1)
					if (playerLoc.getZ() >= min.getBlockZ() && playerLoc.getZ() <= max.getBlockZ() + 1)
						players.add(onPlayer);
			}

		return players;
	}

	// Calculate the center of a region and return the location
	public static Location getRegionCenter(ProtectedRegion region)
	{
		// Get points of the region
		BlockVector min = region.getMinimumPoint();
		BlockVector max = region.getMaximumPoint();
		World world = OpenHouseAdmin.getInstance().getLobbyWorld();

		double pointX = (min.getBlockX() + max.getBlockX()) / 2.0;
		double pointZ = (min.getBlockZ() + max.getBlockZ()) / 2.0;
		int pointY = world.getHighestBlockAt((int) pointX, (int) pointZ).getY();

		return new Location(world, pointX, pointY + 1, pointZ);
	}
}
