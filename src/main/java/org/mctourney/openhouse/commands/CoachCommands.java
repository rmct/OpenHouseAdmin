package org.mctourney.openhouse.commands;

import java.util.Set;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.mctourney.autoreferee.regions.AutoRefRegion;
import org.mctourney.autoreferee.regions.CuboidRegion;
import org.mctourney.autoreferee.util.commands.AutoRefCommand;
import org.mctourney.autoreferee.util.commands.AutoRefPermission;
import org.mctourney.autoreferee.util.commands.CommandHandler;
import org.mctourney.openhouse.OpenHouseAdmin;
import org.mctourney.openhouse.RegionData;
import org.mctourney.openhouse.util.RegionUtil;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;

/**
 * @author Mustek
 * @author authorblues
 */
public class CoachCommands implements CommandHandler
{
	private OpenHouseAdmin plugin;

	public CoachCommands(OpenHouseAdmin plugin)
	{
		this.plugin = plugin;
	}

	@AutoRefCommand(name={"openhouse", "tp"}, argmin=1, argmax=1,
		description="Teleport to the named region.")
	@AutoRefPermission(console=false, nodes={"openhouse.coach"})

	public boolean teleport(CommandSender sender, World match, String[] args, CommandLine options)
	{
		AutoRefRegion regionTo;
		String regionToName = args[0].toUpperCase();

		if (plugin.regions.containsKey(regionToName))
			regionTo = plugin.regions.get(regionToName).region;
		else
		{
			sender.sendMessage(ChatColor.RED + "Region " + regionToName + " does not exist.");
			return true;
		}

		((Player) sender).teleport(RegionUtil.getRegionCenter(regionTo));
		return true;
	}

	@AutoRefCommand(name={"openhouse", "transfer"}, argmin=2, argmax=2,
		description="Teleport all users within a region to another region.")
	@AutoRefPermission(console=false, nodes={"openhouse.coach"})

	public boolean transfer(CommandSender sender, World match, String[] args, CommandLine options)
	{
		AutoRefRegion regionFrom, regionTo;

		String regionFromName = args[0].toUpperCase();
		String regionToName = args[1].toUpperCase();

		if (plugin.regions.containsKey(regionFromName))
			regionFrom = plugin.regions.get(regionFromName).region;
		else
		{
			sender.sendMessage(ChatColor.RED + "Region " + regionFromName + " does not exist.");
			return true;
		}

		if (plugin.regions.containsKey(regionToName))
			regionTo = plugin.regions.get(regionToName).region;
		else
		{
			sender.sendMessage(ChatColor.RED + "Region " + regionToName + " does not exist.");
			return true;
		}

		Location locationTo = RegionUtil.getRegionCenter(regionTo);
		for (Player onPlayer : RegionUtil.getPlayersRegion(regionFrom))
		{
			onPlayer.teleport(locationTo);
			onPlayer.sendMessage(ChatColor.GREEN + "You have been teleported by " + sender.getName() + ".");
		}

		sender.sendMessage(ChatColor.GREEN + "Players have been teleported from " +
				regionFromName + " to " + regionToName + ".");

		return true;
	}

	@AutoRefCommand(name={"openhouse", "regions"}, argmax=0,
		description="List all available regions.")
	@AutoRefPermission(console=true, nodes={"openhouse.coach"})

	public boolean listRegions(CommandSender sender, World match, String[] args, CommandLine options)
	{
		Set<String> regionNames = Sets.newHashSet();

		for (String regname : plugin.regions.keySet())
			regionNames.add(regname.toUpperCase());

		sender.sendMessage(ChatColor.GREEN + "Available Regions: " +
			ChatColor.WHITE + StringUtils.join(regionNames, ", "));

		return true;
	}

	@AutoRefCommand(name={"openhouse", "region"}, argmin=1, argmax=1,
			description="Find out the number of players inside a given region.")
	@AutoRefPermission(console=true, nodes={"openhouse.coach"})

	public boolean describeRegion(CommandSender sender, World match, String[] args, CommandLine options)
	{
		try
		{
			String regionName = args[0].toUpperCase();
			AutoRefRegion region = plugin.regions.get(regionName).region;
			Set<String> names = Sets.newHashSet();

			for (Player onPlayer : RegionUtil.getPlayersRegion(region))
				names.add(onPlayer.getDisplayName());

			sender.sendMessage(String.format(ChatColor.GREEN + "Region %s [%d]: " + ChatColor.RESET + "%s",
					regionName, names.size(), names.size() > 0 ? StringUtils.join(names, ", ") : "None"));
		}
		catch (Exception e)
		{
			sender.sendMessage(ChatColor.RED + "That region does not exist.");
		}

		return true;
	}

	@AutoRefCommand(name={"openhouse", "defregion"}, argmin=1, argmax=1, options="g+",
			description="Create a new region.")
	@AutoRefPermission(console=false, nodes={"openhouse.coach"})

	public boolean defineRegion(CommandSender sender, World match, String[] args, CommandLine options)
	{
		if (match == null) return false;
		Player player = (Player) sender;

		WorldEditPlugin worldEdit = plugin.getWorldEdit();
		if (worldEdit == null)
		{
			// world edit not installed
			sender.sendMessage("This method requires WorldEdit installed and running.");
			return true;
		}

		Selection sel = worldEdit.getSelection(player);
		AutoRefRegion reg = null;

		if ((sel instanceof CuboidSelection))
		{
			CuboidSelection csel = (CuboidSelection) sel;
			reg = new CuboidRegion(csel.getMinimumPoint(), csel.getMaximumPoint());
		}

		String regionName = args[0].toUpperCase();
		if (reg != null)
		{
			reg.setName(regionName);
			RegionData rdata = new RegionData(reg);

			if (options.hasOption('g'))
				rdata.permissions.add("openhouse.group." + options.getOptionValue('g'));

			plugin.regions.put(regionName, rdata);
			sender.sendMessage(regionName + " is now " + reg);
		}
		return true;
	}

	@AutoRefCommand(name={"openhouse", "bring"}, argmin=1, argmax=1,
		description="Teleport all users within a region to you.")
	@AutoRefPermission(console=false, nodes={"openhouse.coach"})

	public boolean bringPlayers(CommandSender sender, World match, String[] args, CommandLine options)
	{
		Player player = (Player) sender;
		AutoRefRegion regionFrom;

		String regionName = args[0].toUpperCase();
		if (plugin.regions.containsKey(regionName))
			regionFrom = plugin.regions.get(regionName).region;
		else
		{
			sender.sendMessage(ChatColor.RED + "Region " + regionName + " does not exist.");
			return true;
		}

		Location locationTo = player.getLocation();
		for (Player onPlayer : RegionUtil.getPlayersRegion(regionFrom))
		{
			onPlayer.teleport(locationTo);
			onPlayer.sendMessage(ChatColor.GREEN + "You have been teleported by " + player.getName() + ".");
		}

		sender.sendMessage(ChatColor.GREEN + "Players have been teleported from " + regionName + ".");

		return true;
	}
}
