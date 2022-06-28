package skyshop.pages;

import org.bukkit.inventory.ItemStack;
import skyshop.categories.Category;
import skyshop.skyshop.SkyShop;

public class ShopItem {
    public ItemStack item;
    public float buy;
    public float sell;
    public int slot;

    //ores,5,COAL,b:100,s:10

    public String ToString(String category, int page, int slot)
    {
        return category + "," + page + "," + slot + "," + item.getType().name() + "," + "b:" + buy + "," + "s:" + sell;
    }

    public static Category getCategory(String nbt)
    {
        return SkyShop.skyShop.getCategory(nbt.split(",")[0]);
    }

    public static int getPage(String nbt)
    {
        return Integer.parseInt(nbt.split(",")[1]);
    }

    public static int getSlot(String nbt)
    {
        return Integer.parseInt(nbt.split(",")[2]);
    }
}
