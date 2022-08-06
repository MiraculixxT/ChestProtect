package de.miraculixx.chestprotect.events

import de.miraculixx.chestprotect.utils.*
import de.miraculixx.chestprotect.utils.data.LiteLocation
import de.miraculixx.chestprotect.utils.gui.GUIBuilder
import de.miraculixx.chestprotect.utils.gui.enums.InvState
import net.axay.kspigot.event.listen
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.name
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

object InventoryClick {
    @Suppress("unused")
    private val onClick = listen<InventoryClickEvent> {
        val player = it.whoClicked as? Player ?: return@listen
        val view = it.view
        if (!mm.stripTags(mm.serialize(view.title())).contains("Chest Protect · (")) return@listen
        it.isCancelled = true

        // Build up connection to source block
        val inventory = view.topInventory
        val indicator = inventory.getItem(0)?.itemMeta?.name ?: return@listen
        val split = mm.stripTags(mm.serialize(indicator)) .split(':')
        val chest = ChestManager.getChest(LiteLocation(split[0].toInt(), split[1].toInt(), split[2].toInt(), split[3])) ?: return@listen

        val item = it.currentItem
        val currentState = if (inventory.size == 9*3) InvState.MENU else InvState.REMOVE

        when (item?.itemMeta?.customModel ?: return@listen) {
            0 -> {
                if (currentState == InvState.MENU) return@listen
                else {
                    GUIBuilder(chest, InvState.MENU).openInventory(player)
                    player.click()
                }
            }

            1 -> {
                chest.protected = !chest.protected
                GUIBuilder(chest, InvState.MENU).openInventory(player)
                player.click()
            }

            2 -> {
                chest.visual = !chest.visual
                GUIBuilder(chest, InvState.MENU).openInventory(player)
                player.click()
            }

            3 -> {
                player.closeInventory()
                player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 99999, 1, false, false, false))
                player.click()
                AwaitChatMessage(chest, player)
            }

            4 -> {
                GUIBuilder(chest, InvState.REMOVE).openInventory(player)
                player.click()
            }

            100 -> {
                player.playSound(player, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1f, 1f)
                val loreText = mm.serialize(item.lore()?.getOrNull(0) ?: return@listen)
                val uuid = UUID.fromString(mm.stripTags(loreText).split(' ').lastOrNull())
                chest.trusted.remove(uuid)
                GUIBuilder(chest, InvState.REMOVE).openInventory(player)
            }
        }
    }
}