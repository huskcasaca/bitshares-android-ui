package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.*

@Serializable
data class K101_BaseObject(
    @SerialName(KEY_ID)
    override val id: K101_BaseIdType = emptyIdType(),
) : AbstractObject(), K101_BaseType
