package skyshop.categories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import skyfont.skyfont.SkyFont;
import skyshop.pages.Page;
import skyshop.pages.ShopItem;
import skyshop.skyshop.*;
import skyshop.utils.DataPackItems;
import skyshop.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Category {
    public String name;
    public String invName;
    public List<Page> pages;
    public FileConfiguration config;
    SkyShop plugin;

    int previousId;
    int backId;
    int nextId;
    int size;

    public void setup(FileConfiguration _config, String _name, SkyShop _plugin)
    {
        plugin = _plugin;
        config = _config;
        name = _name;

        invName = config.getString(name + ".name");
        previousId = config.getInt(name + ".buttons.previous.slot");
        backId = config.getInt(name + ".buttons.back.slot");
        nextId = config.getInt(name + ".buttons.next.slot");
        size = config.getInt(name + ".size");

        loadPages();
    }

    void loadPages()
    {
        pages = new ArrayList<>();

        //Load each pages in <file>.yml pages section
        Object[] fields = config.getConfigurationSection(name + ".pages").getKeys(false).toArray();
        for (Object key : fields){
            Page newPage = new Page();
            newPage.items = new ArrayList<>();

            newPage.previousSlot = previousId;
            newPage.backSlot = backId;
            newPage.nextSlot = nextId;

            loadAllItems(newPage, key.toString());

            pages.add(newPage);
        }
    }

    void loadAllItems(Page page, String pageID)
    {
        Object[] fields = config.getConfigurationSection(name + ".pages." + pageID + ".items").getKeys(false).toArray();
        for (Object key : fields){
            String base = name + ".pages." + pageID + ".items." + key;

            if (config.getString(base + ".type").equals("item"))
            {
                ShopItem si = new ShopItem();

                si.item = new ItemStack(Material.valueOf(config.getString(base + ".item.material")));
                si.buy = config.getInt(base + ".buy");
                si.sell = config.getInt(base + ".sell");
                si.slot = config.getInt(base + ".slot");

                page.items.add(si);
            }
        }
    }

    int getPreviousPage(int page)
    {
        if (page == 0)
            return pages.size() - 1;

        return page - 1;
    }

    int getNextPage(int page)
    {
        if (page == pages.size() - 1)
            return 0;

        return page + 1;
    }

    void loadItems(Inventory inv, Player player, int page)
    {
        for (ShopItem item : pages.get(page).items)
        {
            ItemStack it = item.item;

            //Store item data as nbt
            Utils.SetItemNBTFloat(it, "buy", item.buy);
            Utils.SetItemNBTFloat(it, "sell", item.sell);
            Utils.SetItemNBTFloat(it, "page", page);

            ItemMeta itm = it.getItemMeta();

            itm.setLore(Arrays.asList(ChatColor.RED.toString() + ChatColor.BOLD + "Acheter" + ChatColor.WHITE + " : " + String.format("%.2f", item.buy) + plugin.skyFont.getCharacter("money"),
                    ChatColor.GREEN.toString() + ChatColor.BOLD + "Vendre" + ChatColor.WHITE + " : " + String.format("%.2f", item.sell) + plugin.skyFont.getCharacter("money")));

            it.setItemMeta(itm);

            inv.setItem(item.slot, it);
        }
    }

    public void open(Player player, int page)
    {
        Inventory inv = Bukkit.createInventory(null, size, ChatColor.WHITE + SkyFont.getPlugin().getCharacter("inventoryBacks") + invName);

        //Some buttons can have specific goal, store it as btn
        inv.setItem(previousId, DataPackItems.getLeftArrow());
        Utils.SetItemNBT(inv.getItem(previousId), "function", "previous");
        Utils.SetItemNBTInt(inv.getItem(previousId), "page", getPreviousPage(page));

        inv.setItem(backId, DataPackItems.getBack());
        Utils.SetItemNBT(inv.getItem(backId), "function", "back");

        inv.setItem(nextId, DataPackItems.getRightArrow());
        Utils.SetItemNBT(inv.getItem(nextId), "function", "next");
        Utils.SetItemNBTInt(inv.getItem(nextId), "page", getNextPage(page));

        //Fill inventory with page's items
        loadItems(inv, player, page);

        player.openInventory(inv);
    }
}
