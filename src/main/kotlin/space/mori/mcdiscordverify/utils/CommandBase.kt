package space.mori.mcdiscordverify.utils

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.TabExecutor


open class CommandBase (
    open val SubCommands: Map<String, SubCommand> = listOf<SubCommand>().associateBy { it.name },
    open val commandName: String,
    open val permissions: String?,
    open val alias: Array<String>?
) : Command(commandName, permissions, *(alias ?: arrayOf())), TabExecutor {

    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        if (sender != null && args != null) {
            onCommand(sender, args)
        }
    }

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): MutableIterable<String> {
        return when {
            args.isEmpty() -> SubCommands.filter { if (it.value.permissions != null) sender.hasPermission(it.value.permissions!!) else true }.map { it.value.name }
            args.size == 1 && SubCommands.filter { if (it.value.permissions != null) sender.hasPermission(it.value.permissions!!) else true }.keys.any { it.startsWith(args[0], ignoreCase = true) } -> {
                SubCommands.map { it.value.name }.filter { it.startsWith(args[0], ignoreCase = true) }
            }
            args.size > 1 && SubCommands.keys.contains(args[0]) -> {
                SubCommands[args[0]]?.tabCompleter(sender, args)?.filter {
                    it.startsWith(args[args.size - 1], ignoreCase = true)
                } ?: listOf()
            }
            else -> listOf()
        }.toMutableList()
    }

    open fun onCommand(sender: CommandSender, args: Array<out String>) {
        return
    }

    internal fun sendMessage(sender: CommandSender, msg: String) {
        val message = TextComponent(msg.getColored)
        sender.sendMessage(message)
    }
}

open class SubCommand (
    open val name: String = "",
    open val description: String = "",
    open val parameter: String = "",
    open val permissions: String? = ""
) {
    open fun commandExecutor(sender: CommandSender, args: Array<out String>): Boolean { return true }
    open fun tabCompleter(sender: CommandSender, args: Array<out String>): MutableList<String> { return mutableListOf() }

    fun sendMessage(sender: CommandSender, msg: String) {
        val message = TextComponent(msg.getColored)
        sender.sendMessage(message)
    }
}