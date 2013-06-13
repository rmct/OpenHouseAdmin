package org.mctourney.openhouse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.mctourney.autoreferee.AutoReferee;
import org.mctourney.autoreferee.regions.AutoRefRegion;
import org.mctourney.autoreferee.util.LocationUtil;
import org.mctourney.autoreferee.util.commands.CommandManager;
import org.mctourney.openhouse.commands.CoachCommands;
import org.mctourney.openhouse.listeners.LobbyListener;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import com.google.common.collect.Maps;

/**
 * @author Mustek
 */
public class OpenHouseAdmin extends JavaPlugin
{
	private static OpenHouseAdmin instance = null;

	public Map<String, RegionData> regions = Maps.newHashMap();

	// return OpenHouseAdmin singleton
	public static OpenHouseAdmin getInstance()
	{ return instance; }

	@Override
	public void onEnable()
	{
		// save static plugin instance
		instance = this;
		loadRegions();

		// user interface commands in a custom command manager
		CommandManager cmdmanager = AutoReferee.getInstance().getCommandManager();
		cmdmanager.registerCommands(new CoachCommands(this), this);

		PluginManager manager = Bukkit.getPluginManager();
		manager.registerEvents(new LobbyListener(this), this);

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
		// can't load regions if there isn't a lobby world
		World lobby = getLobbyWorld();
		if (lobby == null) return;

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

		Element regcontainer = regionConfig.getChild("regions");
		if (regcontainer != null) for (Element relement : regcontainer.getChildren())
		{
			RegionData rdata = new RegionData(AutoRefRegion.fromElement(lobby, relement));
			regions.put(rdata.region.getName(), rdata);

			for (Element signelement : relement.getChildren("sign"))
			{
				String coords = signelement.getAttributeValue("location");
				Location location = LocationUtil.fromCoords(lobby, coords);
				if (location.getBlock().getState() instanceof Sign)
					rdata.signs.add((Sign) location.getBlock().getState());
			}

			for (Element permelement : relement.getChildren("permission"))
				rdata.permissions.add(permelement.getTextTrim());
		}
	}

	public void saveRegions()
	{
		Element regionscontainer = new Element("regions");
		for (Map.Entry<String, RegionData> entry : regions.entrySet())
		{
			RegionData rdata = entry.getValue();

			Element regelement = rdata.region.toElement();
			regionscontainer.addContent(regelement);

			for (Sign sign : rdata.signs)
				regelement.addContent(new Element("sign").setAttribute("location",
					LocationUtil.toBlockCoords(sign.getLocation())));

			for (String perm : rdata.permissions)
				regelement.addContent(new Element("permission").setText(perm));
		}

		Element root = new Element("regions");
		root.addContent(regionscontainer);

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
