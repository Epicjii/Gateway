package com.rose.gateway.discord.bot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.loadModule
import com.rose.gateway.GatewayPlugin
import com.rose.gateway.config.PluginConfig
import com.rose.gateway.config.extensions.botChannels
import com.rose.gateway.config.extensions.botToken
import com.rose.gateway.discord.bot.client.ClientInfo
import com.rose.gateway.discord.bot.presence.DynamicPresence
import com.rose.gateway.minecraft.logging.Logger
import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.exception.KordInitializationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DiscordBot : KoinComponent {
    private val plugin: GatewayPlugin by inject()
    private val config: PluginConfig by inject()

    val botChannels = mutableSetOf<TextChannel>()
    val botGuilds = mutableSetOf<Guild>()
    private var job: Job? = null
    var botStatus = BotStatus.NOT_STARTED

    var bot = buildBot()
    val presence = DynamicPresence()

    private fun buildBot(): ExtensibleBot? {
        return try {
            if (config.notLoaded()) {
                botStatus = BotStatus.STOPPED because "No valid configuration is loaded."
                null
            } else {
                runBlocking {
                    createBot(config.botToken())
                }
            }
        } catch (e: KordInitializationException) {
            botStatus = BotStatus.STOPPED because e.localizedMessage
            null
        }
    }

    private suspend fun createBot(token: String): ExtensibleBot {
        return ExtensibleBot(token) {
            hooks {
                kordShutdownHook = false

                afterKoinSetup {
                    loadModule {
                        single { plugin }
                        single { config }
                    }
                }
            }
            presence {
                since = plugin.startTime
                playing(presence.presenceForPlayerCount())
            }
            applicationCommands {
                enabled = true
            }
            extensions {
                extensions.addAll(
                    DiscordBotConstants.BOT_EXTENSIONS.map { extension -> extension.extensionConstructor() }
                )
            }
        }
    }

    fun kordClient(): Kord? = bot?.getKoin()?.get()

    suspend fun start() {
        if (bot == null) return

        botStatus = BotStatus.STARTING

        unloadDisabledExtensions()
        fillBotChannels()
        launchBotInNewThread()

        Logger.info("Bot ready!")
    }

    private suspend fun unloadDisabledExtensions() {
        for (extension in DiscordBotConstants.BOT_EXTENSIONS) {
            if (!extension.isEnabled(plugin)) bot!!.unloadExtension(extension.extensionName())
        }
    }

    suspend fun fillBotChannels() {
        val validBotChannels = config.botChannels()

        botChannels.clear()
        botGuilds.clear()

        kordClient()?.guilds?.collect { guild ->
            guild.channels.collect { channel ->
                if (
                    ClientInfo.hasChannelPermissions(channel, DiscordBotConstants.REQUIRED_PERMISSIONS) &&
                    channel is TextChannel &&
                    channel.name in validBotChannels
                ) {
                    botChannels.add(channel)
                    botGuilds.add(guild)
                }
            }
        }
    }

    private suspend fun launchBotInNewThread() {
        job = try {
            botStatus = BotStatus.RUNNING
            bot?.startAsync()
        } catch (error: KordInitializationException) {
            val message = "An error occurred while running bot: ${error.message}"
            botStatus = BotStatus.STOPPED because message
            Logger.warning("Could not start Discord bot. Check status for info.")
            null
        }
    }

    suspend fun stop() {
        botStatus = BotStatus.STOPPING

        bot?.stop()
        job?.join()

        botStatus = BotStatus.STOPPED
    }

    suspend fun close() {
        botStatus = BotStatus.STOPPING

        bot?.close()
        job?.join()

        botStatus = BotStatus.STOPPED
    }

    suspend fun rebuild() {
        close()
        bot = buildBot()
        start()
    }
}
