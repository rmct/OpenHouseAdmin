package org.mctourney.openhouse;

import com.google.common.collect.Maps;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mctourney.autoreferee.regions.AutoRefRegion;
import org.mctourney.openhouse.commands.CoachCommands;
import org.mctourney.openhouse.util.commands.CommandManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

/**
 * @author Mustek
 */
public class OpenHouseAdmin extends JavaPlugin
{
	private static OpenHouseAdmin instance = null;
	
	private CommandManager commandManager;

	public Map<String, AutoRefRegion> regions = Maps.newHashMap();
	
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

		if (getLobbyWorld() != null) loadRegions();
		getLogger().info(this.getDescription().getFullName() + " is enabled");
	}

	@Override
	public void onDisable()
	{
		if (getLobbyWorld() != null) saveRegions();
		getLogger().info(this.getDescription().getFullName() + " is disabled");
	}

	public File getRegionFile()
	{ return new File(getLobbyWorld().getWorldFolder(), "regions.xml"); }

	public void loadRegions()
	{
		Element regionConfig = null;
		File regionFile = getRegionFile();
		if (!regionFile.exists()) return;

		try
		{
			FileInputStream cfgStream = new FileInputStream(regionFile);
			regionConfig = new SAXBuilder().build(cfgStream).getRootElement();
			assert "regions".equalsIgnoreCase(regionConfig.getName());
		}
		catch (Exception e) { e.printStackTrace(); return; }

		World lobby = getLobbyWorld();
		for (Element child : regionConfig.getChildren())
		{
			AutoRefRegion reg = AutoRefRegion.fromElement(lobby, child);
			regions.put(child.getAttributeValue("name"), reg);
		}
	}

	public void saveRegions()
	{
		Element root = new Element("regions");
		for (Map.Entry<String, AutoRefRegion> entry : regions.entrySet())
		{
			Element regElement = entry.getValue().toElement();
			regElement.setAttribute("name", entry.getKey());
			root.addContent(regElement);
		}

		try
		{
			XMLOutputter xmlout = new XMLOutputter(Format.getPrettyFormat());
			xmlout.output(root, new FileOutputStream(getRegionFile()));
		}
		catch (java.io.IOException e)
		{ e.printStackTrace(); }

	}

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
