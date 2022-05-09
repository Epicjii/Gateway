package com.rose.gateway

import com.rose.gateway.bot.DiscordBot
import com.rose.gateway.configuration.PluginConfiguration
import com.rose.gateway.minecraft.CommandRegistry
import com.rose.gateway.minecraft.EventListeners
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

@Suppress("unused")
class GatewayPlugin : JavaPlugin() {
    init {
        startKoin {
            module {
                single { this }
            }
        }
    }

    val loader = classLoader
    val httpClient = HttpClient(CIO)
    val startTime = Clock.System.now()
    val configuration = PluginConfiguration(this)
    var discordBot = DiscordBot(this)
    private val eventListeners = EventListeners(this)
    private val commandRegistry = CommandRegistry(this)

    override fun onEnable() {
        Logger.logInfo("Starting Gateway!")

        GlobalContext.getOrNull()

        runBlocking {
            discordBot.start()
        }

        eventListeners.registerListeners(server)
        commandRegistry.registerCommands()

        Logger.logInfo("Gateway started!")
    }

    override fun onDisable() {
        Logger.logInfo("Stopping Gateway!")

        runBlocking {
            discordBot.stop()
        }

        Logger.logInfo("Gateway stopped!")
    }

    fun restartBot(): Boolean {
        httpClient.close()

        runBlocking {
            discordBot.stop()
            discordBot = DiscordBot(this@GatewayPlugin)
            discordBot.start()
        }

        return discordBot.bot != null
    }
}
