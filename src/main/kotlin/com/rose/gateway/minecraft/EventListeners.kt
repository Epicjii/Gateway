package com.rose.gateway.minecraft

import com.rose.gateway.GatewayPlugin
import com.rose.gateway.minecraft.chat.ChatListener
import com.rose.gateway.minecraft.users.UserCount
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object EventListeners : KoinComponent {
    val plugin: GatewayPlugin by inject()
    val server = plugin.server

    fun registerListeners() {
        server.pluginManager.registerEvents(ChatListener(), plugin)
        server.pluginManager.registerEvents(UserCount(), plugin)
    }
}
