package skyshop.events;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import skyfont.skyfont.SkyFont;
import skyshop.categories.Category;
import skyshop.skyshop.SkyShop;
import skyshop.utils.Utils;

public class ShopEvent implements @NotNull Listener {
    SkyShop plugin;

    public ShopEvent(SkyShop skyShop) {
        plugin = skyShop;
    }

    //Check if player clicked the shop main page
    boolean checkForMenu(InventoryClickEvent e)
    {
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR)
            return false;

        if (!e.getView().getTitle().contains(SkyFont.getPlugin().getCharacter("shopMenu")))
            return false;

        e.setCancelled(true);

        //Get item category nbt (should be the same name than a loaded category)
        NBTItem nbti = new NBTItem(e.getCurrentItem());
        String nbt = nbti.getString("category");

        if (nbt == null || nbt.isEmpty())
            return false;

        plugin.getCategory(nbt).open((Player) e.getView().getPlayer(), 0);

        return true;
    }

    //Check if player clicked in a category view
    boolean checkForCategory(InventoryClickEvent e)
    {
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR)
            return false;

        Category category = plugin.getCategoryByContainingInventoryName(e.getView().getTitle());

        if (category == null)
            return false;

        e.setCancelled(true);

        NBTItem nbti = new NBTItem(e.getCurrentItem());

        //Check if clicked item has a specific thing to do (If true, it's not a shop item)
        if (nbti.hasKey("function"))
        {
            String function = nbti.getString("function");

            //If item should close current window, go back to menu
            if (function == "back")
            {
                e.getView().close();

                Utils.openMenu((Player) e.getView().getPlayer());

                return true;
            }

            //If the others cases are false, this item is prev/next button, so load its page
            category.open((Player) e.getView().getPlayer(), nbti.getInteger("page"));

            return true;
        }

        Utils.openBuyPanel((Player) e.getView().getPlayer(), clickedItem, 1);

        return true;
    }

    //Check if player clicked in abuy menu
    boolean checkForBuy(InventoryClickEvent e)
    {
        ItemStack clickedItem = e.getCurrentItem();
        Inventory inv = e.getClickedInventory();

        if (clickedItem == null || clickedItem.getType() == Material.AIR)
            return false;

        if (!e.getView().getTitle().contains(SkyFont.getPlugin().getCharacter("buyMenu")))
            return false;

        e.setCancelled(true);

        ItemStack buyItem = inv.getItem(4);

        NBTItem itemNBT = new NBTItem(e.getCurrentItem());
        String function = itemNBT.getString("function");
        int amount = itemNBT.getInteger("amount");

        switch(function)
        {
            case "remove":
                Utils.openBuyPanel((Player) e.getView().getPlayer(), buyItem, buyItem.getAmount() - amount);
                break;
            case "add":
                Utils.openBuyPanel((Player) e.getView().getPlayer(), buyItem, buyItem.getAmount() + amount);
                break;
            case "set":
                Utils.openBuyPanel((Player) e.getView().getPlayer(), buyItem, amount);
                break;
            case "back":
                Utils.openMenu((Player) e.getView().getPlayer());
                break;
            case "confirm":
                Utils.openMenu((Player) e.getView().getPlayer());
                break;
            case "all":
                Utils.openMenu((Player) e.getView().getPlayer());
                break;
            case "more":
                Utils.openMenu((Player) e.getView().getPlayer());
                break;
        }

        return true;
    }

    @EventHandler
    void OnPlayerInteractShop(InventoryClickEvent e) {
        if (checkForMenu(e))
            return;
        if (checkForCategory(e))
            return;
        if (checkForBuy(e))
            return;
    }
}
