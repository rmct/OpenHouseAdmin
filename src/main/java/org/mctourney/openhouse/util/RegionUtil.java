package org.mctourney.openhouse.util;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import org.mctourney.autoreferee.regions.AutoRefRegion;
import org.mctourney.autoreferee.regions.CuboidRegion;
import org.mctourney.openhouse.OpenHouseAdmin;

import com.google.common.collect.Sets;

/**
 * @author Mustek
 * @author authorblues
 */
public class RegionUtil
{
	public static Set<Player> getPlayersRegion(AutoRefRegion region)
	{
		// Iterate through players to see if they match the region
		Set<Player> players = Sets.newHashSet();

		for (Player onPlayer : Bukkit.getOnlinePlayers())
			if (onPlayer.getGameMode() == GameMode.ADVENTURE && region.contains(onPlayer.getLocation()))
				players.add(onPlayer);

		return players;
	}

	// Calculate the center of a region and return the location
	public static Location getRegionCenter(AutoRefRegion region)
	{
		// Get points of the region
		CuboidRegion cuboid = region.getBoundingCuboid();
		Location min = cuboid.getMinimumPoint();
		Location max = cuboid.getMaximumPoint();
		World world = OpenHouseAdmin.getInstance().getLobbyWorld();

		double pointX = (min.getBlockX() + max.getBlockX()) / 2.0;
		double pointZ = (min.getBlockZ() + max.getBlockZ()) / 2.0;
		int pointY = world.getHighestBlockAt((int) pointX, (int) pointZ).getY();

		return new Location(world, pointX, pointY + 1, pointZ);
	}
}
