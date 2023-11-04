package dev.kutuptilkisi.auction.util;

import org.bukkit.ChatColor;

public class ChatUtil {
    public static String color(String text){
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
