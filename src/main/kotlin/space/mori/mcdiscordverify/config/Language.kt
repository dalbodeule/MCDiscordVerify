package space.mori.mcdiscordverify.config

import java.io.File
import java.nio.file.Paths

object Language : ConfigBase<LanguageData>(
    config = LanguageData(),
    target = run {
        val target = getTarget(Paths.get("lang", "lang_${Config.config.lang}.json"))

        return@run if (!File(target.toUri()).exists()) {
            getTarget(Paths.get("lang", "lang_en.json"))
        } else {
            Config.config.lang = "en"
            target
        }
    }
)

data class LanguageData(
    val verifyKickMsg: String = "type '!verify {verifyCode}' in discord channel 'verify'\nverify is refused in {verifyTimeout} seconds.",
    val verifySuccessMsgTitle: String = "Verify Success",
    val verifySuccessMsgDesc: String = "Successfully verified. {nickname}",
    val isNotRegistedCode: String = "{code} is not registed code",
    val removeKickMsg: String = "You were kicked from the server because you were forced out of the guild or left yourself."
)