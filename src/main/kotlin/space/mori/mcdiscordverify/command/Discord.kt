package space.mori.mcdiscordverify.command

object Discord: CommandBase() {
    override val SubCommands: Map<String, SubCommand> = mutableListOf<SubCommand>(

    ).associateBy { it.name }
}