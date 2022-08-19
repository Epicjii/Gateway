package com.rose.gateway.config

import com.rose.gateway.config.schema.Config
import com.rose.gateway.discord.bot.DiscordBot
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.reflect.KType

class PluginConfig : KoinComponent {
    private val bot: DiscordBot by inject()
    private val stringMap: ConfigStringMap by inject()

    private val configurationLoader = GatewayConfigLoader()
    var config: Config = runBlocking {
        configurationLoader.loadOrCreateConfig()
    }

    suspend fun reloadConfiguration(): Boolean {
        config = configurationLoader.loadOrCreateConfig()

        return if (notLoaded()) {
            bot.stop()

            false
        } else {
            true
        }
    }

    fun saveConfiguration() {
        configurationLoader.saveConfig(config)
    }

    fun notLoaded(): Boolean = config == DEFAULT_CONFIG

    fun allItems(): List<Item<*>> = stringMap.itemMap.values.toList()

    operator fun get(item: String): Item<*>? {
        return stringMap.fromString(item)
    }

    operator fun <T : Any> get(type: KType, item: String): Item<T>? {
        val match = get(item)

        return if (match != null && match.type == type) {
            @Suppress("UNCHECKED_CAST")
            match as Item<T>
        } else null
    }
}
