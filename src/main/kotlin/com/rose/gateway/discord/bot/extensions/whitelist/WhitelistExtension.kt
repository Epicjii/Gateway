package com.rose.gateway.discord.bot.extensions.whitelist

import com.kotlindiscord.kord.extensions.commands.application.slash.ephemeralSubCommand
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.rose.gateway.config.PluginConfig
import com.rose.gateway.config.extensions.whitelistExtensionEnabled
import com.rose.gateway.discord.bot.extensions.ExtensionToggle
import com.rose.gateway.minecraft.logging.Logger
import com.rose.gateway.minecraft.whitelist.Whitelist
import com.rose.gateway.minecraft.whitelist.WhitelistState
import org.koin.core.component.inject

/**
 * A Discord bot extension providing Discord commands to modify the whitelist.
 *
 * @constructor Create a "whitelist extension".
 */
class WhitelistExtension : Extension() {
    companion object : ExtensionToggle {
        private val config: PluginConfig by inject()

        override fun extensionName(): String = "whitelist"

        override fun extensionConstructor(): () -> Extension = ::WhitelistExtension

        override fun isEnabled(): Boolean = config.whitelistExtensionEnabled()
    }

    override val name: String = extensionName()

    override suspend fun setup() {
        ephemeralSlashCommand {
            name = "whitelist"
            description = "Runs an operation that relates to the server whitelist."

            ephemeralSubCommand(::WhitelistArguments) {
                name = "add"
                description = "Adds a player to the whitelist."

                action {
                    Logger.info("${user.asUserOrNull()?.username} added ${arguments.username} to whitelist!")

                    val status = when (Whitelist.addToWhitelist(arguments.username)) {
                        WhitelistState.STATE_MODIFIED -> "${arguments.username} successfully added to whitelist."
                        WhitelistState.STATE_SUSTAINED -> "${arguments.username} already exists in whitelist."
                        WhitelistState.STATE_INVALID -> "An error occurred adding ${arguments.username} to whitelist."
                    }

                    respond {
                        content = status
                    }
                }
            }

            ephemeralSubCommand(::WhitelistArguments) {
                name = "remove"
                description = "Removes a player from the whitelist."

                action {
                    Logger.info("${user.asUserOrNull()?.username} removed ${arguments.username} from whitelist!")

                    val status = when (Whitelist.removeFromWhitelist(arguments.username)) {
                        WhitelistState.STATE_MODIFIED -> "${arguments.username} successfully removed from whitelist."
                        WhitelistState.STATE_SUSTAINED -> "${arguments.username} does not exist in whitelist."
                        WhitelistState.STATE_INVALID -> "Error occurred removing ${arguments.username} from whitelist."
                    }

                    respond {
                        content = status
                    }
                }
            }

            ephemeralSubCommand {
                name = "list"
                description = "Lists all currently whitelisted players."

                action {
                    Logger.info("${user.asUserOrNull()?.username} requested list of whitelisted players!")

                    val whitelistedPlayers = Whitelist.whitelistedPlayersAsString()
                    val response = if (whitelistedPlayers.isEmpty()) "No players currently whitelisted."
                    else "Players currently whitelisted: $whitelistedPlayers"

                    respond {
                        content = response
                    }
                }
            }
        }
    }
}
