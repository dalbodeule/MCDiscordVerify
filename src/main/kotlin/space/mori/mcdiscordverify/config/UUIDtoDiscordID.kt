package space.mori.mcdiscordverify.config

import net.dv8tion.jda.api.entities.User
import org.bukkit.entity.Player
import space.mori.mcdiscordverify.discord.Discord
import space.mori.mcdiscordverify.utils.ConfigBase
import space.mori.mcdiscordverify.utils.getTarget

object UUIDtoDiscordID: ConfigBase<MutableMap<String, String>>(
    config = mutableMapOf(),
    target = getTarget("uuidToDiscord.json")
) {
    internal fun addUser(uuid: String, discordId: String): Boolean {
        config[uuid] = discordId

        return true
    }

    internal fun getUser(uuid: String): String? {
        return config[uuid]
    }

    internal fun isContainsUser(uuid: String): Boolean {
        return config.contains(uuid)
    }

    internal fun removeUser(uuid: String): Boolean {
        config.remove(uuid)

        return true
    }
}

val Player.getDiscordUser: User?
    get () {
        val discordId = UUIDtoDiscordID.getUser(this.uniqueId.toString())

        return if (discordId != null) Discord.bot.getUserById(discordId) else null
    }