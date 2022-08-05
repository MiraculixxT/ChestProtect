@file:Suppress("unused")

package de.miraculixx.chestprotect.events

import de.miraculixx.chestprotect.utils.ChestManager
import de.miraculixx.chestprotect.utils.toLiteLocation
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.broadcast
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

object ChestPlace {
    private val onPlace = listen<BlockPlaceEvent> {
        broadcast("place")
        val player = it.player
        val uuid = player.uniqueId
        val block = it.block
        val loc = block.location
        when (block.type) {
            Material.CHEST, Material.TRAPPED_CHEST -> {
                if (!ChestManager.hasAccess(uuid, loc.add(1.0, .0, .0).toLiteLocation())) {
                    ChestManager.noAccess(player, loc.block)
                    it.isCancelled = true
                } else if (!ChestManager.hasAccess(uuid, loc.add(-2.0, .0, .0).toLiteLocation())) {
                    ChestManager.noAccess(player, loc.block)
                    it.isCancelled = true
                } else if (!ChestManager.hasAccess(uuid, loc.add(1.0, .0, 1.0).toLiteLocation())) {
                    ChestManager.noAccess(player, loc.block)
                    it.isCancelled = true
                } else if (!ChestManager.hasAccess(uuid, loc.add(.0, .0, -2.0).toLiteLocation())) {
                    ChestManager.noAccess(player, loc.block)
                    it.isCancelled = true
                }
                ChestManager.addChest(loc.toLiteLocation(), uuid)
            }

            Material.HOPPER, Material.RAIL, Material.POWERED_RAIL, Material.ACTIVATOR_RAIL, Material.DETECTOR_RAIL -> {
                if (!ChestManager.hasAccess(uuid, loc.add(.0, 1.0, .0).toLiteLocation())) {
                    ChestManager.noAccess(player, loc.block)
                    it.isCancelled = true
                }
            }

            else -> {}
        }
    }

    private val onBreak = listen<BlockBreakEvent> {
        val block = it.block
        val type = block.type
        if (type != Material.CHEST && type != Material.TRAPPED_CHEST) return@listen
        val player = it.player
        val loc = block.location.toLiteLocation()
        if (ChestManager.hasAccess(player.uniqueId, loc)) ChestManager.removeChest(loc)
        else it.isCancelled = true
    }
}