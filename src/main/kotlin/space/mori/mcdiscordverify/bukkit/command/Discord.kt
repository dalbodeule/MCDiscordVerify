package space.mori.mcdiscordverify.bukkit.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import space.mori.mcdiscordverify.bungee.config.Language.prefix
import space.mori.mcdiscordverify.bukkit.config.getDiscordUser
import space.mori.mcdiscordverify.bukkit.discord.Discord.guild
import space.mori.mcdiscordverify.utils.sendColorMessage

object Discord: CommandBase(
    mutableListOf(
        object: SubCommand(
            "me",
            "check my information",
            "",
            "mcdiscordverify.discord"
        ) {
            override fun commandExecutor(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
                val member = (sender as Player).getDiscordUser()?.let { guild?.getMember(it) }

                if (member != null) {
                    sender.sendColorMessage("$prefix Your discord account is &9" +
                        (if (member.nickname != null) member.nickname else member.user.name) +
                        "&r (&6@${member.user.name}&r)")
                } else {
                    sender.sendColorMessage("$prefix Your discord account is not found!")
                }

                return true
            }
        }
    ).associateBy { it.name }.toSortedMap()
) {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return if (args.isEmpty() || args[0] == "help" || !SubCommands.keys.contains(args[0])) {
            sender.sendColorMessage("$prefix ${command.name} Commands")
            SubCommands.forEach {
                when {
                    it.value.permissions == null ->
                        sender.sendColorMessage("/${command.name} ${it.value.name} ${it.value.parameter} - ${it.value.description}")
                    sender.hasPermission(it.value.permissions!!) ->
                        sender.sendColorMessage("/${command.name} ${it.value.name} ${it.value.parameter} - ${it.value.description}")
                }
            }

            true
        } else {
            if (SubCommands[args[0]]!!.permissions != null && !sender.hasPermission(SubCommands[args[0]]!!.permissions!!)) {
                sender.sendColorMessage("$prefix §cNo Permissions.")
            } else if (!SubCommands[args[0]]?.commandExecutor(sender, command, label, args)!!) {
                sender.sendColorMessage("$prefix wrong command")
                sender.sendColorMessage("$prefix /$label ${SubCommands[args[0]]!!.name} ${SubCommands[args[0]]!!.parameter}")
            }

            true
        }
    }
}
