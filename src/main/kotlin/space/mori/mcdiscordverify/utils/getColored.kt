package space.mori.mcdiscordverify.utils

import org.bukkit.ChatColor

val String.getColored: String
    get() = ChatColor.translateAlternateColorCodes('&', this)