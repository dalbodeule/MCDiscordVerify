package space.mori.mcdiscordverify.config

import net.dv8tion.jda.api.entities.User
import org.bukkit.entity.Player
import space.mori.mcdiscordverify.discord.Discord

object UUIDtoDiscordID: ConfigBase<MutableMap<String, String>>(
    config = mutableMapOf(),
    target = getTarget("uuidToDiscord.json")
)

val Player.getDiscordUser: User?
    get () {
        val discordId = UUIDtoDiscordID.config.filter { it.key == this.uniqueId.toString() }.values.firstOrNull()

        return if (discordId != null) Discord.bot.getUserById(discordId) else null
    }