@file:Suppress("unused")

package de.miraculixx.chestprotect.events

import de.miraculixx.chestprotect.utils.ChestManager
import de.miraculixx.chestprotect.utils.toLiteLocation
import net.axay.kspigot.event.listen
import org.bukkit.block.Block
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityExplodeEvent

object ChestProtection {
    private val onExplosion = listen<EntityExplodeEvent> {
        it.blockList().removeAll(getProtectedBlocks(it.blockList()))
    }
    private val onExplosion2 = listen<BlockExplodeEvent> {
        it.blockList().removeAll(getProtectedBlocks(it.blockList()))
    }

    private fun getProtectedBlocks(list: List<Block>): List<Block> {
        return list.filter { block ->
            ChestManager.isProtected(block.location.toLiteLocation())
        }
    }
}