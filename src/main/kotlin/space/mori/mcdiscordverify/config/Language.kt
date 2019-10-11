package space.mori.mcdiscordverify.config

object Language : ConfigBase<LanguageData>(
    config = LanguageData(),
    target = getTarget("lang/lang_${Config.config.lang}.json")
)

data class LanguageData(
    val verifyKickMsg: String = "type '!verify {verifyCode}' in discord channel 'verify'\nverify is refused in {verifyTimeout} seconds.",
    val verifySuccessMsgTitle: String = "Verify Success",
    val verifySuccessMsgDesc: String = "Successfully verified. {nickname}",
    val isNotRegistedCode: String = "{code} is not registed code",
    val removeKickMsg: String = "You were kicked from the server because you were forced out of the guild or left yourself."
)