package de.miraculixx.chestprotect.utils

import kotlinx.serialization.json.Json
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage

const val prefix = "§8§l[§9ChestProtect§8§l]§7"
val cBase: TextColor = NamedTextColor.GRAY
val cHighlight: TextColor = NamedTextColor.BLUE
val mm = MiniMessage.miniMessage()
val jsonInstance = Json {
    prettyPrint = true
}