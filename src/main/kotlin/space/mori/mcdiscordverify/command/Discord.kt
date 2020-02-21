package space.mori.mcdiscordverify.command

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import space.mori.mcdiscordverify.config.Language.prefix
import space.mori.mcdiscordverify.config.getDiscordUser
import space.mori.mcdiscordverify.utils.CommandBase
import space.mori.mcdiscordverify.utils.SubCommand
import space.mori.mcdiscordverify.utils.getColored

object Discord: CommandBase(
    mutableListOf(
        object: SubCommand(
            "me",
            "check my information",
            ""
        ) {
            override fun commandExecutor(sender: CommandSender, args: Array<out String>): Boolean {
                val discordUser = (sender as ProxiedPlayer).getDiscordUser!!

                sendMessage(sender, "$prefix Your discord account is ${discordUser.name}")

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