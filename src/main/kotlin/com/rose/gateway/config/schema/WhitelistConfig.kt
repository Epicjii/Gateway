package com.rose.gateway.config.schema

import com.rose.gateway.config.markers.CommonExtensionConfig
import com.rose.gateway.discord.bot.extensions.whitelist.WhitelistExtension
import com.rose.gateway.shared.serialization.SurrogateBasedSerializer
import com.rose.gateway.shared.serialization.SurrogateConverter
import kotlinx.serialization.Serializable

@Serializable(with = WhitelistConfigSerializer::class)
class WhitelistConfig(
    enabled: Boolean
) : CommonExtensionConfig(enabled, WhitelistExtension.extensionName())

@Serializable
data class WhitelistConfigSurrogate(val enabled: Boolean) {
    companion object : SurrogateConverter<WhitelistConfig, WhitelistConfigSurrogate> {
        override fun fromBase(base: WhitelistConfig): WhitelistConfigSurrogate = WhitelistConfigSurrogate(base.enabled)

        override fun toBase(surrogate: WhitelistConfigSurrogate): WhitelistConfig = WhitelistConfig(surrogate.enabled)
    }
}

object WhitelistConfigSerializer :
    SurrogateBasedSerializer<WhitelistConfig, WhitelistConfigSurrogate>(
        WhitelistConfigSurrogate.serializer(),
        WhitelistConfigSurrogate
    )
