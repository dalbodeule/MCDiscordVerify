package space.mori.mcdiscordverify.util

import org.bukkit.ChatColor

val String.getColoredString: String
    get() = ChatColor.translateAlternateColorCodes('&', this)