package de.miraculixx.chestprotect.utils.gui

import de.miraculixx.chestprotect.utils.*
import de.miraculixx.chestprotect.utils.data.ChestData
import de.miraculixx.chestprotect.utils.gui.enums.InvItem
import de.miraculixx.chestprotect.utils.gui.enums.InvState
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.meta.SkullMeta
import java.sql.Timestamp
import java.time.format.DateTimeFormatter

class GUIBuilder(private val chestData: ChestData, private val state: InvState) {

    private fun menuInventory(itemProvider: ItemProvider): Inventory {
        val inv = Bukkit.createInventory(null, 3 * 9, cmp("Chest Protect · (${chestData.location})", cHighlight))
        inv.fillPlaceholder()
        inv.setItem(0, itemProvider.getItem(InvItem.INDICATOR))
        inv.setItem(11, itemProvider.getItem(InvItem.PROTECTED))
        inv.setItem(12, itemProvider.getItem(InvItem.TRANSPARENT))
        inv.setItem(14, itemProvider.getItem(InvItem.ADD))
        inv.setItem(15, itemProvider.getItem(InvItem.REMOVE))
        return inv
    }

    private fun removerInventory(itemProvider: ItemProvider): Inventory {
        val inv = Bukkit.createInventory(null, 6*9, cmp("Chest Protect · (${chestData.location})", cHighlight))
        inv.fillPlaceholder()
        val ph = itemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE) {
            meta {
                customModel = -1
                name = emptyComponent()
            }
        }
        repeat(9 * 4) { i ->
            inv.setItem(i + 9, ph)
        }
        inv.setItem(4, itemProvider.getItem(InvItem.REMOVE))
        for ((i, uuid) in chestData.trusted.withIndex()) {
            val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
            val lastSeen = offlinePlayer.lastSeen
            if (i > 9 * 4) break
            inv.setItem(9 + i, itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    owningPlayer = offlinePlayer
                    name = cmp(offlinePlayer.name ?: uuid.toString(), cHighlight, bold = true)
                    customModel = 100
                    lore(
                        listOf(
                            cmp("UUID: $uuid", TextColor.color(0x3b3b3b)),
                            emptyComponent(),
                            mm.deserialize("<!italic><grey>∙ <blue><u>Info"),
                            cmp("   Online: ") + cmp("${offlinePlayer.isOnline}", cHighlight),
                            cmp("   Last Seen: ") + cmp(
                                if (lastSeen == 0L) "never"
                                else Timestamp(lastSeen).toLocalDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")), cHighlight
                            ),
                            emptyComponent(),
                            cmp("Click", cHighlight) + cmp(" ≫ Remove Player")
                        )
                    )
                }
            })
        }
        inv.setItem(0, itemProvider.getItem(InvItem.INDICATOR))
        return inv
    }

    fun openInventory(player: Player) {
        val items = ItemProvider(chestData)
        when (state) {
            InvState.MENU -> {
                player.openInventory(menuInventory(items))
            }

            InvState.REMOVE -> {
                player.openInventory(removerInventory(items))
            }
        }
    }
}