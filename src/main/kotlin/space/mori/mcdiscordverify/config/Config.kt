package space.mori.mcdiscordverify.config

import space.mori.mcdiscordverify.utils.ConfigBase
import space.mori.mcdiscordverify.utils.getTarget

object Config : ConfigBase<ConfigData>(
    config = ConfigData(),
    target = getTarget("config.json")
) {
    var debug: Boolean
        get() = config.debug
        set(value) { config.debug = value }

    var discordToken: String
        get() = config.discordToken
        set(value) { config.discordToken = value }

    var discordGuild: Number
        get() = config.discordGuild
        set(value) { config.discordGuild = value }

    var discordChannel: Number
        get() = config.discordChannel
        set(value) { config.discordChannel = value }

    var verifyTimeout: Int
        get() = config.verifyTimeout
        set(value) { config.verifyTimeout = value }

    var lang: String
        get() = config.lang
        set(value) { config.lang = value }
}

data class ConfigData(
    var debug: Boolean = false,
    var discordToken: String = "",
    var discordGuild: Number = 0,
    var discordChannel: Number = 0,
    var verifyTimeout: Int = 120,
    var lang: String = "en"
)