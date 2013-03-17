package com.gmail.imustek.oha.Helpers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author Mustek
 */
public class MessageHelper {

    public static void sendMessage(Player player, String message) {
        player.sendMessage(ChatColor.GOLD + "[RMCT] " + replaceColors(message));
    }

    // Replace all colorcodes by their color
    public static String replaceColors(String message) {
        message = message.replaceAll("&0", "" + ChatColor.BLACK);
        message = message.replaceAll("&1", "" + ChatColor.DARK_BLUE);
        message = message.replaceAll("&2", "" + ChatColor.BLUE);
        message = message.replaceAll("&3", "" + ChatColor.DARK_GREEN);
        message = message.replaceAll("&4", "" + ChatColor.DARK_RED);
        message = message.replaceAll("&5", "" + ChatColor.DARK_PURPLE);
        message = message.replaceAll("&6", "" + ChatColor.GOLD);
        message = message.replaceAll("&7", "" + ChatColor.GRAY);
        message = message.replaceAll("&8", "" + ChatColor.DARK_GRAY);
        message = message.replaceAll("&9", "" + ChatColor.DARK_AQUA);
        message = message.replaceAll("&a", "" + ChatColor.GREEN);
        message = message.replaceAll("&b", "" + ChatColor.AQUA);
        message = message.replaceAll("&c", "" + ChatColor.RED);
        message = message.replaceAll("&d", "" + ChatColor.LIGHT_PURPLE);
        message = message.replaceAll("&e", "" + ChatColor.YELLOW);
        message = message.replaceAll("&f", "" + ChatColor.WHITE);

        return message;
    }
}
