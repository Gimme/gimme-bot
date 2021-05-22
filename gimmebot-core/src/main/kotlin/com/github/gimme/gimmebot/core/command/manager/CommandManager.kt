package com.github.gimme.gimmebot.core.command.manager

import com.github.gimme.gimmebot.core.command.Command
import com.github.gimme.gimmebot.core.command.exception.CommandException
import com.github.gimme.gimmebot.core.command.sender.CommandSender

/**
 * Represents a command manager that handles the registration and execution of commands.
 *
 * @param R the command execution response type
 */
interface CommandManager<R> {

    /** Returns all registered commands. */
    val commands: Iterable<Command<*>>

    /**
     * Registers the given [command] to be executable through this manager with the specified [responseConverter] to
     * convert the result of the command execution from [T] to the uniform type [R].
     */
    fun <T> registerCommand(command: Command<T>, responseConverter: ((T) -> R)? = null)

    /** Returns the command at the [path] if it has been registered, else null. */
    fun getCommand(path: List<String>): Command<*>?

    /** Returns if a command has been registered at the [path]. */
    fun hasCommand(path: List<String>): Boolean

    /**
     * Returns the longest matching sub-set from the start of the [path] that leads to a command, or null if no match.
     */
    fun findCommand(path: List<String>): List<String>?

    /**
     * Returns the roots of all child branches under the command [path], or an empty set if no command exists under that
     * [path].
     */
    fun getBranches(path: List<String>): Set<String>

    /**
     * Executes the registered [command] with the [arguments] as the given [commandSender] and
     * returns the response.
     *
     * @throws CommandException if the command execution was unsuccessful
     */
    @Throws(CommandException::class)
    fun executeCommand(commandSender: CommandSender, command: Command<*>, arguments: List<String> = listOf()): R

    /**
     * Adds a listener to commands being registered with this command manager.
     */
    fun addRegisterCommandListener(registerCommandListener: RegisterCommandListener)

    /**
     * A listener to commands being registered.
     */
    interface RegisterCommandListener {
        /**
         * Does something when a command is registered.
         */
        fun onRegisterCommand(command: Command<*>)
    }
}
