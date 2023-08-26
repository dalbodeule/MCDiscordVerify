package space.mori.mcdiscordverify.bukkit.config

import space.mori.mcdiscordverify.bukkit.MCDiscordVerify.Companion.instance
import space.mori.mcdiscordverify.utils.parseJSON
import space.mori.mcdiscordverify.utils.serializeJSON
import java.nio.file.Files
import java.nio.file.Path

open class ConfigBase<T: Any>(
    open var data: T,
    private val target: Path = instance.dataFolder.toPath().resolveSibling("dummy.json")
) {
    internal fun load() {
        if (this.target.toFile().exists()) {
            data = parseJSON(
                Files.readAllBytes(this.target).toString(Charsets.UTF_8),
                data::class.java
            )
        }
    }

    internal fun save() {
        if (!this.target.toFile().exists()) {
            Files.createDirectories(this.target.parent)
            Files.createFile(this.target)
        }

        Files.write(this.target, data.serializeJSON().toByteArray())
    }
}

fun getTarget(src: String): Path {
    return instance.dataFolder.toPath().resolve(src)
}
fun getTarget(src: Path): Path {
    return instance.dataFolder.toPath().resolve(src)
}
