package de.miraculixx.chestprotect.utils

import de.miraculixx.chestprotect.utils.data.LiteLocation
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player
import java.util.UUID


fun Location.toLiteLocation(): LiteLocation {
    return LiteLocation(blockX, blockY, blockZ, world.name)
}

fun Player.errorParticle(location: Location) {
    spawnParticle(Particle.WAX_ON, location, 1, .0, .0, .0, 0.1)
}

fun Player.click() {
    playSound(location, Sound.UI_BUTTON_CLICK, 0.8f, 1f)
}

fun Block.hollow(player: Player) {
    val list = ArrayList<Location>()
    /*
              a2 -- b2      x (side)
    a1 -- b1  |  •  |       y (up/down)
    |  •  |  c2 -- d2       z (depth)
    c1 -- d1    back
     front
     */
    val loc = location.clone().add(.5, .5, .5) //•
    val p1 = loc.clone().add(-.5, .5, .5) //a1
    val p2 = loc.clone().add(-.5, .5, -.5) //a2
    val p3 = loc.clone().add(-.5, -.5, .5) //c1
    val p4 = loc.clone().add(-.5, -.5, -.5) //c2

    // Creating -- bridges
    repeat(4) {
        list.add(p1.clone().add(.25 * it, .0, .0)) //a1 to b1
        list.add(p2.clone().add(.25 * it, .0, .0)) //a2 to b2
        list.add(p3.clone().add(.25 * it, .0, .0)) //c1 to d1
        list.add(p4.clone().add(.25 * it, .0, .0)) //c2 to d2
    }
    // Creating | bridges
    p3.add(1.0, 1.0, .0) //to a1
    p4.add(1.0, 1.0, .0) //to a2
    repeat(4) {
        list.add(p1.clone().subtract(.0, .25 * it, .0)) //b1 to d1
        list.add(p2.clone().subtract(.0, .25 * it, .0)) //b2 to d2
        list.add(p3.clone().subtract(.0, .25 * it, .0)) //a1 to c1
        list.add(p4.clone().subtract(.0, .25 * it, .0)) //a2 to c2
    }
    // Creating depth bridges
    p2.add(.0, -1.0, 1.0) //d2 to a1
    p4.add(.0, -1.0, 1.0) //c2 to b1
    repeat(4) {
        list.add(p1.clone().subtract(.0, .0, .25 * it)) //d1 to d2
        list.add(p2.clone().subtract(.0, .0, .25 * it)) //a1 to a2
        list.add(p3.clone().subtract(.0, .0, .25 * it)) //c1 to c2
        list.add(p4.clone().subtract(.0, .0, .25 * it)) //b1 to b2
    }

    list.forEach { player.errorParticle(it) }
}

fun UUID.playerName(): String {
    return Bukkit.getOfflinePlayer(this).name ?: this.toString()
}

fun cmp(text: String, color: TextColor = cBase, bold: Boolean = false, italic: Boolean = false, strikethrough: Boolean = false, underlined: Boolean = false): Component {
    return Component.text(text).color(color)
        .decorations(
            mapOf(
                TextDecoration.BOLD to TextDecoration.State.byBoolean(bold),
                TextDecoration.ITALIC to TextDecoration.State.byBoolean(italic),
                TextDecoration.STRIKETHROUGH to TextDecoration.State.byBoolean(strikethrough),
                TextDecoration.UNDERLINED to TextDecoration.State.byBoolean(underlined)
            )
        )
}

operator fun Component.plus(other: Component): Component {
    return append(other)
}

fun emptyComponent(): Component {
    return Component.text(" ")
}
