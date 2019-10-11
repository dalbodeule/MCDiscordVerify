package space.mori.mcdiscordverify

import org.bukkit.plugin.java.JavaPlugin
import space.mori.mcdiscordverify.config.Config
import space.mori.mcdiscordverify.config.UUIDtoDiscordID
import space.mori.mcdiscordverify.discord.Discord

class MCDiscordVerify: JavaPlugin() {
    companion object {
        lateinit var instance: MCDiscordVerify
    }

    override fun onEnable() {
        instance = this

        Config.load()
        UUIDtoDiscordID.load()

        Discord.main()

        server.pluginManager.registerEvents(Discord, this)

        logger.info("enabled $name")
    }

    override fun onDisable() {
        Discord.disable()

        Config.save()
        UUIDtoDiscordID.save()

        logger.info("disabled $name")
    }
}