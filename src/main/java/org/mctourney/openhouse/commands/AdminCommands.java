package org.mctourney.openhouse.commands;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

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

import org.apache.commons.cli.CommandLine;

/**
 * @author Mustek
 * @author authorblues
 */
public class AdminCommands implements CommandHandler
{
	private OpenHouseAdmin plugin;

	public AdminCommands(OpenHouseAdmin plugin)
	{
		this.plugin = plugin;
	}

	@AutoRefCommand(name={"openhouse", "defregion"}, argmin=1, argmax=1, options="g+",
		description="Create a new region.")
	@AutoRefPermission(console=false, nodes={"openhouse.admin"})

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
}
