package com.rose.gateway.minecraft.commands.arguments

import com.rose.gateway.configuration.PluginConfiguration
import com.rose.gateway.minecraft.commands.converters.BooleanArg
import com.rose.gateway.minecraft.commands.converters.StringArg
import com.rose.gateway.minecraft.commands.converters.boolean
import com.rose.gateway.minecraft.commands.converters.string
import com.rose.gateway.minecraft.commands.converters.typedConfigItem
import com.rose.gateway.minecraft.commands.framework.runner.RunnerArg
import com.rose.gateway.minecraft.commands.framework.runner.RunnerArguments
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.reflect.KType
import kotlin.reflect.typeOf

open class ConfigArgs<
    T : Any,
    A : ConfigArgs<T, A, R>,
    R : RunnerArg<T, A, R>
    >(
    configType: KType,
    valueArg: A.() -> RunnerArg<T, A, R>
) : KoinComponent, RunnerArguments<A>() {
    private val config: PluginConfiguration by inject()

    val item by typedConfigItem<T, A> {
        name = "CONFIG_ITEM"
        description = "The item to modify."
        type = configType
        completer = {
            configItemsWithType(config, configType)
        }
    }

    @Suppress("UNCHECKED_CAST", "LeakingThis")
    val value by (this as A).valueArg()

    private fun configItemsWithType(config: PluginConfiguration, type: KType): List<String> {
        val items = config.allItems()
        val matchedItems = items.filter {
            val itemType = it.type()

            itemType == type
        }

        return matchedItems.map { it.path }
    }
}

class ConfigBooleanArgs : ConfigArgs<Boolean, ConfigBooleanArgs, BooleanArg<ConfigBooleanArgs>>(
    typeOf<Boolean>(),
    {
        boolean {
            name = "VALUE"
            description = "Boolean to use the item with."
        }
    }
)

class ConfigStringArgs : ConfigArgs<String, ConfigStringArgs, StringArg<ConfigStringArgs>>(
    typeOf<String>(),
    {
        string {
            name = "VALUE"
            description = "String to use the item with."
        }
    }
)
