package space.mori.mcdiscordverify.bungee.config

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.md_5.bungee.api.connection.ProxiedPlayer
import space.mori.mcdiscordverify.bukkit.config.Config
import space.mori.mcdiscordverify.bungee.discord.Discord
import space.mori.mcdiscordverify.bungee.discord.Discord.bot

object UUIDtoDiscordID: ConfigBase<MutableMap<String, String>>(
    data = mutableMapOf(),
    target = getTarget("uuidToDiscord.json")
) {

    internal fun addUser(uuid: String, discordId: String): Boolean {
        data[uuid] = discordId

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
}

val ProxiedPlayer.getDiscordUser: Member?
    get() = UUIDtoDiscordID.getUser(this.uniqueId.toString())?.let {
        bot.getGuildById("${Config.discordGuild}")?.getMemberById(it)
    }