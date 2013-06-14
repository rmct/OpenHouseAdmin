package org.mctourney.openhouse;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.mctourney.autoreferee.regions.AutoRefRegion;
import org.mctourney.openhouse.util.RegionUtil;

import com.google.common.collect.Sets;

public class RegionData
{
	private static final int FULL_REGION_SIZE = 8;
	private static final long ANNOUNCEMENT_COOLDOWN = 30 * 1000L;

	// associated region
	public AutoRefRegion region;

	// any signs targetting this region
	public Set<Sign> signs = Sets.newHashSet();

	// any permissions necessary to join this region
	public Set<String> permissions = Sets.newHashSet();

	// coach who has claimed this region (not permanent)
	public String claimant = null;

	// is this region open or closed
	public boolean open = true;

	private long lastAnnouncementFull = 0L;

	public RegionData(AutoRefRegion reg)
	{ this.region = reg; }

	public boolean canJoin(Permissible player)
	{
		// full or closed regions can never be joined
		if (!this.open || this.isFull()) return false;

		// check all necessary permissions otherwise
		for (String perm : permissions)
			if (!player.hasPermission(perm)) return false;
		return true;
	}

	public void update()
	{
		// get the count and color based on whether the region is full
		int count = this.getPlayers().size();
		boolean full = isFull();

		ChatColor color = full ? ChatColor.DARK_RED : ChatColor.DARK_GREEN;
		String text = (count == 0 ? "no" : count) + " player" + (count == 1 ? "" : "s");
		String line = color + " " + text.toUpperCase();

		// update all the necessary signs
		for (Sign sign : signs) { sign.setLine(2, line); sign.update(); }

		long time = System.currentTimeMillis();

		// alert coaches via chat if region is full
		if (full && time > lastAnnouncementFull + ANNOUNCEMENT_COOLDOWN)
		{
			lastAnnouncementFull = time;
			for (Player player : OpenHouseAdmin.getInstance().getLobbyWorld().getPlayers())
				if (player.hasPermission("openhouse.coach")) player.sendMessage(new String[]
				{
					// tell the coaches that the region is ready
					ChatColor.GREEN + "Region " + region.getName() + " is ready for a coach!",

					// explain how to visit the region for inspection
					ChatColor.GREEN + "Type '/coach tp " + region.getName() + "' to teleport to this region.",

					// explain how to bring players from the region to your location
					ChatColor.GREEN + "Type '/coach bring " + region.getName() + "' to bring players to your location.",
				});
		}
	}

	public Set<Player> getPlayers()
	{ return RegionUtil.getPlayersRegion(this.region); }

	public boolean isFull()
	{ return getPlayers().size() >= FULL_REGION_SIZE; }
}
