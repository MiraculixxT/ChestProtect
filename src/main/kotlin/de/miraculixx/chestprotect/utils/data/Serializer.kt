package de.miraculixx.chestprotect.utils.data

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ChestData(
    val location: LiteLocation,
    var protected: Boolean,
    var visual: Boolean,
    @Serializable(with = UUIDSerializer::class) val owner: UUID,
    val trusted: MutableList<@Serializable(with = UUIDSerializer::class) UUID>
)

@Serializable
data class LiteLocation(val x: Int, val y: Int, val z: Int, val world: String)
