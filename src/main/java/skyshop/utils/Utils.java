package skyshop.utils;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import skyfont.skyfont.SkyFont;

import java.util.Arrays;

public class Utils {
    public static void SetItemNBT(ItemStack item, String nbtKey, String nbtValue)
    {
        NBTItem nbti = new NBTItem(item);

        nbti.setString(nbtKey, nbtValue);
        nbti.applyNBT(item);
    }

    public static void SetItemNBTInt(ItemStack item, String nbtKey, int nbtValue)
    {
        NBTItem nbti = new NBTItem(item);

        nbti.setInteger(nbtKey, nbtValue);
        nbti.applyNBT(item);
    }

    public static void SetItemNBTFloat(ItemStack item, String nbtKey, float nbtValue)
    {
        NBTItem nbti = new NBTItem(item);

        nbti.setFloat(nbtKey, nbtValue);
        nbti.applyNBT(item);
    }

    public static void set2x2Button(Inventory inv, int topLeftIndex, ItemStack buttonItem, String nbtKey, String nbtValue)
    {
        inv.setItem(topLeftIndex, buttonItem);
        inv.setItem(topLeftIndex + 1, buttonItem);
        inv.setItem(topLeftIndex + 9, buttonItem);
        inv.setItem(topLeftIndex + 10, buttonItem);

        Utils.SetItemNBT(inv.getItem(topLeftIndex), nbtKey, nbtValue);
        Utils.SetItemNBT(inv.getItem(topLeftIndex + 1), nbtKey, nbtValue);
        Utils.SetItemNBT(inv.getItem(topLeftIndex + 9), nbtKey, nbtValue);
        Utils.SetItemNBT(inv.getItem(topLeftIndex + 10), nbtKey, nbtValue);
    }

    public static void setButton(Inventory inv, int index, ItemStack buttonItem, String nbtKey, String nbtValue)
    {
        inv.setItem(index, buttonItem);

        Utils.SetItemNBT(inv.getItem(index), nbtKey, nbtValue);
    }

    public static InventoryView openMenu(Player player)
    {
        Inventory inv = Bukkit.createInventory(null, 9*4, ChatColor.WHITE + SkyFont.getPlugin().getCharacter("inventoryBacks") + SkyFont.getPlugin().getCharacter("shopMenu"));

        set2x2Button(inv, 0, DataPackItems.getInvisible("Blocs", Arrays.asList("§aAchetez §fet §cvendez §fdivers blocs"), "#7f7f7f", "#404040"), "category", "blocks");
        set2x2Button(inv, 2, DataPackItems.getInvisible("Cultures", Arrays.asList("§aAchetez §fet §cvendez §fvos récoltes"), "#d3d600", "#37d500"), "category", "cultures");
        set2x2Button(inv, 5, DataPackItems.getInvisible("Redstone", Arrays.asList("§aAchetez §fet §cvendez §fde quoi", "§fautomatiser votre île"), "#f00000", "#920000"), "category", "redstone");
        set2x2Button(inv, 7, DataPackItems.getInvisible("Divers", Arrays.asList("§aAchetez §fet §cvendez §fdivers items"), "#2a2ef2", "#e86313"), "category", "misc");
        set2x2Button(inv, 18, DataPackItems.getInvisible("Nourriture", Arrays.asList("§aAchetez §fet §cvendez", "§fde la nourriture"), "#792d13", "#3d1609"), "category", "food");
        set2x2Button(inv, 20, DataPackItems.getInvisible("Loots", Arrays.asList("§aAchetez §fet §cvendez", "§fle fruit de vos exploits"), "#ffffff", "#ffffff"), "category", "loots");
        set2x2Button(inv, 23, DataPackItems.getInvisible("Mineraix", Arrays.asList("§aAchetez §fet §cvendez §fdes", "§fmineraix rares"), "#1ed6ec", "#ece51e"), "category", "ores");
        set2x2Button(inv, 25, DataPackItems.getInvisible("Spawners", Arrays.asList("§aAchetez §fet §cvendez §fdes spawners"), "#393939", "#393939"), "category", "spawners");

        return player.openInventory(inv);
    }
}
