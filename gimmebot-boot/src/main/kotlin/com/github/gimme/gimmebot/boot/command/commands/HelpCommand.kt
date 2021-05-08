package com.github.gimme.gimmebot.boot.command.commands

import com.github.gimme.gimmebot.boot.command.SimpleCommand
import com.github.gimme.gimmebot.boot.command.executor.CommandExecutor
import com.github.gimme.gimmebot.core.command.channel.CommandChannel

/**
 * Displays a list of available commands.
 */
class HelpCommand(private val commandChannel: CommandChannel<*>) : SimpleCommand<List<HelpCommand.CommandHelp>>("help") {

    /** Prints available commands. */
    @CommandExecutor
    fun printCommands(): List<CommandHelp> {

        val list: MutableList<CommandHelp> = mutableListOf()

        commandChannel.commandManagers.forEach { commandManager ->
            commandManager.commandCollection.getCommands().forEach {
                list.add(CommandHelp(it.name, it.usage))
            }
        }

        return list
    }

    /**
     * Help info about a command.
     *
     * @property name the name of the command
     * @property usage the usage info of the command
     */
    data class CommandHelp(
        val name: String,
        val usage: String,
    )
}