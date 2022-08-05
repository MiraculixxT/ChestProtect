package de.miraculixx.chestprotect.utils.gui

import de.miraculixx.chestprotect.utils.*
import de.miraculixx.chestprotect.utils.data.ChestData
import de.miraculixx.chestprotect.utils.gui.enums.InvItem
import de.miraculixx.chestprotect.utils.gui.enums.InvState
import io.papermc.paper.event.player.AsyncChatEvent
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.bukkit.title
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.runnables.sync
import net.axay.kspigot.runnables.task
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.sql.Timestamp
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.util.*

class SettingsInv(private val owner: Player, private val chest: ChestData) {
    private var currentState = InvState.MENU
    private var awaitingInput = false

    // Privat Listener
    private val onClick = listen<InventoryClickEvent> {
        val player = it.whoClicked as? Player ?: return@listen
        if (player != owner) return@listen
        val item = it.currentItem

        when (item?.itemMeta?.customModel ?: return@listen) {
            0 -> {
                if (currentState == InvState.MENU) return@listen
                else player.openInventory(craftInventory(InvState.MENU))
            }

            1 -> {
                chest.protected = !chest.protected
                player.openInventory(craftInventory(InvState.MENU))
                player.click()
            }

            2 -> {
                chest.visual = !chest.visual
                player.openInventory(craftInventory(InvState.MENU))
                player.click()
            }

            3 -> {
                player.closeInventory()
                player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 99999, 1, false, false, false))
                player.click()
                awaitingInput = true
                schedule()
            }

            4 -> {
                player.openInventory(craftInventory(InvState.REMOVE))
                player.click()
            }

            100 -> {
                player.playSound(player, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1f, 1f)
                val loreText = mm.serialize(item.lore()?.getOrNull(0) ?: return@listen)
                val uuid = UUID.fromString(mm.stripTags(loreText).split(' ').lastOrNull())
                chest.trusted.remove(uuid)
                player.openInventory(craftInventory(InvState.REMOVE))
            }
        }
    }

    private val onClose = listen<InventoryCloseEvent> {
        if (it.player != owner)
            stop()
    }

    private val onChat = listen<AsyncChatEvent> {
        if (it.player != owner || !awaitingInput) return@listen
        awaitingInput = false
        val name = mm.serialize(it.originalMessage())
        val offlinePlayer = Bukkit.getOfflinePlayer(name)
        if (chest.trusted.remove(offlinePlayer.uniqueId)) {
            owner.playSound(owner, Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 1.3f)
            owner.sendMessage(mm.deserialize("<gray>>><gray> <green>Spieler <color:#46d146>$name</color> ist nun Vertraut!</green>"))
        } else {
            owner.playSound(owner, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
            owner.sendMessage(mm.deserialize("<gray>>><gray> <red>Spieler <color:#e81e25>$name</color> konnte nicht gefunden werden!</red>"))
        }
    }

    private fun schedule() {
        task(false, 0, 20, 30, true, {
            sync {
                owner.removePotionEffect(PotionEffectType.BLINDNESS)
                owner.title(cmp("Timed Out", NamedTextColor.RED, bold = true))
            }
        }) {
            if (!awaitingInput) {
                it.cancel()
                return@task
            }
            owner.title(cmp("Enter Player Name", cHighlight), cmp("You have ${it.counterDownToZero}s left", TextColor.color(0x8987ff)), Duration.ZERO)
        }
    }

    // Returns the current GUI state
    private fun craftInventory(state: InvState): Inventory {
        val items = ItemProvider(chest)
        val title = cmp("Chest Protect - ${chest.location}")
        currentState = state
        return when (state) {
            InvState.MENU -> {
                val inv = Bukkit.createInventory(null, 3*9, title)
                inv.fillPlaceholder()
                inv.setItem(11, items.getItem(InvItem.PROTECTED))
                inv.setItem(12, items.getItem(InvItem.TRANSPARENT))
                inv.setItem(14, items.getItem(InvItem.ADD))
                inv.setItem(15, items.getItem(InvItem.REMOVE))
                inv
            }

            InvState.REMOVE -> {
                val inv = Bukkit.createInventory(null, 6*9, title)
                inv.fillPlaceholder()
                val ph = itemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE) {
                    meta {
                        customModel = 0
                        name = emptyComponent()
                    }
                }
                repeat(9 * 4) { i ->
                    inv.setItem(i + 9, ph)
                }
                inv.setItem(4, items.getItem(InvItem.REMOVE))
                for ((i, uuid) in chest.trusted.withIndex()) {
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
                                    mm.deserialize("<grey>∙ <blue><u>Info"),
                                    cmp("   Online: ") + cmp("${offlinePlayer.isOnline}", cHighlight),
                                    cmp("   Last Seen: ") + cmp(
                                        if (lastSeen == 0L) "never"
                                        else Timestamp(lastSeen).toLocalDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
                                    ),
                                    emptyComponent(),
                                    cmp("Click", cHighlight) + cmp(" ≫ Remove Player")
                                )
                            )
                        }
                    })
                }
                inv
            }
        }
    }

    // Stop every action for this object
    private fun stop() {
        onClick.unregister()
        onClose.unregister()
        onChat.unregister()
        owner.closeInventory()
    }

    init {
        owner.openInventory(craftInventory(InvState.MENU))
    }
}