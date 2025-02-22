package com.rose.gateway.minecraft.logging

import com.rose.gateway.GatewayPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Helper object that provides logging functionality throughout Gateway
 */
object Logger : KoinComponent {
    private val plugin: GatewayPlugin by inject()
    private val pluginLogger = plugin.logger

    /**
     * Logs a message with level "INFO"
     *
     * @param message The message to log
     */
    fun info(message: String) {
        pluginLogger.info(message)
    }

    /**
     * Logs a message with level "WARNING"
     *
     * @param message The message to log
     */
    fun warning(message: String) {
        pluginLogger.warning(message)
    }
}
