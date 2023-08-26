package space.mori.mcdiscordverify.bukkit.config

import net.dv8tion.jda.api.entities.User
import org.bukkit.entity.Player
import space.mori.mcdiscordverify.bukkit.discord.Discord.bot

object UUIDtoDiscordID: ConfigBase<MutableMap<String, String>>(
    data = mutableMapOf(),
    target = getTarget("uuidToDiscord.json")
) {
    internal fun addUser(uuid: String, discordId: String): Boolean {
        data[uuid] = discordId

        this.save()

        return true
    }

    internal fun getUser(uuid: String): String? {
        return data[uuid]
    }

    internal fun isContainsUser(uuid: String): Boolean {
        return data.contains(uuid)
    }

    internal fun removeUser(uuid: String): Boolean {
        data.remove(uuid)

        return true
    }

    internal fun getUserWithDiscordID(discordID: String): String? {
        return data.filterValues { it == discordID }.map { it.key }.firstOrNull()
    }
}

fun Player.getDiscordUser(): User? = UUIDtoDiscordID.getUser(this.uniqueId.toString())?.let { bot.getUserById(it) }
