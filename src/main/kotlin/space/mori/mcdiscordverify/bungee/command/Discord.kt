package space.mori.mcdiscordverify.bungee.command

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import space.mori.mcdiscordverify.bungee.MCDiscordVerify
import space.mori.mcdiscordverify.bungee.MCDiscordVerify.Companion.instance
import space.mori.mcdiscordverify.bungee.config.Language.prefix
import space.mori.mcdiscordverify.bungee.config.getDiscordUser
import java.util.concurrent.TimeUnit

object Discord: CommandBase(
    mutableListOf(
        object: SubCommand(
            "me",
            "check my information",
            ""
        ) {
            override fun commandExecutor(sender: CommandSender, args: Array<out String>): Boolean {
                val member = (sender as ProxiedPlayer).getDiscordUser

                if(member != null) {
                    sendMessage(sender, "$prefix Your discord account is &9${member.effectiveName}&r (&6@${member.user.name}&r)")
                } else {
                    sendMessage(sender, "$prefix Your discord account is not found!")
                }
                // sendMessage(sender, "${Language.prefix} testing...")

                return true
            }
        },
        object: SubCommand(
            "reload",
            "reload this plugin",
            "",
            "mcdiscordverify.reload"
        ) {
            override fun commandExecutor(sender: CommandSender, args: Array<out String>): Boolean {
                MCDiscordVerify.pluginConfig.load()
                MCDiscordVerify.uuidToDiscordID.load()
                MCDiscordVerify.language.load()
                MCDiscordVerify.discordHandler.disable()
                instance.proxy.scheduler.schedule(instance, {
                    MCDiscordVerify.discordHandler.main()
                    sendMessage(sender, "$prefix Reload complete.")
                }, 20L * 10, TimeUnit.SECONDS)

                return true
            }
        }
    ).associateBy { it.name }.toSortedMap(),
    "discord",
    null,
    null
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