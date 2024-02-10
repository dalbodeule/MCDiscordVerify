package space.mori.mcdiscordverify.bungee.config

import space.mori.mcdiscordverify.common.config.LanguageData
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

    val pingCmdDesc: String
        get() = data.pingCmdDesc

    val pingCmdMsg: String
        get() = data.pingCmdMsg

    val verifyCmdDesc: String
        get() = data.verifyCmdDesc

    val verifyCmdOptCode: String
        get() = data.verifyCmdOptCode

    val roleCmdDesc: String
        get() = data.roleCmdDesc

    val roleCmdMsg: String
        get() = data.roleCmdMsg

    val roleCmdOptDesc: String
        get() = data.roleCmdOptDesc

    val serverCmdDesc: String
        get() = data.serverCmdDesc

    val serverCmdMsg: String
        get() = data.serverCmdMsg

    val serverCmdOptDesc: String
        get() = data.serverCmdOptDesc
}
