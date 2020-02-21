package space.mori.mcdiscordverify.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import space.mori.mcdiscordverify.config.Language.prefix
import space.mori.mcdiscordverify.config.getDiscordUser
import space.mori.mcdiscordverify.utils.CommandBase
import space.mori.mcdiscordverify.utils.SubCommand
import space.mori.mcdiscordverify.utils.getColored

object Discord: CommandBase(
    mutableListOf(
        object: SubCommand(
            "me", "check my information", "", "mcdiscordverify.discord"
        ) {
            override fun commandExecutor(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
                val discordUser = (sender as Player).getDiscordUser!!

                sender.sendMessage("$prefix Your discord account is ${discordUser.name}")

                return true
            }
        }
    ).associateBy { it.name }.toSortedMap()
) {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return if (args.isEmpty() || args[0] == "help" || !SubCommands.keys.contains(args[0])) {
            sender.sendMessage("$prefix MCDiscordVerify Command Help".getColored)
            SubCommands.forEach {
                when {
                    it.value.permissions == null ->
                        sender.sendMessage("$prefix/${command.name} ${it.value.name} ${it.value.parameter} - ${it.value.description}".getColored)
                    it.value.permissions != null && sender.hasPermission(it.value.permissions!!) ->
                        sender.sendMessage("$prefix/${command.name} ${it.value.name} ${it.value.parameter} - ${it.value.description}".getColored)
                }
            }
            true
        } else {
            if (SubCommands[args[0]]?.permissions?.let { sender.hasPermission(it) }!!) {
                sender.sendMessage("$prefix §cNo Permissions.".getColored)
                return true
            } else if (SubCommands[args[0]]!!.commandExecutor(sender, command, label, args)) {
                true
            } else {
                sender.sendMessage("$prefix Wrong Command.".getColored)
                sender.sendMessage("${command.name} ${SubCommands[args[0]]!!.name} ${SubCommands[args[0]]!!.parameter}".getColored)
                true
            }
        }
    }
}