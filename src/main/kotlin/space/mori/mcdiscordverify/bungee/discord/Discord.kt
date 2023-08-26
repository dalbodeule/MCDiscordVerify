package space.mori.mcdiscordverify.bungee.discord

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority
import org.bukkit.Bukkit
import space.mori.mcdiscordverify.bungee.MCDiscordVerify.Companion.instance
import space.mori.mcdiscordverify.bungee.config.Config
import space.mori.mcdiscordverify.bungee.config.Config.discordChannel
import space.mori.mcdiscordverify.bungee.config.Config.discordGuild
import space.mori.mcdiscordverify.bungee.config.Config.discordToken
import space.mori.mcdiscordverify.bungee.config.Config.verifyTimeout
import space.mori.mcdiscordverify.bungee.config.Language
import space.mori.mcdiscordverify.bungee.config.Language.prefix
import space.mori.mcdiscordverify.bungee.config.Language.removeKickMsg
import space.mori.mcdiscordverify.bungee.config.Language.verifyKickMsg
import space.mori.mcdiscordverify.bungee.config.UUIDtoDiscordID
import space.mori.mcdiscordverify.bungee.config.getDiscordUser
import space.mori.mcdiscordverify.utils.getColored
import java.awt.Color
import java.util.*
import java.util.concurrent.TimeUnit


object Discord: Listener, ListenerAdapter() {
    private val verifyUsers: MutableMap<String, UUID> = mutableMapOf()

    lateinit var bot: JDA

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

            instance.proxy.scheduler.schedule(instance, {
                if (verifyUsers[verifyCode] != null) {
                    verifyUsers.remove(verifyCode)
                }
            }, Config.data.verifyTimeout.toLong(), TimeUnit.SECONDS)
        } else {
            if (event.player.getDiscordUser == null) {
                event.player.disconnect(TextComponent("$prefix $removeKickMsg"))
                UUIDtoDiscordID.removeUser(event.player.uniqueId.toString())
            }
        }
    }

    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
        val uuid = UUIDtoDiscordID.data.filterValues { it == event.user.id }.map { it.key }.firstOrNull()

        if (uuid != null) {
            UUIDtoDiscordID.removeUser(uuid)
            instance.proxy.getPlayer(UUID.fromString(uuid))?.disconnect(
                TextComponent("$prefix $removeKickMsg".getColored)
            )
            instance.logger.info("mcUUID: $uuid, discord: ${event.user.name} has leaved guild")
        }
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "ping" -> event.reply("Pong!").queue()
            "verify" -> {
                if (
                    event.guild?.id == discordGuild.toString() &&
                    event.channel.id == discordChannel.toString()
                ) {
                    val code = event.getOption("code")!!.asString

                    if (code in verifyUsers.keys) {
                        val eb = EmbedBuilder()
                        eb.setTitle(Language.verifySuccessMsgTitle)
                        eb.setColor(Color(0x88C959))

                        eb.setDescription(
                            Language.verifySuccessMsgDesc
                                .replace("{nickname}", Bukkit.getOfflinePlayer(verifyUsers[code]!!).name!!)
                        )

                        eb.setImage("https://minotar.net/helm/${verifyUsers[code]!!}")
                        event.replyEmbeds(eb.build()).queue()

                        space.mori.mcdiscordverify.bukkit.config.UUIDtoDiscordID.addUser(verifyUsers[code]!!.toString(), event.member!!.id)
                        verifyUsers.remove(code)
                    } else {
                        event.reply(
                            Language.isNotRegisteredCode
                                .replace("{code}", code)
                        ).queue()
                    }
                }
            }
        }
    }

    internal fun main() {
        val thread = Thread {
            try {
                bot = JDABuilder.createDefault(discordToken)
                    .addEventListeners(Discord)
                    .setActivity(Activity.playing("Minecraft"))
                    .build().awaitReady()

                val guild = bot.getGuildById(discordGuild.toLong())

                guild?.updateCommands()?.addCommands(
                    Commands.slash("ping", "Pong!"),
                    Commands.slash("verify", "")
                        .addOption(OptionType.STRING, "code", "")
                )?.queue()

                if (guild == null) {
                    instance.logger.info("$prefix Guild is not found! plugin disabled.")
                    instance.proxy.pluginManager.plugins.remove(instance)
                }

            } catch (ex: Exception) {
                instance.logger.info(ex.message)
                instance.proxy.pluginManager.plugins.remove(instance)
                throw ex
            }
        }

        thread.start()
    }

    internal fun disable() {
        bot.shutdown()
    }

    private fun getRandomString(length: Int): String {
        val charPool = ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}