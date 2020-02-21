package space.mori.mcdiscordverify.utils

import net.md_5.bungee.api.ChatColor

val String.getColored: String
    get() = ChatColor.translateAlternateColorCodes('&', this)