package com.rose.gateway.minecraft.chat.processing.tokens

import com.rose.gateway.GatewayPlugin
import com.rose.gateway.minecraft.chat.processing.tokens.result.RoleMentionBuilder
import com.rose.gateway.minecraft.chat.processing.tokens.result.TokenProcessingResult
import com.rose.gateway.shared.processing.TokenProcessor
import guru.zoroark.lixy.LixyToken
import guru.zoroark.lixy.LixyTokenType

class RoleMentionTokenProcessor(plugin: GatewayPlugin) : TokenProcessor<TokenProcessingResult> {
    companion object {
        const val ROLE_MENTION_START_INDEX = 3
    }

    private val mentionBuilder = RoleMentionBuilder(plugin)

    override fun tokenType(): LixyTokenType {
        return ChatComponent.ROLE_MENTION
    }

    override fun regexPattern(): String {
        return "@R=[^\\s@]+"
    }

    override suspend fun process(token: LixyToken): TokenProcessingResult {
        val roleString = token.string.substring(ROLE_MENTION_START_INDEX)

        return mentionBuilder.createRoleMention(roleString)
    }
}
