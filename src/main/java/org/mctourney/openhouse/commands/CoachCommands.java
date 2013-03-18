package org.mctourney.openhouse.commands;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mctourney.openhouse.OpenHouseAdmin;
import org.mctourney.openhouse.util.RegionUtil;
import org.mctourney.openhouse.util.commands.CommandHandler;
import org.mctourney.openhouse.util.commands.OpenHouseCommand;
import org.mctourney.openhouse.util.commands.OpenHousePermission;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

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

	@OpenHouseCommand(name={"openhouse", "tp"}, argmin=2, argmax=2,
		description="Teleport all users within a region to another region.")
	@OpenHousePermission(console=false, nodes={"openhouse.coach"})

	public boolean teleport(CommandSender sender, World match, String[] args, CommandLine options)
	{
		ProtectedRegion regionFrom, regionTo;

		if (plugin.getRegionManager().hasRegion(args[0]))
			regionFrom = plugin.getRegionManager().getRegion(args[0]);
		else
		{
			sender.sendMessage(ChatColor.RED + "Region " + args[0] + " does not exist.");
			return true;
		}
		
		if (plugin.getRegionManager().hasRegion(args[1]))
			regionTo = plugin.getRegionManager().getRegion(args[1]);
		else
		{
			sender.sendMessage(ChatColor.RED + "Region " + args[1] + " does not exist.");
			return true;
		}

		Location locationTo = RegionUtil.getRegionCenter(regionTo);
		for (Player onPlayer : RegionUtil.getPlayersRegion(regionFrom))
		{
			onPlayer.teleport(locationTo);
			onPlayer.sendMessage(ChatColor.GREEN + "You have been teleported by " + sender.getName() + ".");
		}

		sender.sendMessage(ChatColor.GREEN + "Players have been teleported from " + 
			regionFrom.getId() + " to " + regionTo.getId() + ".");
		
		return true;
	}

	@OpenHouseCommand(name={"openhouse", "regions"}, argmax=0,
		description="List all available regions.")
	@OpenHousePermission(console=true, nodes={"openhouse.coach"})

	public boolean listRegions(CommandSender sender, World match, String[] args, CommandLine options)
	{
		Set<String> regionNames = Sets.newHashSet();
		
		for (ProtectedRegion reg : plugin.getRegionManager().getRegions().values())
			regionNames.add(reg.getId());

		sender.sendMessage(ChatColor.GREEN + "Available Regions: " + 
			ChatColor.WHITE + StringUtils.join(regionNames, ", "));
		
		return true;
	}

	@OpenHouseCommand(name={"openhouse", "region"}, argmin=1, argmax=1,
		description="Find out the number of players inside a given region.")
	@OpenHousePermission(console=true, nodes={"openhouse.coach"})

	public boolean describeRegion(CommandSender sender, World match, String[] args, CommandLine options)
	{
		try
		{
			ProtectedRegion region = plugin.getRegionManager().getRegion(args[0]);
			Set<String> names = Sets.newHashSet();
			
			for (Player onPlayer : RegionUtil.getPlayersRegion(region))
				names.add(onPlayer.getDisplayName());
			
			sender.sendMessage(String.format(ChatColor.GREEN + "Region %s [%d]: " + ChatColor.RESET + "%s", 
				region.getId(), names.size(), names.size() > 0 ? StringUtils.join(names, ", ") : "None"));
		}
		catch (Exception e)
		{
			sender.sendMessage(ChatColor.RED + "That region does not exist.");
		}
		
		return true;
	}

	@OpenHouseCommand(name={"openhouse", "bring"}, argmin=1, argmax=1,
		description="Teleport all users within a region to you.")
	@OpenHousePermission(console=false, nodes={"openhouse.coach"})

	public boolean bringPlayers(CommandSender sender, World match, String[] args, CommandLine options)
	{
		Player player = (Player) sender;
		ProtectedRegion regionFrom;

		if (plugin.getRegionManager().hasRegion(args[0]))
			regionFrom = plugin.getRegionManager().getRegion(args[0]);
		else
		{
			sender.sendMessage(ChatColor.RED + "Region " + args[0] + " does not exist.");
			return true;
		}

		Location locationTo = player.getLocation();
		for (Player onPlayer : RegionUtil.getPlayersRegion(regionFrom))
		{
			onPlayer.teleport(locationTo);
			onPlayer.sendMessage(ChatColor.GREEN + "You have been teleported by " + player.getName() + ".");
		}

		sender.sendMessage(ChatColor.GREEN + "Players have been teleported from " + regionFrom.getId() + ".");
		
		return true;
	}
}
