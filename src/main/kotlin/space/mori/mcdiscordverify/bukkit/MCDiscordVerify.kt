package space.mori.mcdiscordverify.bukkit

import org.bukkit.plugin.java.JavaPlugin
import space.mori.mcdiscordverify.bukkit.config.Config
import space.mori.mcdiscordverify.bukkit.config.Language
import space.mori.mcdiscordverify.bukkit.config.UUIDtoDiscordID
import space.mori.mcdiscordverify.bukkit.discord.Discord
import space.mori.mcdiscordverify.bukkit.command.Discord as DiscordCommand

class MCDiscordVerify: JavaPlugin() {
    companion object {
        lateinit var instance: MCDiscordVerify
    }

    override fun onEnable() {
        instance = this

        // language file initialize
        listOf(
            "lang/lang_en.json",
            "lang/lang_ko.json"
        ).forEach {
            saveResource(it, false)
        }

        // config initialize
        Config.load()
        UUIDtoDiscordID.load()
        Language.load()
        Language.save()

        // jda server initialize
        Discord.main()

        // command initialize
        server.getPluginCommand("discord")?.run {
            this.setExecutor(DiscordCommand)
            this.tabCompleter = DiscordCommand
        }

        server.pluginManager.registerEvents(Discord, this)
    }

    override fun onDisable() {
        Discord.disable()

        Config.save()
        UUIDtoDiscordID.save()
        Language.save()
    }
}
