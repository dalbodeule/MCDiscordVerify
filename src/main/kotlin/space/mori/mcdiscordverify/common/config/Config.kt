package space.mori.mcdiscordverify.common.config

data class ConfigData(
    var debug: Boolean = false,
    var discordToken: String = "",
    var discordGuild: Number = 0,
    var discordChannel: Number = 0,
    var verifyTimeout: Int = 120,
    var lang: String = "en"
)
