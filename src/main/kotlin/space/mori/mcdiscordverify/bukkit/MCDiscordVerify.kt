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
        val pluginConfig = Config
        val uuidToDiscordID = UUIDtoDiscordID
        val language = Language
        val discordHandler = Discord
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
        pluginConfig.load()
        uuidToDiscordID.load()
        language.load()
        language.save()

        // jda server initialize
        discordHandler.main()

        // command initialize
        server.getPluginCommand("discord")?.run {
            this.setExecutor(DiscordCommand)
            this.tabCompleter = DiscordCommand
        }

        server.pluginManager.registerEvents(Discord, this)
    }

    override fun onDisable() {
        discordHandler.disable()

        pluginConfig.save()
        uuidToDiscordID.save()
        language.save()
    }
}
