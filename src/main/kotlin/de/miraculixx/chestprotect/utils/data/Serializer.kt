package de.miraculixx.chestprotect.utils.data

import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.block.Block
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
data class LiteLocation(val x: Int, val y: Int, val z: Int, val world: String) {
    override fun toString(): String {
        return "$x $y $z"
    }

    fun getBlock(): Block? {
        return Bukkit.getWorld(world)?.getBlockAt(x, y, z)
    }
}
