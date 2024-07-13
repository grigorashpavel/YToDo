package api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BotInfoResponse(
    @SerialName("ok")
    val ok: Boolean,
    @SerialName("result")
    val result: ResultWrapper
)

@Serializable
data class ResultWrapper(
    @SerialName("id")
    val id: Long,
    @SerialName("is_bot")
    val isBot: Boolean,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("username")
    val userName: String?,
    @SerialName("can_join_groups")
    val canJoinGroups: Boolean,
    @SerialName("can_read_all_group_messages")
    val canReadAllGroupMessages: Boolean,
    @SerialName("supports_inline_queries")
    val supportInlineQueries: Boolean,
    @SerialName("can_connect_to_business")
    val canConnectToBusiness: Boolean
)