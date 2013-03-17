package com.gmail.imustek.oha.commands;

import com.gmail.imustek.oha.Helpers.MessageHelper;
import com.gmail.imustek.oha.jake;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * @author Mustek
 */
public class JakeConsoleExecutor implements CommandExecutor {
    Plugin plugin = null;

    public JakeConsoleExecutor(jake plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        String arg = "";
        for (String string : args) {
            arg += string + " ";
        }

        String output = MessageHelper.replaceColors(arg.substring(0, arg.length()));
        plugin.getServer().broadcastMessage(ChatColor.GOLD + "[RMCT] " + output);


        return true;
    }
}
