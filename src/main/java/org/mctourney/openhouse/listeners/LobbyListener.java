package org.mctourney.openhouse.listeners;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import org.mctourney.autoreferee.regions.AutoRefRegion;
import org.mctourney.openhouse.OpenHouseAdmin;
import org.mctourney.openhouse.RegionData;

import org.apache.commons.lang.math.RandomUtils;

import com.google.common.collect.Lists;

public class LobbyListener implements Listener
{
	protected OpenHouseAdmin plugin;

	public LobbyListener(OpenHouseAdmin plugin)
	{
		this.plugin = plugin;
		int updatespeed = plugin.getConfig().getInt("sign-update-interval", 15);

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				for (String regname : LobbyListener.this.plugin.regions.keySet())
					LobbyListener.this.plugin.regions.get(regname).update();
			}
		// run a sign update for all signs every so many seconds (default 15)
		}.runTaskTimer(plugin, 0L, updatespeed * 20L);
	}

	private class DeferredSignUpdateTask extends BukkitRunnable
	{
		private String regname;

		public DeferredSignUpdateTask(String regname)
		{ this.regname = regname; }

		@Override
		public void run()
		{ plugin.regions.get(regname).update(); }
	}

	private void updateLocationSign(Location loc)
	{
		for (Map.Entry<String, RegionData> e : plugin.regions.entrySet())
			for (Block sign : e.getValue().signs) if (sign.getLocation().equals(loc))
				new DeferredSignUpdateTask(e.getKey()).runTask(plugin);
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void playerTeleport(PlayerTeleportEvent event)
	{
		updateLocationSign(event.getFrom());
		updateLocationSign(event.getTo());
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void playerLogin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		updateLocationSign(player.getLocation());

		if (plugin.isLobbyServer())
			for (Player p : Bukkit.getOnlinePlayers())
			{
				p.hidePlayer(player);
				player.hidePlayer(p);
			}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void playerChat(PlayerChatEvent event)
	{
		Player player = event.getPlayer();
		if (player.getWorld() != plugin.getLobbyWorld()) return;

		if (plugin.isLobbyServer()) event.getRecipients().clear();
		else
		{
			RegionData rdata = null;
			for (RegionData r : plugin.regions.values())
				if (r.region.contains(player.getLocation())) rdata = r;

			if (rdata != null) event.getRecipients().retainAll(rdata.getAllPlayers());
			else if (!player.hasPermission("openhouse.chatadmin"))
				event.getRecipients().clear();
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void playerLogout(PlayerQuitEvent event)
	{
		updateLocationSign(event.getPlayer().getLocation());
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void playerRespawn(PlayerRespawnEvent event)
	{
		updateLocationSign(event.getRespawnLocation());
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void blockBreak(BlockBreakEvent event)
	{
		if (event.getBlock().getWorld() == plugin.getLobbyWorld())
			for (RegionData reg : plugin.regions.values())
		{
			Iterator<Block> iter = reg.signs.iterator();
			while (iter.hasNext())
			{
				Block block = iter.next();
				if (!(block.getState() instanceof Sign)) iter.remove();
			}
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void makeTeleportSign(SignChangeEvent event)
	{
		Block block = event.getBlock();
		if (block.getWorld() == plugin.getLobbyWorld())
		{
			String[] lines = event.getLines();
			if (lines[0] != null && "[OpenHouse]".equals(ChatColor.stripColor(lines[0].trim())))
			{
				String regname = ChatColor.stripColor(lines[1].trim().toUpperCase());
				RegionData rdata = plugin.regions.get(regname);

				// update lines with colors if they are the right command
				if ("@BACK".equals(regname)) event.setLine(1, ChatColor.BLUE + "@BACK" + ChatColor.RESET);
				else if ("@ANY".equals(regname)) event.setLine(1, ChatColor.RED + "@ANY" + ChatColor.RESET);

				if (rdata != null && rdata.signs.add(block))
					new DeferredSignUpdateTask(regname).runTask(plugin);
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void hitTeleportSign(PlayerInteractEvent event)
	{
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();

		if (event.hasBlock() && block.getWorld() == plugin.getLobbyWorld() && block.getState() instanceof Sign)
		{
			String[] lines = ((Sign) block.getState()).getLines();
			if (lines[0] != null && "[OpenHouse]".equals(ChatColor.stripColor(lines[0].trim())))
			{
				String regname = ChatColor.stripColor(lines[1].trim().toUpperCase());
				RegionData rdata = null;

				// teleport to the largest, non-full region
				if (regname.startsWith("@ANY"))
				{
					List<RegionData> aregions = Lists.newArrayList();
					int bestsize = 0, regsize;

					for (RegionData reg : plugin.regions.values()) if (reg.canJoin(player))
					{
						if ((regsize = reg.getPlayers().size()) > bestsize)
						{ bestsize = regsize; aregions.clear(); }

						// if this region is one of the largest regions, add to the list
						if (regsize == bestsize) aregions.add(reg);
					}

					// select a random region from the list of largest regions
					rdata = aregions.get(RandomUtils.nextInt(aregions.size()));
				}
				// teleport back to spawn
				else if (regname.startsWith("@BACK"))
				{
					// teleport the player to the lobby world spawn
					player.teleport(plugin.getLobbyWorld().getSpawnLocation());

					// setting rdata to null skips the block below
					rdata = null; event.setCancelled(true);
				}
				// get the region we need
				else rdata = plugin.regions.get(regname);

				if (rdata != null && !event.isCancelled() &&
					event.getAction() == Action.RIGHT_CLICK_BLOCK)
				{
					if (!rdata.canJoin(player)) player.sendMessage(
						ChatColor.RED + "You do not have permission to join this region!");
					else player.teleport(rdata.getCenter());
				}
			}

			if (!player.hasPermission("openhouse.coach"))
				event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void playerMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		Location to = event.getTo(), fm = player.getLocation();
		for (Map.Entry<String, RegionData> e : plugin.regions.entrySet())
		{
			AutoRefRegion reg = e.getValue().region;
			if (reg.contains(fm) != reg.contains(to))
				new DeferredSignUpdateTask(e.getKey()).runTask(plugin);
		}

		// cheap trick to disable flight for non-coaches in the lobby (fighting with AutoReferee)
		if (fm.getWorld() == plugin.getLobbyWorld() && player.getGameMode() != GameMode.CREATIVE && player.isFlying()
			&& !player.hasPermission("openhouse.coach")) player.setAllowFlight(false);
	}
}
