package space.mori.mcdiscordverify.config

object UUIDtoDiscordID: ConfigBase<MutableMap<String, String>>(
    config = mutableMapOf(),
    target = getTarget("uuidToDiscord.json")
)