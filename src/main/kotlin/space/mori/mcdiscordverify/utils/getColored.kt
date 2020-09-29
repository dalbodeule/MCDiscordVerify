package space.mori.mcdiscordverify.utils

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

val String.getColored
    get() = ChatColor.translateAlternateColorCodes('&', this)

fun CommandSender.sendColorMessage(value: String) {
    this.sendMessage(value.getColored)
}
