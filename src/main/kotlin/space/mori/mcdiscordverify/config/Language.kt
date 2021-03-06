package space.mori.mcdiscordverify.config

import space.mori.mcdiscordverify.utils.ConfigBase
import space.mori.mcdiscordverify.utils.getTarget
import java.io.File
import java.nio.file.Paths

object Language : ConfigBase<LanguageData>(
    data = LanguageData(),
    target = run {
        val target = getTarget(
            Paths.get(
                "lang",
                "lang_${Config.lang}.json"
            )
        )

        return@run if (!File(target.toUri()).exists()) {
            Config.lang = "en"
            getTarget(Paths.get("lang", "lang_en.json"))
        } else {
            target
        }
    }
) {
    val prefix: String
        get() = data.prefix

    val verifyKickMsg: String
        get() = data.verifyKickMsg

    val verifySuccessMsgTitle: String
        get() = data.verifySuccessMsgTitle

    val verifySuccessMsgDesc: String
        get() = data.verifySuccessMsgDesc

    val isNotRegisteredCode: String
        get() = data.isNotRegisteredCode

    val removeKickMsg: String
        get() = data.removeKickMsg
}

data class LanguageData(
    val prefix: String = "&6[MCDiscordVerify]&r",
    val verifyKickMsg: String = "&etype '!verify {verifyCode}' in discord channel 'verify'\nverify is refused in {verifyTimeout} seconds.",
    val verifySuccessMsgTitle: String = "Verify Success",
    val verifySuccessMsgDesc: String = "Successfully verified. {nickname}",
    val isNotRegisteredCode: String = "{code} is not registered  code",
    val removeKickMsg: String = "&eYou were kicked from the server because you were forced out of the guild or left yourself."
)
