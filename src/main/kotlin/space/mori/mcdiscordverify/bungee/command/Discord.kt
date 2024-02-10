package space.mori.mcdiscordverify.bungee.command

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import space.mori.mcdiscordverify.bukkit.config.Language
import space.mori.mcdiscordverify.bungee.config.Language.prefix
import space.mori.mcdiscordverify.bungee.config.getDiscordUser
import space.mori.mcdiscordverify.utils.sendColorMessage

object Discord: CommandBase(
    mutableListOf(
        object: SubCommand(
            "me",
            "check my information",
            ""
        ) {
            override fun commandExecutor(sender: CommandSender, args: Array<out String>): Boolean {
                /* val member = (sender as ProxiedPlayer).getDiscordUser

                if(member != null) {
                    sendMessage(sender, "${Language.prefix} Your discord account is &9${member.effectiveName}&r (&6@${member.user.name}&r)")
                } else {
                    sendMessage(sender, "$prefix Your discord account is not found!")
                } */
                sendMessage(sender, "${Language.prefix} testing...")

                return true
            }
        }
    ).associateBy { it.name }.toSortedMap(),
    "discord",
    "mcdiscordverify.discord",
    arrayOf("")
) {
    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        if (args.isEmpty() || args[0] == "help" || !SubCommands.keys.contains(args[0])) {
            sendMessage(sender, "$prefix Whitelist Commands")
            SubCommands.forEach {
                when {
                    it.value.permissions == null ->
                        sendMessage(sender, "/$commandName ${it.value.name} ${it.value.parameter} - ${it.value.description}")
                    sender.hasPermission(it.value.permissions) ->
                        sendMessage(sender, "/$commandName ${it.value.name} ${it.value.parameter} - ${it.value.description}")
                }
            }
        } else {
            if (!SubCommands[args[0]]!!.commandExecutor(sender, args)) {
                sendMessage(sender, "$prefix wrong command")
                sendMessage(sender, "$prefix /$commandName ${SubCommands[args[0]]!!.name} ${SubCommands[args[0]]!!.parameter}")
            }
        }
    }
}