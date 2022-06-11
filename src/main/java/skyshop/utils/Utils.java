package skyshop.utils;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import skychat.skychat.SkyChat;
import skyfont.skyfont.SkyFont;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

    static void setShopItemMeta(Inventory inv, int slotId, String function, int amount)
    {
        ItemStack it = inv.getItem(slotId);

        if (it == null)
            return;

        SetItemNBT(it, "function", function);
        SetItemNBTInt(it, "amount", amount);
    }

    public static Inventory openBuyPanel(Player player, ItemStack _item, int amount, double playerBal)
    {
        Inventory inv = Bukkit.createInventory(null, 9*4, ChatColor.WHITE + SkyFont.getPlugin().getCharacter("inventoryBacks") + SkyFont.getPlugin().getCharacter("buyMenu"));

        ItemStack item = new ItemStack(_item.getType());
        item.setAmount(amount);

        //MINUS
        if (amount > 1)
            inv.setItem(0, DataPackItems.getMinusItem("Mettre à 1", new ArrayList<>(), "#f96767", "#c71111"));
        setShopItemMeta(inv, 0, "set", 1);

        ItemStack minus32 = DataPackItems.getMinusItem("Retirer 32", new ArrayList<>(), "#f96767", "#c71111");
        minus32.setAmount(32);
        if (amount > 32)
            inv.setItem(1, minus32);
        setShopItemMeta(inv, 1, "remove", 32);

        ItemStack minus10 = DataPackItems.getMinusItem("Retirer 10", new ArrayList<>(), "#f96767", "#c71111");
        minus10.setAmount(10);
        if (amount > 10)
            inv.setItem(2, minus10);
        setShopItemMeta(inv, 2, "remove", 10);

        if (amount > 1)
            inv.setItem(3, DataPackItems.getMinusItem("Retirer 1", new ArrayList<>(), "#f96767", "#c71111"));
        setShopItemMeta(inv, 3, "remove", 1);

        //ITEM
        inv.setItem(4, item);
        setShopItemMeta(inv, 4, "main", 0);
        SetItemNBTFloat(inv.getItem(4), "buy", new NBTItem(_item).getFloat("buy"));
        SetItemNBTFloat(inv.getItem(4), "sell", new NBTItem(_item).getFloat("sell"));

        //PLUS
        if (amount < 64)
            inv.setItem(5, DataPackItems.getPlusItem("Ajouter 1", new ArrayList<>(), "#70f967", "#1dc711"));
        setShopItemMeta(inv, 5, "add", 1);

        ItemStack plus10 = DataPackItems.getPlusItem("Ajouter 10", new ArrayList<>(), "#70f967", "#1dc711");
        plus10.setAmount(10);
        if (amount <= 54)
            inv.setItem(6, plus10);
        setShopItemMeta(inv, 6, "add", 10);

        ItemStack plus32 = DataPackItems.getPlusItem("Ajouter 32", new ArrayList<>(), "#70f967", "#1dc711");
        plus32.setAmount(32);
        if (amount <= 32)
            inv.setItem(7, plus32);
        setShopItemMeta(inv, 7, "add", 32);

        if (amount < 64)
            inv.setItem(8, DataPackItems.getPlusItem("Mettre à 64", new ArrayList<>(), "#70f967", "#1dc711"));
        setShopItemMeta(inv, 8, "set", 64);

        float price = new NBTItem(inv.getItem(4)).getFloat("buy") * amount;

        int maxBuyableMoney = (int) (playerBal / new NBTItem(inv.getItem(4)).getFloat("buy"));
        int maxBuyableInv = getMaxItemToFill(player, item);
        int maxBuyable = Math.min(maxBuyableInv, maxBuyableMoney);

        //NAV BUTTONS
        inv.setItem(21, DataPackItems.getYes("Valider", Arrays.asList((price > playerBal ? ChatColor.RED : ChatColor.GREEN) + "Pour " + String.format("%.2f", price) + ChatColor.WHITE + SkyFont.getPlugin().getCharacter("money")), "#e7383c", "#f79617"));
        setShopItemMeta(inv, 21, "confirm", 0);
        if (maxBuyable > 0)
            inv.setItem(22, DataPackItems.getAll("Acheter max", Arrays.asList(ChatColor.WHITE.toString() + maxBuyable +
                    " pour " + String.format("%.2f", new NBTItem(inv.getItem(4)).getFloat("buy") * maxBuyable) +
                    SkyFont.getPlugin().getCharacter("money")), "#e7383c", "#f79617"));
        setShopItemMeta(inv, 22, "all", maxBuyable);
        inv.setItem(23, DataPackItems.getNo("Annuler", new ArrayList<>(), "#e7383c", "#f79617"));
        setShopItemMeta(inv, 23, "back", 0);

        inv.setItem(31, DataPackItems.getPlus("Acheter plus", new ArrayList<>(), "#e7383c", "#f79617"));
        setShopItemMeta(inv, 31, "more", 0);

        inv.setItem(35, DataPackItems.getMoney("Balance :", Arrays.asList(ChatColor.WHITE + String.format("%.2f", playerBal) + SkyFont.getPlugin().getCharacter("money")), "#ffa800", "#fff000"));
        setShopItemMeta(inv, 35, "none", 0);

        player.openInventory(inv);

        return inv;
    }

    public static Inventory openSellPanel(Player player, ItemStack _item, int amount, double playerBal)
    {
        Inventory inv = Bukkit.createInventory(null, 9*4, ChatColor.WHITE + SkyFont.getPlugin().getCharacter("inventoryBacks") + SkyFont.getPlugin().getCharacter("sellMenu"));

        ItemStack item = new ItemStack(_item.getType());
        item.setAmount(amount);

        int inPlayerPossession = getNumberOfItem(player, _item);

        //MINUS
        if (amount > 1)
            inv.setItem(0, DataPackItems.getMinusItem("Mettre à 1", new ArrayList<>(), "#f96767", "#c71111"));
        setShopItemMeta(inv, 0, "set", 1);

        ItemStack minus32 = DataPackItems.getMinusItem("Retirer 32", new ArrayList<>(), "#f96767", "#c71111");
        minus32.setAmount(32);
        if (amount > 32)
            inv.setItem(1, minus32);
        setShopItemMeta(inv, 1, "remove", 32);

        ItemStack minus10 = DataPackItems.getMinusItem("Retirer 10", new ArrayList<>(), "#f96767", "#c71111");
        minus10.setAmount(10);
        if (amount > 10)
            inv.setItem(2, minus10);
        setShopItemMeta(inv, 2, "remove", 10);

        if (amount > 1)
            inv.setItem(3, DataPackItems.getMinusItem("Retirer 1", new ArrayList<>(), "#f96767", "#c71111"));
        setShopItemMeta(inv, 3, "remove", 1);

        //ITEM
        inv.setItem(4, item);
        setShopItemMeta(inv, 4, "main", 0);
        SetItemNBTFloat(inv.getItem(4), "buy", new NBTItem(_item).getFloat("buy"));
        SetItemNBTFloat(inv.getItem(4), "sell", new NBTItem(_item).getFloat("sell"));

        //PLUS
        if (amount < 64)
            inv.setItem(5, DataPackItems.getPlusItem("Ajouter 1", new ArrayList<>(), "#70f967", "#1dc711"));
        setShopItemMeta(inv, 5, "add", 1);

        ItemStack plus10 = DataPackItems.getPlusItem("Ajouter 10", new ArrayList<>(), "#70f967", "#1dc711");
        plus10.setAmount(10);
        if (amount <= 54)
            inv.setItem(6, plus10);
        setShopItemMeta(inv, 6, "add", 10);

        ItemStack plus32 = DataPackItems.getPlusItem("Ajouter 32", new ArrayList<>(), "#70f967", "#1dc711");
        plus32.setAmount(32);
        if (amount <= 32)
            inv.setItem(7, plus32);
        setShopItemMeta(inv, 7, "add", 32);

        if (amount < 64)
            inv.setItem(8, DataPackItems.getPlusItem("Mettre à 64", new ArrayList<>(), "#70f967", "#1dc711"));
        setShopItemMeta(inv, 8, "set", 64);

        float price = new NBTItem(inv.getItem(4)).getFloat("sell") * amount;

        int maxSellable = inPlayerPossession;

        //NAV BUTTONS
        inv.setItem(21, DataPackItems.getYes("Valider", Arrays.asList((amount > inPlayerPossession ? ChatColor.RED : ChatColor.GREEN) + "Pour " + String.format("%.2f", price) + ChatColor.WHITE + SkyFont.getPlugin().getCharacter("money")), "#e7383c", "#f79617"));
        setShopItemMeta(inv, 21, "confirm", 0);
        if (maxSellable > 0)
            inv.setItem(22, DataPackItems.getAll("Vendre tout", Arrays.asList(ChatColor.WHITE.toString() + maxSellable +
                    " pour " + String.format("%.2f", new NBTItem(inv.getItem(4)).getFloat("sell") * maxSellable) +
                    SkyFont.getPlugin().getCharacter("money")), "#e7383c", "#f79617"));
        setShopItemMeta(inv, 22, "all", maxSellable);
        inv.setItem(23, DataPackItems.getNo("Annuler", new ArrayList<>(), "#e7383c", "#f79617"));
        setShopItemMeta(inv, 23, "back", 0);

        inv.setItem(31, DataPackItems.getPlus("Vendre plus", new ArrayList<>(), "#e7383c", "#f79617"));
        setShopItemMeta(inv, 31, "more", 0);

        inv.setItem(35, DataPackItems.getMoney("Balance :", Arrays.asList(ChatColor.WHITE + String.format("%.2f", playerBal) + SkyFont.getPlugin().getCharacter("money")), "#ffa800", "#fff000"));
        setShopItemMeta(inv, 35, "none", 0);

        player.openInventory(inv);

        return inv;
    }

    static void setBuyableStack(Inventory inv, ItemStack item, int slotId, int stackAmount)
    {
        ItemStack newItem = new ItemStack(item);
        newItem.setAmount(stackAmount);

        ItemMeta newItemMeta = newItem.getItemMeta();

        newItemMeta.setDisplayName(SkyChat.getPlugin().getGradient("" + stackAmount + (stackAmount == 1 ? " stack" : " stacks"), "#e7383c", "#f79617"));
        newItemMeta.setLore(Arrays.asList(ChatColor.RED.toString() + ChatColor.BOLD + "Acheter" + ChatColor.WHITE + " : " + String.format("%.2f", new NBTItem(item).getFloat("buy") * stackAmount * 64) + SkyFont.getPlugin().getCharacter("money")));

        newItem.setItemMeta(newItemMeta);

        inv.setItem(slotId, newItem);

        SetItemNBT(inv.getItem(slotId), "function", "buy");
    }

    static void setSellableStack(Inventory inv, ItemStack item, int slotId, int stackAmount)
    {
        ItemStack newItem = new ItemStack(item);
        newItem.setAmount(stackAmount);

        ItemMeta newItemMeta = newItem.getItemMeta();

        newItemMeta.setDisplayName(SkyChat.getPlugin().getGradient("" + stackAmount + (stackAmount == 1 ? " stack" : " stacks"), "#e7383c", "#f79617"));
        newItemMeta.setLore(Arrays.asList(ChatColor.GREEN.toString() + ChatColor.BOLD + "Vendre" + ChatColor.WHITE + " : " + String.format("%.2f", new NBTItem(item).getFloat("sell") * stackAmount * 64) + SkyFont.getPlugin().getCharacter("money")));

        newItem.setItemMeta(newItemMeta);

        inv.setItem(slotId, newItem);

        SetItemNBT(inv.getItem(slotId), "function", "sell");
    }

    public static Inventory openStackBuyPanel(Player player, ItemStack _item, double playerBal)
    {
        Inventory inv = Bukkit.createInventory(null, 9*3, ChatColor.WHITE + SkyFont.getPlugin().getCharacter("inventoryBacks") + SkyFont.getPlugin().getCharacter("achatStack"));

        for (int i = 0; i < 9; i++)
        {
            setBuyableStack(inv, _item, i, i + 1);
        }

        inv.setItem(22, DataPackItems.getBack());
        setShopItemMeta(inv, 22, "back", 0);

        inv.setItem(26, DataPackItems.getMoney("Balance :", Arrays.asList(ChatColor.WHITE + String.format("%.2f", playerBal) + SkyFont.getPlugin().getCharacter("money")), "#ffa800", "#fff000"));
        setShopItemMeta(inv, 26, "none", 0);

        player.openInventory(inv);

        return inv;
    }

    public static Inventory openStackSellPanel(Player player, ItemStack _item, double playerBal)
    {
        Inventory inv = Bukkit.createInventory(null, 9*3, ChatColor.WHITE + SkyFont.getPlugin().getCharacter("inventoryBacks") + SkyFont.getPlugin().getCharacter("venteStack"));

        for (int i = 0; i < 9; i++)
        {
            setSellableStack(inv, _item, i, i + 1);
        }

        inv.setItem(22, DataPackItems.getBack());
        setShopItemMeta(inv, 22, "back", 0);

        inv.setItem(26, DataPackItems.getMoney("Balance :", Arrays.asList(ChatColor.WHITE + String.format("%.2f", playerBal) + SkyFont.getPlugin().getCharacter("money")), "#ffa800", "#fff000"));
        setShopItemMeta(inv, 26, "none", 0);

        player.openInventory(inv);

        return inv;
    }

    public static int getMaxItemToFill(Player player, ItemStack item)
    {
        Inventory inv = player.getInventory();

        int amount = 0;

        for (int i = 0; i < 36; i++) {
            ItemStack slotItem = inv.getItem(i);

            if (slotItem == null || slotItem.getType().isAir())
            {
                amount += 64;

                continue;
            }

            if (slotItem.getType() == item.getType())
            {
                amount += 64 - slotItem.getAmount();
            }
        }

        return amount;
    }

    public static int getItemGiveRest(Player player, ItemStack item, int amount)
    {
        Inventory inv = player.getInventory();

        for (int i = 0; i < 36; i++)
        {
            if (amount <= 0)
                return 0;

            ItemStack slotItem = inv.getItem(i);

            if (slotItem == null || slotItem.getType().isAir())
            {
                amount -= 64;

                continue;
            }

            if (slotItem.getType() == item.getType())
            {
                amount -= 64 - slotItem.getAmount();
            }
        }

        return amount;
    }

    public static boolean hasSameNBT(ItemStack a, ItemStack b, List<String> ignoredKeys)
    {
        NBTItem anbt = new NBTItem(a);
        NBTItem bnbt = new NBTItem(b);

        Set<String> akeylist = anbt.getKeys();
        Set<String> bkeylist = bnbt.getKeys();

        if (akeylist.size() != bkeylist.size())
            return false;

        for (String aStr : akeylist)
        {
            boolean skip = false;

            for (String ignoredKey : ignoredKeys) {
                if (aStr.equalsIgnoreCase(ignoredKey))
                {
                    skip = true;
                    break;
                }
            }

            if (skip)
                continue;

            boolean hasSame = false;

            for (String bStr : bkeylist) {
                if (aStr.equalsIgnoreCase(bStr))
                {
                    hasSame = true;
                    break;
                }
            }

            if (!hasSame)
                return false;
        }

        return true;
    }

    public static int getNumberOfItem(Player player, ItemStack item)
    {
        Inventory inv = player.getInventory();

        int count = 0;

        for (int i = 0; i < 36; i++) {
            ItemStack slotItem = inv.getItem(i);

            if (slotItem == null || slotItem.getType() != item.getType() || hasSameNBT(item, slotItem, Arrays.asList("function", "amount", "buy", "sell")))
            {
                continue;
            }

            count += slotItem.getAmount();
        }

        return count;
    }
}
