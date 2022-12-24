package com.rose.gateway.minecraft.commands.runners

import com.rose.gateway.config.Item
import com.rose.gateway.minecraft.commands.arguments.ConfigArgs
import com.rose.gateway.minecraft.commands.arguments.ConfigListArgs
import com.rose.gateway.minecraft.commands.framework.data.context.CommandExecuteContext
import com.rose.gateway.minecraft.commands.framework.runner.ArgParser
import com.rose.gateway.minecraft.commands.parsers.StringParser
import com.rose.gateway.minecraft.component.ColorComponent
import com.rose.gateway.minecraft.component.italic
import com.rose.gateway.minecraft.component.joinSpace
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender

/**
 * Commands to modify the plugin config
 */
object ConfigCommands {
    /**
     * Command that sets the value of a config item
     *
     * @param ConfigValueType The type of the config value to modify
     * @param ArgsType The type of the config args in the command context
     * @param ValueParserType The type of the parser for the config value
     * @param context The command context with the config args
     * @return Whether the command succeeded
     */
    fun <
        ConfigValueType,
        ArgsType : ConfigArgs<ConfigValueType, ArgsType, ValueParserType>,
        ValueParserType : ArgParser<ConfigValueType, ArgsType, ValueParserType>> setConfig(
        context: CommandExecuteContext<ArgsType>
    ): Boolean {
        val args = context.args
        val item = args.item
        val value = args.value ?: return false

        item.value = value
        sendConfirmation(context.bukkit.sender, item, value)

        return true
    }

    /**
     * Sends confirmation of setting a config's value to the sender
     *
     * @param T The type of the config item
     * @param sender The sender to receive the confirmation
     * @param item The config item that was set
     * @param value The config value that was set
     */
    private fun <T> sendConfirmation(sender: CommandSender, item: Item<T>, value: T) {
        sender.sendMessage(
            joinSpace(
                ColorComponent.tertiary(item.path).italic(),
                Component.text("set to"),
                ColorComponent.secondary(value.toString()).italic(),
                Component.text("successfully!")
            )
        )
    }

    /**
     * Command that adds value to a config list item
     *
     * @param ConfigValueType The type of the config item
     * @param ListArgsType The type of the list args in the command context
     * @param ValueParserType The type of the parser for the config values
     * @param context The command context with the config list args
     * @return Whether the command succeeded
     */
    fun <
        ConfigValueType,
        ListArgsType : ConfigListArgs<ConfigValueType, ListArgsType, ValueParserType>,
        ValueParserType : StringParser<ListArgsType>> addConfiguration(
        context: CommandExecuteContext<ListArgsType>
    ): Boolean {
        val configItem = context.args.item
        val values = context.args.value

        addToConfiguration(configItem, values)
        sendAddConfirmation(context.bukkit.sender, configItem, values)

        return true
    }

    /**
     * Adds values to a config item
     *
     * @param T The type of the config item
     * @param item The config item to add values to
     * @param additionalValues The values to add
     */
    private fun <T> addToConfiguration(item: Item<List<T>>, additionalValues: List<T>) {
        val currentValues = item.value
        val newValues = currentValues + additionalValues

        item.value = newValues
    }

    /**
     * Sends confirmation of having added values to a list config
     *
     * @param T The type of the config item
     * @param sender The sender to receive the confirmation
     * @param item The config item that was added to
     * @param values The values that were added
     */
    private fun <T> sendAddConfirmation(sender: CommandSender, item: Item<T>, values: T) {
        sender.sendMessage(
            joinSpace(
                ColorComponent.secondary(values.toString()).italic(),
                Component.text("added to"),
                ColorComponent.tertiary(item.path).italic(),
                Component.text("successfully!")
            )
        )
    }

    /**
     * Command that removes values from a config list item
     *
     * @param ConfigValueType The type of the config item
     * @param ListArgsType The type of the list args in the command context
     * @param ValueParserType The type of the parser for the config values
     * @param context The command context with the config list args
     * @return Whether the command succeeded
     */
    fun <
        ConfigValueType,
        ListArgsType : ConfigListArgs<ConfigValueType, ListArgsType, ValueParserType>,
        ValueParserType : StringParser<ListArgsType>> removeConfiguration(
        context: CommandExecuteContext<ListArgsType>
    ): Boolean {
        val configItem = context.args.item
        val values = context.args.value

        removeFromConfiguration(configItem, values)
        sendRemoveConfirmation(context.bukkit.sender, configItem, values)

        return true
    }

    /**
     * Removes values from a config item
     *
     * @param T The type of the config item
     * @param item The config item to remove values from
     * @param valuesToBeRemoved The values to remove
     */
    private fun <T> removeFromConfiguration(item: Item<List<T>>, valuesToBeRemoved: List<T>) {
        val currentValues = item.value
        val newValues = currentValues - valuesToBeRemoved.toSet()

        item.value = newValues
    }

    /**
     * Sends confirmation of having removed values from a list config
     *
     * @param T The type of the config item
     * @param sender The sender to receive the confirmation
     * @param item The config item that was removed from
     * @param values The values that were removed
     */
    private fun <T> sendRemoveConfirmation(sender: CommandSender, item: Item<T>, values: T) {
        sender.sendMessage(
            joinSpace(
                ColorComponent.secondary(values.toString()).italic(),
                Component.text("removed from"),
                ColorComponent.tertiary(item.path).italic(),
                Component.text("successfully!")
            )
        )
    }
}
