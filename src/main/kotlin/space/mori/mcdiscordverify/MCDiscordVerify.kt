package space.mori.mcdiscordverify

import org.bukkit.plugin.java.JavaPlugin
import space.mori.mcdiscordverify.config.Config
import space.mori.mcdiscordverify.config.Language
import space.mori.mcdiscordverify.config.UUIDtoDiscordID
import space.mori.mcdiscordverify.discord.Discord

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

        Discord.main()

        server.pluginManager.registerEvents(Discord, this)
    }

    override fun onDisable() {
        Discord.disable()

        Config.save()
        UUIDtoDiscordID.save()
        Language.save()
    }
}