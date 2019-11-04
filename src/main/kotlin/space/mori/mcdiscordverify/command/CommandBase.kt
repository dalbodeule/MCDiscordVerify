package space.mori.mcdiscordverify.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

open class CommandBase (
    open val SubCommands: Map<String, SubCommand> = listOf<SubCommand>().associateBy { it.name }
) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty() || args[0] == "help") {
            sender.sendMessage("Help: ${command.name}")

            SubCommands.forEach {
                if (sender.hasPermission(it.value.permission)) {
                    sender.sendMessage("/${command.name} ${it.value.name} ${it.value.parameter} - ${it.value.description}")
                }
            }
        } else if (args[0] !in SubCommands.keys) {
            sender.sendMessage("/${command.name} ${args[0]} is not registered command")
        } else {
            SubCommands[args[0]]!!.commandExecutor(sender, command, label, args)
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        return if (args.isEmpty() || SubCommands[args[0]] == null) {
            SubCommands.filter { sender.hasPermission(it.value.permission) }.map { it.value.name } as MutableList<String>
        } else {
            SubCommands[args[0]]?.tabCompleter(sender, command, alias, args) ?: mutableListOf()
        }
    }
}

open class SubCommand (
    open val name: String = "",
    open val description: String = "",
    open val parameter: String = "",
    open val permission: String = ""
) {
    open fun commandExecutor(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean { return true }
    open fun tabCompleter(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> { return mutableListOf() }
}