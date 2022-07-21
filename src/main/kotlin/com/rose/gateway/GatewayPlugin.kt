package com.rose.gateway

import com.rose.gateway.bot.DiscordBot
import com.rose.gateway.configuration.ConfigurationStringMap
import com.rose.gateway.configuration.PluginConfiguration
import com.rose.gateway.minecraft.CommandRegistry
import com.rose.gateway.minecraft.EventListeners
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module

@Suppress("unused")
class GatewayPlugin : JavaPlugin(), KoinComponent {
    init {
        startKoin {
            modules(
                module {
                    single { this@GatewayPlugin }
                    single { PluginConfiguration() }
                    single { ConfigurationStringMap() }
                    single { DiscordBot() }
                    single { HttpClient(CIO) }
                }
            )
        }
    }

    val bot: DiscordBot by inject()

    val startTime = Clock.System.now()
    val loader = classLoader

    override fun onEnable() {
        Logger.info("Starting Gateway!")

        runBlocking {
            bot.start()
        }

        EventListeners.registerListeners(server)
        CommandRegistry.registerCommands()

        Logger.info("Gateway started!")
    }

    override fun onDisable() {
        Logger.info("Stopping Gateway!")

        runBlocking {
            bot.stop()
        }

        Logger.info("Gateway stopped!")
    }
}
