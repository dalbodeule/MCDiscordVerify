package space.mori.mcdiscordverify

import net.md_5.bungee.api.plugin.Plugin
import space.mori.mcdiscordverify.config.Config
import space.mori.mcdiscordverify.config.Language
import space.mori.mcdiscordverify.config.UUIDtoDiscordID
import space.mori.mcdiscordverify.discord.Discord
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URLConnection
import java.nio.file.FileSystemAlreadyExistsException
import java.nio.file.FileSystemException
import space.mori.mcdiscordverify.command.Discord as DiscordCommand


class MCDiscordVerify: Plugin() {
    companion object {
        lateinit var instance: MCDiscordVerify
    }

    override fun onEnable() {
        instance = this

        // language file initialize
        listOf(
            "lang/lang_en.json",
            "lang/lang_ko.json"
        ).forEach {
            saveResource(it, false)
        }

        // config initialize
        Config.load()
        UUIDtoDiscordID.load()
        Language.load()
        Language.save()

        // jda server initialize
        Discord.main()

        // command initialize
        proxy.pluginManager.registerCommand(this, DiscordCommand)

        // listener initialize
        proxy.pluginManager.registerListener(this, Discord)
    }

    override fun onDisable() {
        Discord.disable()

        Config.save()
        UUIDtoDiscordID.save()
        Language.save()
    }

    private fun getResource(filename: String?): InputStream? {
        requireNotNull(filename) { "Filename cannot be null" }
        try {
            val url = javaClass.classLoader.getResource(filename) ?: return null
            val connection: URLConnection = url.openConnection()
            connection.useCaches = false
            return connection.getInputStream()
        } catch (ex: IOException) {
        }
        return null
    }

    // https://github.com/libraryaddict/ConvertDatabase/blob/da3f315033f67e5a7efdf23bcfc609c5a13af26c/src/me/libraryaddict/convert/Bungee.java#L23

    private fun saveResource(resourcePath: String?, replace: Boolean) {
        if (resourcePath == null || resourcePath == "") {
            throw java.lang.IllegalArgumentException("ResourcePath cannot be null or empty")
        }

        val file = getResource(resourcePath.replace('\\', '/'))
            ?: throw IllegalArgumentException("The embedded resource '$resourcePath' cannot be found in $file")

        val outFile = File(this.dataFolder, resourcePath)
        val lastIndex = resourcePath.lastIndexOf('/')
        val outDir = File(this.dataFolder, resourcePath.substring(0, if (lastIndex >= 0) lastIndex else 0))

        if (!outDir.exists()) {
            outDir.mkdirs()
        }
        try {
            if (!outFile.exists() || replace) {
                val out = FileOutputStream(outFile)
                val buf = ByteArray(1024)
                var len: Int
                while (file.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
                out.close()
                file.close()

            } else {
                throw FileSystemAlreadyExistsException("Could not save ${outFile.name} to $outFile because ${outFile.name} already exists.")
            }
        } catch (ex: IOException) {
            throw FileSystemException("Could not save ${outFile.name} to $outFile")
        }
    }
}