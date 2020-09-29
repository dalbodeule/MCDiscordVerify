package space.mori.mcdiscordverify.discord

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import space.mori.mcdiscordverify.MCDiscordVerify.Companion.instance
import space.mori.mcdiscordverify.config.Config
import space.mori.mcdiscordverify.config.Config.discordChannel
import space.mori.mcdiscordverify.config.Config.discordGuild
import space.mori.mcdiscordverify.config.Config.discordToken
import space.mori.mcdiscordverify.config.Config.verifyTimeout
import space.mori.mcdiscordverify.config.Language.isNotRegisteredCode
import space.mori.mcdiscordverify.config.Language.prefix
import space.mori.mcdiscordverify.config.Language.removeKickMsg
import space.mori.mcdiscordverify.config.Language.verifyKickMsg
import space.mori.mcdiscordverify.config.Language.verifySuccessMsgDesc
import space.mori.mcdiscordverify.config.Language.verifySuccessMsgTitle
import space.mori.mcdiscordverify.config.UUIDtoDiscordID
import space.mori.mcdiscordverify.utils.getColored
import java.awt.Color
import java.util.*
import javax.security.auth.login.LoginException


object Discord: Listener, ListenerAdapter() {
    private val verifyUsers: MutableMap<String, UUID> = mutableMapOf()

    lateinit var bot: JDA
    var guild: Guild? = null

    private val commands = listOf(
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
                            eb.setTitle(verifySuccessMsgTitle)
                            eb.setColor(Color(0x88C959))

                            eb.setDescription(
                                verifySuccessMsgDesc
                                    .replace("{nickname}", Bukkit.getOfflinePlayer(verifyUsers[code]!!).name!!)
                            )

                            eb.setImage("https://minotar.net/helm/${verifyUsers[code]!!}")

                            return@run eb.build()
                        }).queue()

                        UUIDtoDiscordID.addUser(verifyUsers[code]!!.toString(), event.member!!.id)
                        verifyUsers.remove(code)
                    } else {
                        event.channel.sendMessage(
                            isNotRegisteredCode
                                .replace("{code}", code)
                        ).queue()
                    }
                }
            }
        }
    ).associateBy { it.name }.toSortedMap()

    @EventHandler(priority = EventPriority.HIGHEST)
    internal fun onJoin(event: PlayerJoinEvent) {
        if (!UUIDtoDiscordID.isContainsUser(event.player.uniqueId.toString())) {
            var verifyCode = verifyUsers.filterValues { it == event.player.uniqueId }.map { it.key }.firstOrNull()

            if (verifyCode == null) {
                 verifyCode = getRandomString(10)
            }

            event.player.kickPlayer("$prefix $verifyKickMsg"
                .replace("{verifyCode}", verifyCode)
                .replace("{verifyTimeout}", "$verifyTimeout")
                .getColored
            )
            verifyUsers[verifyCode] = event.player.uniqueId

            instance.server.scheduler.runTaskLater(instance, Runnable {
                if (verifyUsers[verifyCode] != null) {
                    verifyUsers.remove(verifyCode)
                }
            }, 20L * Config.verifyTimeout)
        } else {
            if (UUIDtoDiscordID.getUser(event.player.uniqueId.toString()) == null) {
                event.player.kickPlayer("$prefix $removeKickMsg")
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
        val uuid = UUIDtoDiscordID.getUserWithDiscordID(event.user.id)

        if (uuid != null) {
            UUIDtoDiscordID.removeUser(uuid)
            Bukkit.getPlayer(UUID.fromString(uuid))?.kickPlayer("$prefix $removeKickMsg".getColored)
            instance.logger.info("mcUUID: $uuid, discord: ${event.user.name} has leaved guild")
        }
    }

    internal fun main() {
        try {
            bot = JDABuilder(discordToken)
                .addEventListeners(Discord)
                .setActivity(Activity.playing("Minecraft"))
                .build().awaitReady()

            guild = bot.getGuildById(discordGuild.toLong())

            if (guild == null) {
                instance.logger.info("$prefix Guild is not found! plugin disabled.")
                instance.pluginLoader.disablePlugin(instance)
            }

        } catch (ex: Exception) {
            instance.logger.info(ex.message)

            instance.pluginLoader.disablePlugin(instance)

            throw ex
        }
    }

    internal fun disable() {
        try {
            bot.shutdown()
        } catch (e: UninitializedPropertyAccessException) {

        }
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
