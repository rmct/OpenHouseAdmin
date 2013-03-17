package com.gmail.imustek.oha;

import com.gmail.imustek.oha.commands.JakeCommandExecutor;
import com.gmail.imustek.oha.commands.JakeConsoleExecutor;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Mustek
 */
public class jake extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Plugin is enabled");
        getCommand("c").setExecutor(new JakeCommandExecutor(this));
        getCommand("cb").setExecutor(new JakeConsoleExecutor(this));

    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin is disabled");
    }

    // Return the WG region manager
    public RegionManager getRegionManager() {
        return getWorldGuard().getRegionManager(getServer().getWorld("lobby"));
    }

    // Import Worldguard
    public WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }

        return (WorldGuardPlugin) plugin;
    }
}
