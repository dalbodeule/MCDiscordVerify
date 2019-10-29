package space.mori.mcdiscordverify.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

open class CommandBase (
    open val SubCommands: Map<String, SubCommand> = listOf<SubCommand>().associateBy { it.name }
) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        return if (args.isEmpty() || SubCommands[args[0]] == null) {
            SubCommands.map { it.value.name } as MutableList<String>
        } else {
            SubCommands[args[0]]?.tabCompleter(sender, command, alias, args) ?: mutableListOf()
        }
    }
}

open class SubCommand (
    open val name: String = "",
    open val description: String = "",
    open val parameter: String = ""
) {
    open fun commandExecutor(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean { return true }
    open fun tabCompleter(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> { return mutableListOf() }
}