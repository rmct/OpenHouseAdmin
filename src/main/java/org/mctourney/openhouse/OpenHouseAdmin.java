package org.mctourney.openhouse;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import org.mctourney.openhouse.commands.CoachCommands;
import org.mctourney.openhouse.util.commands.CommandManager;

/**
 * @author Mustek
 */
public class OpenHouseAdmin extends JavaPlugin
{
	private static OpenHouseAdmin instance = null;
	
	private CommandManager commandManager;
	
	// return OpenHouseAdmin singleton
	public static OpenHouseAdmin getInstance()
	{ return instance; }
	
	@Override
	public void onEnable()
	{
		// save static plugin instance
		instance = this;
		
		// user interface commands in a custom command manager
		commandManager = new CommandManager();
		commandManager.registerCommands(new CoachCommands(this), this);
		
		getLogger().info(this.getDescription().getFullName() + " is enabled");
	}

	@Override
	public void onDisable()
	{
		getLogger().info(this.getDescription().getFullName() + " is disabled");
	}

	// return the WG region manager
	public RegionManager getRegionManager()
	{
		WorldGuardPlugin wg = getWorldGuard();
		
		if (wg == null) return null;
		return wg.getRegionManager(getLobbyWorld());
	}

	public World getLobbyWorld()
	{
		return getServer().getWorld("lobby");
	}

	// import Worldguard
	public WorldGuardPlugin getWorldGuard()
	{
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) { return null; }

		return (WorldGuardPlugin) plugin;
	}
}
