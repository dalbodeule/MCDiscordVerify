package space.mori.mcdiscordverify.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import space.mori.mcdiscordverify.config.getDiscordUser

object Discord: CommandBase() {
    override val SubCommands: Map<String, SubCommand> = mutableListOf<SubCommand>(
        object: SubCommand (
            "me", "check my information", "", "mcdiscordverify.discord"
        ) {
            override fun commandExecutor(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
                val discordUser = (sender as Player).getDiscordUser!!

                sender.sendMessage("Your discord account is ${discordUser.name}")

                return true
            }
        }
    ).associateBy { it.name }
}