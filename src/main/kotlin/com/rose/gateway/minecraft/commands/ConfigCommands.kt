package com.rose.gateway.minecraft.commands

import com.rose.gateway.configuration.PluginConfiguration
import com.rose.gateway.minecraft.commands.framework.CommandContext
import com.rose.gateway.minecraft.commands.framework.TabCompletionContext
import com.rose.gateway.shared.configurations.MinecraftConfiguration.primaryColor
import com.rose.gateway.shared.configurations.MinecraftConfiguration.secondaryColor
import com.rose.gateway.shared.configurations.MinecraftConfiguration.tertiaryColor
import com.rose.gateway.shared.configurations.MinecraftConfiguration.warningColor
import com.uchuhimo.konf.Item
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.CommandSender

class ConfigCommands(private val configuration: PluginConfiguration) {
    private val configStringMap = configuration.configurationStringMap

    fun setConfiguration(context: CommandContext): Boolean {
        val path = context.commandArguments[0] as String
        val configSpec = configuration.configurationStringMap.specificationFromString(path)

        if (configSpec == null) {
            context.sender.sendMessage("Configuration not found. Please try again.")
            return true
        }

        val newValue = context.commandArguments[1]
        setConfiguration(configSpec, newValue)

        context.sender.sendMessage(
            Component.join(
                Component.text(" "),
                Component.text(path, configuration.secondaryColor(), TextDecoration.ITALIC),
                Component.text("set to"),
                Component.text(newValue.toString(), configuration.tertiaryColor(), TextDecoration.ITALIC),
                Component.text("successfully!")
            )
        )

        return true
    }

    private fun <T> setConfiguration(item: Item<T>, newValue: Any?) {
        if (item.type.rawClass.isInstance(newValue)) {
            @Suppress("UNCHECKED_CAST")
            configuration[item] = newValue as T
        }
    }

    fun addConfiguration(context: CommandContext): Boolean {
        context.sender.sendMessage("config add is not yet implemented")
        return true
    }

    fun removeConfiguration(context: CommandContext): Boolean {
        context.sender.sendMessage("config remove is not yet implemented")
        return true
    }

    fun sendConfigurationHelp(sender: CommandSender, configSearchString: String): Boolean {
        val matchingConfigurations = configStringMap.matchingOrAllConfigurationStrings(configSearchString)

        if (matchingConfigurations.size == 1) {
            val path = matchingConfigurations[0]
            val matchingSpec = configStringMap.specificationFromString(path)!!

            sender.sendMessage(
                Component.join(
                    Component.newline(),
                    Component.join(
                        Component.empty(),
                        Component.text("Name: ", configuration.primaryColor()),
                        Component.text(path, configuration.tertiaryColor(), TextDecoration.ITALIC)
                    ),
                    Component.join(
                        Component.empty(),
                        Component.text("Type: ", configuration.primaryColor()),
                        Component.text(matchingSpec.type.rawClass.simpleName),
                        Component.text(if (matchingSpec.nullable) "?" else "", configuration.warningColor())
                    ),
                    Component.join(
                        Component.empty(),
                        Component.text("Current Value: ", configuration.primaryColor()),
                        Component.text(configuration[matchingSpec].toString())
                    ),
                    Component.join(
                        Component.empty(),
                        Component.text("Description: ", configuration.primaryColor()),
                        Component.text(matchingSpec.description)
                    ),
                    Component.text(
                        "View All Configurations",
                        configuration.primaryColor(),
                        TextDecoration.UNDERLINED,
                        TextDecoration.ITALIC
                    )
                        .hoverEvent(HoverEvent.showText(Component.text("Click to view all configurations.")))
                        .clickEvent(ClickEvent.runCommand("/gateway config help"))
                )
            )
        } else {
            val configurations = matchingConfigurations.map { config ->
                Component.join(
                    Component.empty(),
                    Component.text("* "),
                    Component.text(config, configuration.primaryColor(), TextDecoration.ITALIC)
                        .hoverEvent(
                            HoverEvent.showText(
                                Component.join(
                                    Component.empty(),
                                    Component.text("Get help for "),
                                    Component.text(config, configuration.tertiaryColor(), TextDecoration.ITALIC)
                                )
                            )
                        )
                        .clickEvent(ClickEvent.runCommand("/gateway config help $config"))
                )
            }

            sender.sendMessage(
                Component.join(
                    Component.newline(),
                    Component.text("Available Configurations: ", configuration.tertiaryColor()),
                    Component.join(Component.newline(), configurations)
                )
            )
        }

        return true
    }

    fun configCompletion(context: TabCompletionContext): List<String> {
        val currentConfigurationArgument = context.parsedArguments.last() as String

        return configStringMap.matchingOrAllConfigurationStrings(currentConfigurationArgument)
    }
}
