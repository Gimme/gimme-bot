package com.github.gimme.gimmebot.core

import com.github.gimme.gimmebot.core.command.CommandManager
import com.github.gimme.gimmebot.core.command.HelpCommand
import com.github.gimme.gimmebot.core.command.SimpleCommandManager
import com.github.gimme.gimmebot.core.data.config.BotConfig
import com.github.gimme.gimmebot.core.data.yaml.loadYamlFromResource
import com.github.gimme.gimmebot.core.data.DataManager
import com.github.gimme.gimmebot.core.data.requireResource
import com.github.gimme.gimmebot.core.plugin.GimmeBotPlugin
import mu.KotlinLogging
import java.io.File

/**
 * Represents a bot that can be started to perform tasks and respond to commands.
 */
open class GimmeBot : Bot {

    private val botResourcePath = "bot.yml"
    private val logger = KotlinLogging.logger {}

    private lateinit var botConfig: BotConfig
    private val plugins: MutableList<GimmeBotPlugin> = mutableListOf()

    /** If the bot is started. */
    var started = false
        private set

    /** The data manager. */
    lateinit var dataManager: DataManager

    /** The command manager */
    var commandManager: CommandManager = SimpleCommandManager()

    override fun start() {
        if (started) return
        started = true

        botConfig = requireResource(loadYamlFromResource(botResourcePath, BotConfig::class.java), botResourcePath)
        val name = botConfig.name

        dataManager = DataManager(File(name))

        commandManager.registerCommand(HelpCommand(commandManager))

        onStart()

        logger.info("$name started!")
    }

    override fun stop() {
        if (!started) return
        started = false

        plugins.forEach { plugin -> plugin.enabled = false }

        onStop()

        logger.info("${botConfig.name} stopped!")
    }

    /** Performs startup logic. */
    protected open fun onStart() {}

    /** Performs shutdown logic. */
    protected open fun onStop() {}

    /** Adds and enables the given [plugin]. */
    fun install(plugin: GimmeBotPlugin) {
        plugins.add(plugin)
        plugin.init(this)
        plugin.enabled = true
    }
}
