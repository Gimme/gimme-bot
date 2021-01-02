package com.github.gimme.gimmebot.core.command.medium

import com.github.gimme.gimmebot.core.command.CommandException
import com.github.gimme.gimmebot.core.command.CommandSender
import com.github.gimme.gimmebot.core.command.ErrorCode
import com.github.gimme.gimmebot.core.command.HelpCommand
import com.github.gimme.gimmebot.core.command.manager.CommandManager
import com.github.gimme.gimmebot.core.command.manager.TextCommandManager

/**
 * Represents a text-based command medium with, for example a chat box or a command line interface.
 *
 * @property commandPrefix prefix required for the input to be recognized as a command
 */
abstract class TextCommandMedium(
    includeHelpCommand: Boolean = true,
    includeConsoleListener: Boolean = true,
    open var commandPrefix: String? = null,
) : BaseCommandMedium<String?>(TextCommandManager(), includeConsoleListener) {

    init {
        if (includeHelpCommand) {
            commandManager.registerCommand(HelpCommand(this)) {
                val sb = StringBuilder("Commands:")

                it.forEach { command ->
                    sb.append("\n|  ${command.usage}")
                }

                sb.toString()
            }
        }
    }

    override fun parseInput(sender: CommandSender, input: String) {
        val commandInput = validatePrefix(input) ?: return

        super.parseInput(sender, input)

        val message = try { // Execute the command
            executeCommand(sender, commandInput)
        } catch (e: CommandException) { // The command returned with an error
            e.message
        }

        // Send back the response
        respond(sender, message)
    }

    override fun respond(commandSender: CommandSender, response: String?) {
        if (response.isNullOrEmpty()) return

        super.respond(commandSender, response)

        commandSender.sendMessage(response)
    }

    @Throws(CommandException::class)
    private fun executeCommand(commandSender: CommandSender, commandInput: String): String? {
        var bestMatchCommandName: String? = null

        registeredCommandManagers.forEach {
            val foundCommand = it.commandManager.commandCollection.findCommand(commandInput.split(" "))

            foundCommand?.name?.let { name ->
                if (name.length > bestMatchCommandName?.length ?: -1) {
                    bestMatchCommandName = foundCommand.name
                }
            }
        }

        val commandName = bestMatchCommandName ?: throw ErrorCode.NOT_A_COMMAND.createException()

        // Remove command name, leaving only the arguments
        val argsInput = commandInput.removePrefix(commandName)

        // Split into words on spaces, ignoring spaces between two quotation marks
        val args = argsInput.split("\\s(?=(?:[^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex())
            .map { s -> s.replace("\"", "") }.drop(1)

        return executeCommand(commandSender, commandName, args)
    }

    /**
     * Registers the given [commandManager] making the contained commands executable through this medium with the
     * results converted to strings.
     */
    fun <T> registerCommandManager(commandManager: CommandManager<T>) {
        super.registerCommandManager(commandManager) { it?.toString() }
    }

    /**
     * If the given input starts with the [commandPrefix], returns a copy of the input with the prefix removed.
     * Otherwise, returns null.
     */
    private fun validatePrefix(input: String): String? {
        val prefix = commandPrefix

        if (prefix.isNullOrEmpty()) return input

        // Has to start with the command prefix
        if (!input.startsWith(prefix)) return null
        // Remove prefix
        return input.removePrefix(prefix)
    }
}
