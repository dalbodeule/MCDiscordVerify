package space.mori.mcdiscordverify.discord

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority
import space.mori.mcdiscordverify.MCDiscordVerify.Companion.instance
import space.mori.mcdiscordverify.config.Config
import space.mori.mcdiscordverify.config.Config.discordChannel
import space.mori.mcdiscordverify.config.Config.discordGuild
import space.mori.mcdiscordverify.config.Config.discordToken
import space.mori.mcdiscordverify.config.Config.verifyTimeout
import space.mori.mcdiscordverify.config.Language.config
import space.mori.mcdiscordverify.config.Language.prefix
import space.mori.mcdiscordverify.config.Language.removeKickMsg
import space.mori.mcdiscordverify.config.Language.verifyKickMsg
import space.mori.mcdiscordverify.config.UUIDtoDiscordID
import space.mori.mcdiscordverify.config.getDiscordUser
import space.mori.mcdiscordverify.utils.getColored
import java.awt.Color
import java.util.*
import java.util.concurrent.TimeUnit
import javax.security.auth.login.LoginException


object Discord: Listener, ListenerAdapter() {
    private val verifyUsers: MutableMap<String, UUID> = mutableMapOf()

    lateinit var bot: JDA
    private lateinit var commands: Map<String, DiscordCommand>

    @EventHandler(priority = EventPriority.HIGHEST)
    internal fun onJoin(event: PostLoginEvent) {
        if (!UUIDtoDiscordID.isContainsUser(event.player.uniqueId.toString())) {
            var verifyCode = verifyUsers.filterValues { it == event.player.uniqueId }.map { it.key }.firstOrNull()

            if (verifyCode == null) {
                 verifyCode = getRandomString(10)
            }

            event.player.disconnect(
                TextComponent(
                    "$prefix $verifyKickMsg"
                        .replace("{verifyCode}", verifyCode)
                        .replace("{verifyTimeout}", "$verifyTimeout")
                        .getColored
                )
            )
            verifyUsers[verifyCode] = event.player.uniqueId

            instance.proxy.scheduler.schedule(instance, Runnable {
                if (verifyUsers[verifyCode] != null) {
                    verifyUsers.remove(verifyCode)
                }
            }, Config.config.verifyTimeout*0L, TimeUnit.SECONDS)
        } else {
            if (event.player.getDiscordUser == null) {
                event.player.disconnect(TextComponent("$prefix $removeKickMsg"))
                UUIDtoDiscordID.removeUser(event.player.uniqueId.toString())
            }
        }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val msg = event.message.contentRaw
        val command = msg.split(" ")[0]

        if (!event.author.isBot && commands.keys.contains(command) && commands[command] != null) {
            instance.logger.info("${event.author.name} issued $command")
            commands[command]!!.execute(event)
        }
    }

    override fun onGuildMemberLeave(event: GuildMemberLeaveEvent) {
        val uuid = UUIDtoDiscordID.config.filterValues { it == event.user.id }.map { it.key }.firstOrNull()

        if (uuid != null) {
            UUIDtoDiscordID.removeUser(uuid)
            instance.proxy.getPlayer(UUID.fromString(uuid))?.disconnect(
                TextComponent("$prefix $removeKickMsg".getColored)
            )
            instance.logger.info("mcUUID: $uuid, discord: ${event.user.name} has leaved guild")
        }
    }

    internal fun main() {
        try {
            bot = JDABuilder(discordToken)
                .addEventListeners(Discord)
                .setActivity(Activity.playing("Minecraft"))
                .build()
            commands = listOf(
                object : DiscordCommand {
                    override val name = "!ping"
                    override fun execute(event: MessageReceivedEvent) {
                        event.channel.sendMessage("Pong!").queue()
                    }
                },
                object : DiscordCommand {
                    override val name = "!verify"
                    override fun execute(event: MessageReceivedEvent) {
                        if (
                            event.message.guild.id == discordGuild.toString() &&
                            event.channel.id == discordChannel.toString()
                        ) {
                            val code = event.message.contentRaw.split(" ")[1]

                            if (code in verifyUsers.keys) {
                                event.channel.sendMessage(run {
                                    val eb = EmbedBuilder()
                                    eb.setTitle(config.verifySuccessMsgTitle)
                                    eb.setColor(Color(0x88C959))

                                    eb.setDescription(
                                        config.verifySuccessMsgDesc
                                        .replace("{nickname}", verifyUsers[code]!!.toString())
                                    )

                                    eb.setImage("https://minotar.net/helm/${verifyUsers[code]!!}")

                                    return@run eb.build()
                                }).queue()

                                UUIDtoDiscordID.addUser(verifyUsers[code]!!.toString(), event.member!!.id)
                                verifyUsers.remove(code)
                            } else {
                                event.channel.sendMessage(
                                    config.isNotRegisteredCode
                                    .replace("{code}", code)
                                ).queue()
                            }
                        }
                    }
                }
            ).associateBy { it.name }.toSortedMap()
        } catch (e: LoginException) {
            instance.logger.info(e.message)
            throw e
        }
    }

    internal fun disable() {
        bot.shutdown()
    }

    interface DiscordCommand {
        val name: String
        fun execute(event: MessageReceivedEvent)
    }

    private fun getRandomString(length: Int): String {
        val charPool = ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}