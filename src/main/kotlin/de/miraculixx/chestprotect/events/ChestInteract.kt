package de.miraculixx.chestprotect.events

import de.miraculixx.chestprotect.utils.*
import de.miraculixx.chestprotect.utils.ChestManager.getChest
import de.miraculixx.chestprotect.utils.ChestManager.hasAccess
import de.miraculixx.chestprotect.utils.ChestManager.isTransparent
import de.miraculixx.chestprotect.utils.ChestManager.noAccess
import de.miraculixx.chestprotect.utils.gui.SettingsInv
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.broadcast
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Chest
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

object ChestInteract {
    private val fakeChestTitle = cmp("Chest ") + cmp("(Protected)", cHighlight)

    @Suppress("unused")
    private val onChestClick = listen<PlayerInteractEvent> {
        val block = it.clickedBlock ?: return@listen
        val type = block.type
        if (type != Material.CHEST && type != Material.TRAPPED_CHEST) return@listen
        val player = it.player
        val gamemode = player.gameMode

        // Whitelist Creative and Spectator Players to access
        // & ignore shift placements for building
        if (gamemode != GameMode.SURVIVAL && gamemode != GameMode.ADVENTURE) return@listen
        if (player.isSneaking && it.isBlockInHand) return@listen

        // Shift click to get information about the chest
        val loc = block.location.toLiteLocation()
        broadcast("Chest click")
        if (player.isSneaking && it.hand == EquipmentSlot.HAND) {
            broadcast("show info")
            val chest = getChest(loc) ?: return@listen
            broadcast("show info /found")
            player.sendMessage(
                mm.deserialize(
                    "<blue><st>         </st>[ <color:#8987ff>Protected Chest</color> ]<st>         </st>\n" +
                            "<grey>· Owner <color:#858585>≫</color> <color:#8987ff>${chest.owner.playerName()}</color>\n" +
                            "· Protected <color:#858585>≫</color> <color:#8987ff>${chest.protected}</color>\n" +
                            "· Transparent <color:#858585>≫</color> <color:#8987ff>${chest.visual}</color>\n" +
                            "· Trusted <color:#858585>≫</color> <color:#8987ff>${
                                buildString {
                                    chest.trusted.forEach { uuid ->
                                        append(uuid.playerName() + ", ")
                                    }
                                }.removeSuffix(", ")
                            }</color>\n" +
                            "<blue><st>                                           </st>"
                )
            )
            it.isCancelled = true
            return@listen
        }

        // Player is clicking inside the chest
        val uuid = player.uniqueId
        if (!hasAccess(uuid, loc)) {
            broadcast("forbidden access")
            it.isCancelled = true
            noAccess(player, block)
            if (isTransparent(loc)) openChestClone(player, block.state as? Chest)
        } else if (it.action == Action.RIGHT_CLICK_BLOCK) {
            broadcast("open settings")
            SettingsInv(player, getChest(loc) ?: return@listen)
        }
    }

    @Suppress("unused")
    private val onFakeChestClick = listen<InventoryClickEvent> {
        val player = it.whoClicked
        if (player !is Player) return@listen
        val title = it.view.title()
        if (title != fakeChestTitle) return@listen
        it.isCancelled = true
    }

    private fun openChestClone(player: Player, chest: Chest?) {
        chest ?: return
        val inventory = chest.inventory
        val fakeInventory = Bukkit.createInventory(null, inventory.size, fakeChestTitle)
        var counter = 0
        inventory.forEach { item ->
            fakeInventory.setItem(counter, item)
            counter++
        }
        player.openInventory(fakeInventory)
        player.playSound(player, Sound.BLOCK_CHEST_OPEN, 1f, 1f)
    }
}