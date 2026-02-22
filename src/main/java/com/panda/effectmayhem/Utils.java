package com.panda.effectmayhem;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;

public class Utils {
    private Utils() {}

    public static final Component PLUGIN_NAME = MiniMessage.miniMessage().deserialize("<#af7b0d>[</#af7b0d>" + "<gradient:#be850d:#cbb498>EffectMayhem</gradient>" + "<#cbb498>]</#cbb498>");

    public static String translateColorCode(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static Component addHeader(String text) {
        return PLUGIN_NAME.append(Component.text(" " + translateColorCode(text)));
    }
}
