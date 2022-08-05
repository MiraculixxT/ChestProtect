package de.miraculixx.chestprotect.utils.gui

import de.miraculixx.chestprotect.utils.cHighlight
import de.miraculixx.chestprotect.utils.cmp
import de.miraculixx.chestprotect.utils.data.ChestData
import de.miraculixx.chestprotect.utils.emptyComponent
import de.miraculixx.chestprotect.utils.gui.enums.InvItem
import de.miraculixx.chestprotect.utils.plus
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class ItemProvider(private val chestData: ChestData) {
    fun getItem(item: InvItem): ItemStack {
        return when (item) {
            InvItem.PROTECTED -> itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    name = cmp("Protection", cHighlight, bold = true)
                    customModel = 1
                    lore(
                        listOf(
                            emptyComponent(),
                            cmp("· ") + cmp("Info", cHighlight, underlined = true),
                            cmp("   Verbiete anderen Spielern den Zugriff"),
                            cmp("   auf diese Truhe. Nur noch du und vertraute"),
                            cmp("   Spieler haben dann Zugriff!"),
                            emptyComponent(),
                            cmp("· ") + cmp("Status", cHighlight, underlined = true),
                            cmp("   Protection: ") + cmp("${chestData.protected}", cHighlight),
                            cmp("   Transparent: ") + cmp("${chestData.visual}", cHighlight),
                            emptyComponent(),
                            cmp("Click", cHighlight) + cmp(" ≫ Toggle Active")
                        )
                    )
                }
                itemMeta = InvUtils.skullTexture(
                    itemMeta as SkullMeta,
                    if (chestData.protected) "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkMWFiYTczZjYzOWY0YmM0MmJkNDgxOTZjNzE1MTk3YmUyNzEyYzNiOTYyYzk3ZWJmOWU5ZWQ4ZWZhMDI1In19fQ=="
                    else "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Y0MDk0MmYzNjRmNmNiY2VmZmNmMTE1MTc5NjQxMDI4NmE0OGIxYWViYTc3MjQzZTIxODAyNmMwOWNkMSJ9fX0="
                )
            }

            InvItem.TRANSPARENT -> itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    name = cmp("Transparent", cHighlight, bold = true)
                    customModel = 2
                    lore(
                        listOf(
                            emptyComponent(),
                            cmp("· ") + cmp("Info", cHighlight, underlined = true),
                            cmp("   Wenn ein Spieler keinen Zugriff auf diese"),
                            cmp("   Truhe hat, kann er sie trotzdem öffnen,"),
                            cmp("   jedoch nichts modifizieren!"),
                            emptyComponent(),
                            cmp("· ") + cmp("Status", cHighlight, underlined = true),
                            cmp("   Protection: ") + cmp("${chestData.protected}", cHighlight),
                            cmp("   Transparent: ") + cmp("${chestData.visual}", cHighlight),
                            emptyComponent(),
                            cmp("Click", cHighlight) + cmp(" ≫ Toggle Active")
                        )
                    )
                }
                itemMeta = InvUtils.skullTexture(
                    itemMeta as SkullMeta,
                    if (chestData.visual) "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNiMjI4ZjcwYTM1ZDBhYTMyMzUwNDY3ZDllOGMwOWFhZTlhZTBhZTA4NzVmZGM4YzMxMWE4NzZiZTE5MDcxNyJ9fX0="
                    else "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmJkMmY5MzQ3NmFiNjlmYWY1YTUxOWViNTgzMmRiODQxYzg1MjY2ZTAwMWRlNWIyNmU0MjdmNDFkOThlNWM3ZSJ9fX0="
                )
            }

            InvItem.ADD -> itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    name = cmp("Add Trusted User", cHighlight, bold = true)
                    customModel = 3
                    lore(
                        listOf(
                            emptyComponent(),
                            cmp("· ") + cmp("Info", cHighlight, underlined = true),
                            cmp("   Füge einen Spieler hinzu, welcher auf"),
                            cmp("   diese Truhe Zugriff hat, auch wenn sie"),
                            cmp("   privat ist!"),
                            emptyComponent(),
                            cmp("· ") + cmp("Status", cHighlight, underlined = true),
                            cmp("   Trusted Players: ") + cmp("${chestData.trusted.size}", cHighlight),
                            emptyComponent(),
                            cmp("Click", cHighlight) + cmp(" ≫ Add Player")
                        )
                    )
                }
                itemMeta = InvUtils.skullTexture(
                    itemMeta as SkullMeta,
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkZDIwYmU5MzUyMDk0OWU2Y2U3ODlkYzRmNDNlZmFlYjI4YzcxN2VlNmJmY2JiZTAyNzgwMTQyZjcxNiJ9fX0="
                )
            }

            InvItem.REMOVE -> itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    name = cmp("Remove Trusted User", cHighlight, bold = true)
                    customModel = 4
                    lore(
                        listOf(
                            emptyComponent(),
                            cmp("· ") + cmp("Info", cHighlight, underlined = true),
                            cmp("   Entferne einen Vertrauten Spieler."),
                            cmp("   Dadurch hat dieser keinen Zugriff auf diese"),
                            cmp("   Truhe, wenn sie privat ist!"),
                            emptyComponent(),
                            cmp("· ") + cmp("Status", cHighlight, underlined = true),
                            cmp("   Trusted Players: ") + cmp("${chestData.trusted.size}", cHighlight),
                            emptyComponent(),
                            cmp("Click", cHighlight) + cmp(" ≫ Remove Player")
                        )
                    )
                }
                itemMeta = InvUtils.skullTexture(
                    itemMeta as SkullMeta,
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ4YTk5ZGIyYzM3ZWM3MWQ3MTk5Y2Q1MjYzOTk4MWE3NTEzY2U5Y2NhOTYyNmEzOTM2Zjk2NWIxMzExOTMifX19"
                )
            }
        }
    }
}