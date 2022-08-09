package com.rose.gateway.minecraft.chat.processing.tokens.result

import com.rose.gateway.config.PluginConfig
import com.rose.gateway.config.extensions.primaryColor
import com.rose.gateway.discord.bot.DiscordBot
import com.rose.gateway.discord.bot.DiscordBotConstants.MEMBER_QUERY_MAX
import com.rose.gateway.minecraft.component.ComponentBuilder
import dev.kord.common.annotation.KordExperimental
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserMentionBuilder : KoinComponent {
    private val config: PluginConfig by inject()
    private val bot: DiscordBot by inject()

    private val resultBuilder = ResultBuilder()

    @OptIn(KordExperimental::class)
    suspend fun createUserMention(nameString: String): TokenProcessingResult {
        for (guild in bot.botGuilds) {
            val members = guild.getMembers(nameString, MEMBER_QUERY_MAX)
            val firstMember = members.firstOrNull() ?: break
            val discordText = "<@!${firstMember.id}>"

            return TokenProcessingResult(
                ComponentBuilder.atDiscordMemberComponent(firstMember, config.primaryColor()),
                discordText
            )
        }

        return resultBuilder.errorResult("@$nameString")
    }
}
