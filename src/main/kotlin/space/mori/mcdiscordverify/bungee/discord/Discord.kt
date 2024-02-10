package space.mori.mcdiscordverify.bungee.discord

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority
import org.bukkit.Bukkit
import space.mori.mcdiscordverify.bukkit.MCDiscordVerify
import space.mori.mcdiscordverify.bungee.MCDiscordVerify.Companion.instance
import space.mori.mcdiscordverify.bungee.config.Config
import space.mori.mcdiscordverify.bungee.config.Config.discordChannel
import space.mori.mcdiscordverify.bungee.config.Config.discordGuild
import space.mori.mcdiscordverify.bungee.config.Config.discordToken
import space.mori.mcdiscordverify.bungee.config.Config.verifyTimeout
import space.mori.mcdiscordverify.bungee.config.Language
import space.mori.mcdiscordverify.bungee.config.Language.pingCmdDesc
import space.mori.mcdiscordverify.bungee.config.Language.pingCmdMsg
import space.mori.mcdiscordverify.bungee.config.Language.prefix
import space.mori.mcdiscordverify.bungee.config.Language.removeKickMsg
import space.mori.mcdiscordverify.bungee.config.Language.roleCmdDesc
import space.mori.mcdiscordverify.bungee.config.Language.roleCmdMsg
import space.mori.mcdiscordverify.bungee.config.Language.roleCmdOptDesc
import space.mori.mcdiscordverify.bungee.config.Language.serverCmdDesc
import space.mori.mcdiscordverify.bungee.config.Language.serverCmdMsg
import space.mori.mcdiscordverify.bungee.config.Language.serverCmdOptDesc
import space.mori.mcdiscordverify.bungee.config.Language.verifyCmdDesc
import space.mori.mcdiscordverify.bungee.config.Language.verifyCmdOptCode
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
            "ping" -> event.reply(pingCmdMsg).queue()
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

                        UUIDtoDiscordID.addUser(verifyUsers[code]!!.toString(), event.member!!.id)
                        event.guild?.getRoleById(Config.role.toLong())?.let { event.guild?.addRoleToMember(event.user, it) }
                        verifyUsers.remove(code)
                    } else {
                        event.reply(
                            Language.isNotRegisteredCode
                                .replace("{code}", code)
                        ).queue()
                    }
                }
            }
            "group" -> {
                val role = event.getOption("role")?.asRole

                if(role != null) {
                    Config.role = role.idLong
                    Config.save()

                    event.reply(
                        roleCmdMsg.replace("{role}", role.name)
                    ).queue()
                }
            }
            "server" -> {
                val channel = event.getOption("channel")?.asChannel

                if(channel != null) {
                    discordChannel = channel.idLong
                    discordGuild = channel.guild.idLong
                    Config.save()

                    event.reply(serverCmdMsg.replace("{channel}", channel.name)).queue()
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
                    Commands.slash("ping", pingCmdDesc),
                    Commands.slash("verify", verifyCmdDesc)
                        .addOption(OptionType.STRING, "code", verifyCmdOptCode),
                    Commands.slash("group", roleCmdDesc)
                        .addOption(OptionType.ROLE, "role", roleCmdOptDesc)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR, Permission.MANAGE_CHANNEL)),
                    Commands.slash("server", serverCmdDesc)
                        .addOption(OptionType.CHANNEL, "channel", serverCmdOptDesc)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR, Permission.MANAGE_CHANNEL))
                )?.queue()

                if (guild == null) {
                    instance.logger.info("$prefix Guild is not found! require /group commands.")
                    instance.logger.info("https://discord.com/api/oauth2/authorize?client_id={APPLICATION_ID}&permissions=552171874368&scope=bot")
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