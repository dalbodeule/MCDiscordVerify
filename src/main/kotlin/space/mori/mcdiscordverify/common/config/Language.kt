package space.mori.mcdiscordverify.common.config

data class LanguageData(
    val prefix: String = "&6[MCDiscordVerify]&r",
    val verifyKickMsg: String = "&etype '!verify {verifyCode}' in discord channel 'verify'\nverify is refused in {verifyTimeout} seconds.",
    val verifySuccessMsgTitle: String = "Verify Success",
    val verifySuccessMsgDesc: String = "Successfully verified. {nickname}",
    val isNotRegisteredCode: String = "{code} is not registered  code",
    val removeKickMsg: String = "&eYou were kicked from the server because you were forced out of the guild or left yourself.",
    val pingCmdDesc: String = "Pong!",
    val pingCmdMsg: String = "Pong! System is operated!",
    val verifyCmdDesc: String = "verify your minecraft accounts.",
    val verifyCmdOptCode: String = "Can you get verify code on first connects.",
    val roleCmdDesc: String = "Set verify user's role.",
    val roleCmdMsg: String = "Success with {role}!",
    val roleCmdOptDesc: String = "verify user's role.",
    val serverCmdDesc: String = "Set verify channel.",
    val serverCmdMsg: String = "Success with {channel}!",
    val serverCmdOptDesc: String = "verify channel."
)