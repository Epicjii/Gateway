package com.rose.gateway.minecraft.commands.framework.runner

/**
 * The context surrounding parsing a value
 *
 * @param A The type of the args in this context
 * @property arguments The type of the args in this context
 * @property currentIndex The next index to use from the raw args
 * @constructor Create a parse context
 */
data class ParseContext<A : CommandArgs<A>>(
    val arguments: A,
    val currentIndex: Int
)
