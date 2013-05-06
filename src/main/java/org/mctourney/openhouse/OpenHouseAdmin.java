package org.mctourney.openhouse;

import com.google.common.collect.Maps;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import org.mctourney.autoreferee.regions.AutoRefRegion;
import org.mctourney.openhouse.commands.CoachCommands;
import org.mctourney.openhouse.util.commands.CommandManager;

import java.util.Map;

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

	public Map<String, AutoRefRegion> regions = Maps.newHashMap();

	public World getLobbyWorld()
	{
		return getServer().getWorld("lobby");
	}

	// import Worldguard
	public WorldEditPlugin getWorldEdit()
	{
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");

		if (plugin == null || !(plugin instanceof WorldEditPlugin)) { return null; }

		return (WorldEditPlugin) plugin;
	}
}
