package dev.gimme.gimmeapi.command

import dev.gimme.gimmeapi.command.node.CommandNode
import dev.gimme.gimmeapi.command.parameter.CommandParameter
import dev.gimme.gimmeapi.command.parameter.CommandParameterSet
import dev.gimme.gimmeapi.command.sender.CommandSender

val DUMMY_COMMAND_SENDER = object : CommandSender {
    override val name = "dummy"

    override fun sendMessage(message: String) {}
}

val DUMMY_COMMAND = object : DefaultBaseCommand("test") {}

open class DefaultBaseCommand(
    override val name: String,
    override val parent: CommandNode? = null,
) : Command<Any> {

    override fun execute(commandSender: CommandSender, args: Map<CommandParameter, Any?>): Any = Unit
    override var summary = ""
    override var description = ""
    override val usage = ""
    override val parameters = CommandParameterSet()
    override var aliases = setOf<String>()
}
