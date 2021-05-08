package com.github.gimme.gimmebot.core.command

import com.github.gimme.gimmebot.core.command.manager.TextCommandManager
import com.github.gimme.gimmebot.core.command.channel.TextCommandChannel
import com.github.gimme.gimmebot.core.command.sender.CommandSender
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class TextCommandChannelTest {

    @ParameterizedTest
    @MethodSource("args")
    fun `should pass arguments`(input: String, expectedArgs: List<String>) {
        val commandManager = TextCommandManager()

        var actualArgs: List<String>? = null

        val command = object : DefaultBaseCommand("c") {
            override fun execute(commandSender: CommandSender, args: List<String>) {
                actualArgs = args
            }
        }

        commandManager.registerCommand(command)

        object : TextCommandChannel() {
            override fun onEnable() {
                parseInput(DUMMY_COMMAND_SENDER, input)
            }

            override fun onDisable() {}
        }.apply {
            registerCommandManager(commandManager)
            enable()
        }

        Assertions.assertNotNull(actualArgs)
        Assertions.assertIterableEquals(expectedArgs, actualArgs)
    }

    companion object {
        @JvmStatic
        fun args() = listOf(
            Arguments.of("c", emptyList<String>()),
            Arguments.of("c ", listOf("")),
            Arguments.of("c   one", listOf("", "", "one")),
            Arguments.of("c one", listOf("one")),
            Arguments.of("c one two", listOf("one", "two")),
            Arguments.of("c one two three", listOf("one", "two", "three")),
            Arguments.of("c \"one two\" three", listOf("one two", "three")),
        )
    }
}
