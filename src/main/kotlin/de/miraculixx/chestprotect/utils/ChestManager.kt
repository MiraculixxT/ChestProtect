package de.miraculixx.chestprotect.utils

import de.miraculixx.chestprotect.utils.data.ChestData
import de.miraculixx.chestprotect.utils.data.LiteLocation
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.axay.kspigot.languageextensions.kotlinextensions.createIfNotExists
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.entity.Player
import org.bukkit.inventory.DoubleChestInventory
import java.io.File
import java.util.*
import kotlin.io.path.Path

object ChestManager {
    private val chests: MutableMap<LiteLocation, ChestData>

    /**
     * Add a chest to the data list
     * @param location Location of the chest
     * @param owner Owner of the chest
     */
    fun addChest(location: LiteLocation, owner: UUID) {
        chests[location] = ChestData(location, protected = false, visual = true, owner, mutableListOf())
    }

    /**
     * Remove a chest from the data list
     * @param location Location of the chest
     */
    fun removeChest(location: LiteLocation) {
        chests.remove(location)
    }

    /**
     * Receive the chest object at the given location. Could be null if no saved chest is at this location
     * @param location Location of the block
     * @return Chest Object or null
     */
    fun getChest(location: LiteLocation): ChestData? {
        return chests[location]
    }

    /**
     * Check if the player has access to the block at the given location
     * @param player Player to check permissions for
     * @param location Location of the block
     * @return Permission state
     */
    fun hasAccess(player: UUID, location: LiteLocation): Boolean {
        val obj = chests[location] ?: return true
        return !obj.protected || obj.owner == player || obj.trusted.contains(player)
    }

    /**
     * Check if the block at the given location is a protected chest
     * @param location Location of the block
     * @return Protection state
     */
    fun isProtected(location: LiteLocation): Boolean {
        val obj = chests[location] ?: return false
        return obj.protected
    }

    /**
     * Sends an Alert to the Player
     * @param player Alerted player
     * @param block Alerted block
     */
    fun noAccess(player: Player, block: Block) {
        player.playSound(player, Sound.BLOCK_STONE_BREAK, 0.8f, 0.7f)
        val inventory = (block.state as Chest).inventory
        if (inventory is DoubleChestInventory) {
            inventory.rightSide.location?.block?.hollow(player)
            inventory.leftSide.location?.block?.hollow(player)
        } else block.hollow(player)
    }

    /**
     * Check if players with no permission to access the block at the given location can see the content
     * @param location Location of the block
     * @return Transparent State
     */
    fun isTransparent(location: LiteLocation): Boolean {
        val obj = chests[location] ?: return false
        return obj.visual
    }

    fun save() {
        val jsonString = jsonInstance.encodeToString(chests.values)
        consoleMessage("$prefix Saving §9${chests.size}§7 Chests (${jsonString.length / 1000.0} kb)")
        val file = File(Path("plugins/MUtils/chests.json").toAbsolutePath().toUri())
        file.createIfNotExists()
        file.writeText(jsonString)
        consoleMessage("$prefix Saved at §9${file.path}")
    }

    init {
        val file = File(Path("plugins/MUtils/chests.json").toAbsolutePath().toUri())
        if (file.exists()) {
            consoleMessage("$prefix Saved Chests found! Loading §9${file.length() / 1000.0}§7 kb...")
            val jsonString = file.readText()
            chests = buildMap {
                jsonInstance.decodeFromString<List<ChestData>>(jsonString).forEach { data ->
                    put(data.location, data)
                }
            }.toMutableMap()
            consoleMessage("$prefix Loaded ${chests.size} Chests!")
        } else {
            chests = mutableMapOf()
            consoleMessage("$prefix No saved Chests found! Booting with not data")
        }
    }
}