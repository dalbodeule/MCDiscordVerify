package space.mori.mcdiscordverify.bukkit.command

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
        return when {
            args.isEmpty() -> SubCommands.filter { if (it.value.permissions != null) sender.hasPermission(it.value.permissions!!) else true }.map { it.value.name }
            args.size == 1 && SubCommands.filter { if (it.value.permissions != null) sender.hasPermission(it.value.permissions!!) else true }.keys.any { it.startsWith(args[0], ignoreCase = true) } -> {
                SubCommands.map { it.value.name }.filter { it.startsWith(args[0], ignoreCase = true) }
            }
            args.size > 1 && SubCommands.keys.contains(args[1]) -> {
                SubCommands[args[0]]?.tabCompleter(sender, command, alias, args)?.filter {
                    it.startsWith(args[args.size - 1], ignoreCase = true)
                } ?: listOf()
            }
            else -> listOf()
        }.toMutableList()
    }
}

open class SubCommand (
    open val name: String = "",
    open val description: String = "",
    open val parameter: String = "",
    open val permissions: String? = ""
) {
    open fun commandExecutor(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean { return true }
    open fun tabCompleter(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> { return mutableListOf() }
}