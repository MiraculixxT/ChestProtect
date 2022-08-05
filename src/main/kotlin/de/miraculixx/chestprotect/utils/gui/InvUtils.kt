package de.miraculixx.chestprotect.utils.gui

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import de.miraculixx.chestprotect.utils.emptyComponent
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemMeta
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Field
import java.util.*

fun Inventory.fillPlaceholder(): Inventory {
    for (i in 0 until size) {
        setItem(i, InvUtils.primaryPlaceholder)
    }
    if (size != 9) {
        setItem(17, InvUtils.secondaryPlaceholder)
        setItem(size - 18, InvUtils.secondaryPlaceholder)
        repeat(2) { setItem(it, InvUtils.secondaryPlaceholder) }
        repeat(3) { setItem(it + 7, InvUtils.secondaryPlaceholder) }
        repeat(3) { setItem(size - it - 8, InvUtils.secondaryPlaceholder) }
        repeat(2) { setItem(size - it - 1, InvUtils.secondaryPlaceholder) }

    } else {
        setItem(0, InvUtils.secondaryPlaceholder)
        setItem(8, InvUtils.secondaryPlaceholder)
    }
    return this
}

object InvUtils {
    val primaryPlaceholder: ItemStack
    val secondaryPlaceholder: ItemStack

    fun skullTexture(meta: SkullMeta, base64: String): SkullMeta {
        val profile = GameProfile(UUID.randomUUID(), "")
        profile.properties.put("textures", Property("textures", base64))
        val profileField: Field?
        try {
            profileField = meta.javaClass.getDeclaredField("profile")
            profileField.isAccessible = true
            profileField[meta] = profile
        } catch (_: Exception) { }
        return meta
    }


    init {
        val mat1 = Material.GRAY_STAINED_GLASS_PANE
        val mat2 = Material.BLACK_STAINED_GLASS_PANE
        val meta = itemMeta(mat1) {
            name = emptyComponent()
            customModel = 0
        }
        primaryPlaceholder = itemStack(mat1) { itemMeta = meta }
        secondaryPlaceholder = itemStack(mat2) { itemMeta = meta }
    }
}