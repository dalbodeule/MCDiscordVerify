package space.mori.mcdiscordverify.bukkit.config

import space.mori.mcdiscordverify.common.config.ConfigData

object Config : ConfigBase<ConfigData>(
    data = ConfigData(),
    target = getTarget("config.json")
) {
    var debug: Boolean
        get() = data.debug
        set(value) { data.debug = value }

    var discordToken: String
        get() = data.discordToken
        set(value) { data.discordToken = value }

    var discordGuild: Number
        get() = data.discordGuild
        set(value) { data.discordGuild = value }

    var discordChannel: Number
        get() = data.discordChannel
        set(value) { data.discordChannel = value }

    var verifyTimeout: Int
        get() = data.verifyTimeout
        set(value) { data.verifyTimeout = value }

    var lang: String
        get() = data.lang
        set(value) { data.lang = value }
}
