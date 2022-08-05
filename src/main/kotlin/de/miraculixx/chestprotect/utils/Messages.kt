package de.miraculixx.chestprotect.utils

import org.bukkit.Bukkit

fun consoleMessage(string: String) {
    Bukkit.getConsoleSender().sendMessage(string)
}