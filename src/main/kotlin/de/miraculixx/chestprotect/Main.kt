package de.miraculixx.chestprotect

import de.miraculixx.chestprotect.events.ChestInteract
import de.miraculixx.chestprotect.events.ChestPlace
import de.miraculixx.chestprotect.events.ChestProtection
import de.miraculixx.chestprotect.utils.ChestManager
import net.axay.kspigot.main.KSpigot

lateinit var INSTANCE: KSpigot

class Main: KSpigot() {
    override fun startup() {
        INSTANCE = this

        // Preload Data
        ChestManager

        // Register Listener
        ChestInteract
        ChestPlace
        ChestProtection
    }

    override fun shutdown() {
        ChestManager.save()
    }
}