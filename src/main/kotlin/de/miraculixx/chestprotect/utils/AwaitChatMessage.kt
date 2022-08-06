package de.miraculixx.chestprotect.utils

import de.miraculixx.chestprotect.utils.data.ChestData
import de.miraculixx.chestprotect.utils.gui.GUIBuilder
import de.miraculixx.chestprotect.utils.gui.enums.InvState
import io.papermc.paper.event.player.AsyncChatEvent
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.bukkit.title
import net.axay.kspigot.runnables.sync
import net.axay.kspigot.runnables.task
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType
import java.time.Duration

class AwaitChatMessage(private val chestData: ChestData, private val player: Player) {
    private val onChat = listen<AsyncChatEvent> {
        if (it.player != player) return@listen
        it.isCancelled = true
        val name = mm.stripTags(mm.serialize(it.message()))
        val offlinePlayer = Bukkit.getOfflinePlayer(name)
        val uuid = offlinePlayer.uniqueId
        if (uuid == chestData.owner || chestData.trusted.contains(uuid)) {
            player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
            player.sendMessage(mm.deserialize("<gray>>><gray> <red>Spieler <color:#e81e25>$name</color> ist bereits Vertraut!</red>"))
        } else if (chestData.trusted.add(uuid)) {
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 1.3f)
            player.sendMessage(mm.deserialize("<gray>>><gray> <green>Spieler <color:#46d146>$name</color> ist nun Vertraut!</green>"))
        } else {
            player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
            player.sendMessage(mm.deserialize("<gray>>><gray> <red>Spieler <color:#e81e25>$name</color> konnte nicht gefunden werden!</red>"))
        }
        sync { stop() }
    }

    private val schedule = task(false, 0, 20, 30, true, {
        sync {
            stop()
        }
    }) {
        player.title(cmp("Enter Player Name", cHighlight), cmp("You have ${it.counterDownToZero}s left", TextColor.color(0x8987ff)), Duration.ZERO)
    }


    private fun stop() {
        onChat.unregister()
        try { schedule?.cancel() } catch (_: IllegalStateException) {}
        player.removePotionEffect(PotionEffectType.BLINDNESS)
        player.title()
        GUIBuilder(chestData, InvState.MENU).openInventory(player)
    }
}