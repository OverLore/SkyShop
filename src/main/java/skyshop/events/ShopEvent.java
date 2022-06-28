package skyshop.events;

import com.google.common.math.DoubleMath;
import de.tr7zw.nbtapi.NBTItem;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
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
import skyshop.pages.ShopItem;
import skyshop.skyshop.SkyShop;
import skyshop.utils.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShopEvent implements @NotNull Listener {
    SkyShop plugin;

    String logStr = "";
    String defaultPath;

    public ShopEvent(SkyShop skyShop) {

        plugin = skyShop;
        defaultPath = plugin.getDataFolder() + "\\Logs\\";
    }

    enum ShopAction
    {
        Buy,
        Sell
    };

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

        if (e.isLeftClick()) {
            Utils.openBuyPanel((Player) e.getView().getPlayer(), clickedItem, 1, plugin.economy.getBalance(e.getView().getPlayer().getName()));

            return true;
        }
        else if (e.isRightClick()) {
            Utils.openSellPanel((Player) e.getView().getPlayer(), clickedItem, 1, plugin.economy.getBalance(e.getView().getPlayer().getName()));

            return true;
        }

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

        Player player = (Player) e.getView().getPlayer();

        ItemStack buyItem = inv.getItem(4);
        NBTItem buyItemNBT = new NBTItem(buyItem);

        NBTItem itemNBT = new NBTItem(e.getCurrentItem());

        if (itemNBT == null)
            return false;

        String function = itemNBT.getString("function");
        int amount = itemNBT.getInteger("amount");

        double playerBal = plugin.economy.getBalance(player.getName());

        switch(function)
        {
            case "remove":
                Utils.openBuyPanel((Player) player, buyItem, buyItem.getAmount() - amount, playerBal);
                break;
            case "add":
                Utils.openBuyPanel((Player) player, buyItem, buyItem.getAmount() + amount, playerBal);
                break;
            case "set":
                Utils.openBuyPanel((Player) player, buyItem, amount, playerBal);
                break;
            case "back":
                Utils.openMenu((Player) player);
                break;
            case "confirm":
                float price = buyItem.getAmount() * buyItemNBT.getFloat("buy");

                if (price > playerBal)
                {
                    player.sendMessage(ChatColor.RED + "[Erreur] Vous n'avez pas assez d'argent pour acheter " +
                            buyItem.getAmount() + "x" + buyItem.getI18NDisplayName() + " pour " + String.format("%.2f", price) +
                            ChatColor.WHITE + SkyFont.getPlugin().getCharacter("money"));
                    player.sendMessage(ChatColor.RED + "Il vous manque " + String.format("%.2f", price - playerBal) +
                            ChatColor.WHITE + SkyFont.getPlugin().getCharacter("money"));
                    return true;
                }

                plugin.economy.withdrawPlayer(player.getName(), price);
                player.sendMessage(ChatColor.GREEN + "[SkyShop] Vous avez acheté " + buyItem.getAmount() +
                        "x" + buyItem.getI18NDisplayName() + " pour " + String.format("%.2f", price) + ChatColor.WHITE +
                        SkyFont.getPlugin().getCharacter("money"));
                player.sendMessage(ChatColor.GREEN + "Il vous reste " + String.format("%.2f", playerBal - price) +
                        ChatColor.WHITE + SkyFont.getPlugin().getCharacter("money"));

                ItemStack rawItem = new ItemStack(buyItem.getType());
                rawItem.setAmount(buyItem.getAmount());

                int rest = Utils.getItemGiveRest((Player) player, rawItem, buyItem.getAmount());

                player.getInventory().addItem(rawItem);

                if (rest > 0)
                {
                    player.sendMessage(ChatColor.RED + "Votre inventaire est plein.");

                    if (rest == 1)
                        player.sendMessage(ChatColor.RED.toString() + rest + "x " + buyItem.getI18NDisplayName() + " a été drop a vos pieds.");
                    else
                        player.sendMessage(ChatColor.RED.toString() + rest + "x " + buyItem.getI18NDisplayName() + " ont été drop a vos pieds.");

                    rawItem.setAmount(rest);
                    player.getWorld().dropItem(player.getLocation(), rawItem);
                }

                Utils.openMenu((Player) player);

                LogAction(player, ShopAction.Buy, buyItem, buyItem.getAmount(), price);

                break;
            case "all":
                float allPrice = amount * buyItemNBT.getFloat("buy");

                plugin.economy.withdrawPlayer(player.getName(), allPrice);
                player.sendMessage(ChatColor.GREEN + "[SkyShop] Vous avez acheté " + amount +
                        "x" + buyItem.getI18NDisplayName() + " pour " + String.format("%.2f", allPrice) + ChatColor.WHITE +
                        SkyFont.getPlugin().getCharacter("money"));
                player.sendMessage(ChatColor.GREEN + "Il vous reste " + String.format("%.2f", playerBal - allPrice) +
                        ChatColor.WHITE + SkyFont.getPlugin().getCharacter("money"));

                ItemStack rawIAlltem = new ItemStack(buyItem.getType());
                rawIAlltem.setAmount(amount);

                player.getInventory().addItem(rawIAlltem);

                Utils.openMenu((Player) player);

                LogAction(player, ShopAction.Buy, buyItem, buyItem.getAmount(), allPrice);

                break;
            case "more":
                Utils.openStackBuyPanel(player, buyItem, playerBal);
                break;
        }

        return true;
    }

    boolean checkForBuyStack(InventoryClickEvent e)
    {
        ItemStack clickedItem = e.getCurrentItem();
        Inventory inv = e.getClickedInventory();

        if (clickedItem == null || clickedItem.getType() == Material.AIR)
            return false;

        if (!e.getView().getTitle().contains(SkyFont.getPlugin().getCharacter("achatStack")))
            return false;

        e.setCancelled(true);

        Player player = (Player) e.getView().getPlayer();

        NBTItem itemNBT = new NBTItem(e.getCurrentItem());

        if (itemNBT == null)
            return false;

        String function = itemNBT.getString("function");

        if (function == "back")
        {
            Utils.openMenu(player);

            return true;
        }
        if (function == "buy")
        {
            float price = clickedItem.getAmount() * new NBTItem(clickedItem).getFloat("buy") * 64;

            double playerBal = plugin.economy.getBalance(player.getName());

            if (price > playerBal)
            {
                player.sendMessage(ChatColor.RED + "[Erreur] Vous n'avez pas assez d'argent pour acheter " +
                        clickedItem.getAmount() + (clickedItem.getAmount() == 1 ? "x stack de " : "x stacks de ") +
                        clickedItem.getI18NDisplayName() + " pour " + String.format("%.2f", price) +
                        ChatColor.WHITE + SkyFont.getPlugin().getCharacter("money"));
                player.sendMessage(ChatColor.RED + "Il vous manque " + String.format("%.2f", price - playerBal) +
                        ChatColor.WHITE + SkyFont.getPlugin().getCharacter("money"));
                return true;
            }

            plugin.economy.withdrawPlayer(player.getName(), price);
            player.sendMessage(ChatColor.GREEN + "[SkyShop] Vous avez acheté " + clickedItem.getAmount() +
                    (clickedItem.getAmount() == 1 ? "x stack de " : "x stacks de ") +
                    clickedItem.getI18NDisplayName() + " pour " + String.format("%.2f", price) + ChatColor.WHITE +
                    SkyFont.getPlugin().getCharacter("money"));
            player.sendMessage(ChatColor.GREEN + "Il vous reste " + String.format("%.2f", playerBal - price) +
                    ChatColor.WHITE + SkyFont.getPlugin().getCharacter("money"));

            ItemStack rawItem = new ItemStack(clickedItem.getType());
            rawItem.setAmount(clickedItem.getAmount() * 64);

            int rest = Utils.getItemGiveRest((Player) player, rawItem, clickedItem.getAmount() * 64);

            player.getInventory().addItem(rawItem);

            if (rest > 0)
            {
                player.sendMessage(ChatColor.RED + "Votre inventaire est plein.");

                if (rest == 1)
                    player.sendMessage(ChatColor.RED.toString() + rest + "x " + clickedItem.getI18NDisplayName() + " a été drop a vos pieds.");
                else
                    player.sendMessage(ChatColor.RED.toString() + rest + "x " + clickedItem.getI18NDisplayName() + " ont été drop a vos pieds.");

                rawItem.setAmount(rest);
                player.getWorld().dropItem(player.getLocation(), rawItem);
            }

            Utils.openMenu((Player) player);

            LogAction(player, ShopAction.Buy, clickedItem, clickedItem.getAmount() * 64, price);
        }

        return true;
    }

    boolean checkForSellStack(InventoryClickEvent e)
    {
        ItemStack clickedItem = e.getCurrentItem();
        Inventory inv = e.getClickedInventory();

        if (clickedItem == null || clickedItem.getType() == Material.AIR)
            return false;

        if (!e.getView().getTitle().contains(SkyFont.getPlugin().getCharacter("venteStack")))
            return false;

        e.setCancelled(true);

        Player player = (Player) e.getView().getPlayer();

        NBTItem itemNBT = new NBTItem(clickedItem);

        if (itemNBT == null)
            return false;

        int inPlayerPossession = Utils.getNumberOfItem(player, clickedItem);

        String function = itemNBT.getString("function");

        if (function.equals("back"))
        {
            Utils.openMenu(player);

            return true;
        }
        if (function.equals("sell"))
        {
            float price = clickedItem.getAmount() * new NBTItem(clickedItem).getFloat("sell") * 64;

            double playerBal = plugin.economy.getBalance(player.getName());

            if (clickedItem.getAmount() * 64 > inPlayerPossession)
            {
                player.sendMessage(ChatColor.RED + "[Erreur] Vous n'avez pas " +
                        clickedItem.getAmount() + (clickedItem.getAmount() == 1 ? "x stack de " : "x stacks de ") +
                        clickedItem.getI18NDisplayName() + " a vendre");
                player.sendMessage(ChatColor.RED + "Il vous manque " + (clickedItem.getAmount() * 64 - inPlayerPossession) +
                        "x" + clickedItem.getI18NDisplayName());
                return true;
            }

            plugin.economy.depositPlayer(player.getName(), price);
            player.sendMessage(ChatColor.GREEN + "[SkyShop] Vous avez vendu " + clickedItem.getAmount() +
                    (clickedItem.getAmount() == 1 ? "x stack de " : "x stacks de ") +
                    clickedItem.getI18NDisplayName() + " pour " + String.format("%.2f", price) + ChatColor.WHITE +
                    SkyFont.getPlugin().getCharacter("money"));
            player.sendMessage(ChatColor.GREEN + "Vous avez à présent " + String.format("%.2f", playerBal + price) +
                    ChatColor.WHITE + SkyFont.getPlugin().getCharacter("money"));

            ItemStack rawItem = new ItemStack(clickedItem.getType());
            rawItem.setAmount(clickedItem.getAmount() * 64);

            player.getInventory().removeItemAnySlot(rawItem);

            Utils.openMenu((Player) player);

            LogAction(player, ShopAction.Sell, clickedItem, clickedItem.getAmount() * 64, price);
        }

        return true;
    }

    //Check if player clicked in a sell menu
    boolean checkForSell(InventoryClickEvent e)
    {
        ItemStack clickedItem = e.getCurrentItem();
        Inventory inv = e.getClickedInventory();

        if (clickedItem == null || clickedItem.getType() == Material.AIR)
            return false;

        if (!e.getView().getTitle().contains(SkyFont.getPlugin().getCharacter("sellMenu")))
            return false;

        e.setCancelled(true);

        Player player = (Player) e.getView().getPlayer();

        ItemStack sellItem = inv.getItem(4);
        NBTItem sellItemNBT = new NBTItem(sellItem);

        NBTItem itemNBT = new NBTItem(e.getCurrentItem());

        if (itemNBT == null)
            return false;

        String function = itemNBT.getString("function");
        int amount = itemNBT.getInteger("amount");

        double playerBal = plugin.economy.getBalance(player.getName());

        switch(function)
        {
            case "remove":
                Utils.openSellPanel((Player) player, sellItem, sellItem.getAmount() - amount, playerBal);
                break;
            case "add":
                Utils.openSellPanel((Player) player, sellItem, sellItem.getAmount() + amount, playerBal);
                break;
            case "set":
                Utils.openSellPanel((Player) player, sellItem, amount, playerBal);
                break;
            case "back":
                Utils.openMenu((Player) player);
                break;
            case "confirm":
                float price = sellItem.getAmount() * sellItemNBT.getFloat("sell");
                int inPlayerPossession = Utils.getNumberOfItem(player, sellItem);

                player.sendMessage("t'as " + inPlayerPossession + " amount = " + amount);

                if (sellItem.getAmount() > inPlayerPossession)
                {
                    player.sendMessage(ChatColor.RED + "[Erreur] Vous n'avez pas " +
                            sellItem.getAmount() + "x" + sellItem.getI18NDisplayName() + (sellItem.getAmount() > 1 ? " items " : " item ") + "a vendre");
                    player.sendMessage(ChatColor.RED + "Il vous manque " + (sellItem.getAmount() - inPlayerPossession) + "x" + sellItem.getI18NDisplayName());
                    return true;
                }

                plugin.economy.depositPlayer(player.getName(), price);
                player.sendMessage(ChatColor.GREEN + "[SkyShop] Vous avez vendu " + sellItem.getAmount() +
                        "x" + sellItem.getI18NDisplayName() + " pour " + String.format("%.2f", price) + ChatColor.WHITE +
                        SkyFont.getPlugin().getCharacter("money"));
                player.sendMessage(ChatColor.GREEN + "Vous avez à présent " + String.format("%.2f", playerBal + price) +
                        ChatColor.WHITE + SkyFont.getPlugin().getCharacter("money"));

                ItemStack rawItem = new ItemStack(sellItem.getType());
                rawItem.setAmount(sellItem.getAmount());

                player.getInventory().removeItemAnySlot(rawItem);

                Utils.openMenu((Player) player);

                LogAction(player, ShopAction.Sell, sellItem, sellItem.getAmount(), price);

                break;
            case "all":
                float allPrice = amount * sellItemNBT.getFloat("sell");

                plugin.economy.depositPlayer(player.getName(), allPrice);
                player.sendMessage(ChatColor.GREEN + "[SkyShop] Vous avez vendu " + amount +
                       "x" + sellItem.getI18NDisplayName() + " pour " + String.format("%.2f", allPrice) + ChatColor.WHITE +
                       SkyFont.getPlugin().getCharacter("money"));
                player.sendMessage(ChatColor.GREEN + "Vous avez à présent " + String.format("%.2f", playerBal + allPrice) +
                       ChatColor.WHITE + SkyFont.getPlugin().getCharacter("money"));

                ItemStack rawIAlltem = new ItemStack(sellItem.getType());
                rawIAlltem.setAmount(amount);

                player.getInventory().removeItemAnySlot(rawIAlltem);

                Utils.openMenu((Player) player);

                LogAction(player, ShopAction.Sell, sellItem, amount, allPrice);

                break;
            case "more":
                Utils.openStackSellPanel(player, sellItem, playerBal);
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
        if (checkForBuyStack(e))
            return;
        if (checkForSell(e))
            return;
        if (checkForSellStack(e))
            return;
    }

    void LogAction(Player player, ShopAction action, ItemStack item, int quantity, float price)
    {
        SimpleDateFormat formatter= new SimpleDateFormat("HH-mm-ss");
        SimpleDateFormat folderformatter= new SimpleDateFormat("dd-MM-yy");
        Date date = new Date(System.currentTimeMillis());

        NBTItem itemNBT = new NBTItem(item);

        String folderPath = defaultPath + folderformatter.format(date);

        logStr += "[" + folderformatter.format(date) + " " + formatter.format(date) + "] " + player.getName() +
                (action == ShopAction.Buy ? " bought " : " sold ") + "x" + quantity + " " + itemNBT.getString("shopitem") +
                " for " + price + "\n";

        //125 is debug value. 5000 is a better one for production builds
        //TODO: Set it to 5000
        if (logStr.length() < 125)
            return;

        plugin.getLogger().info(folderPath);

        Path defaultPathPath = Paths.get(defaultPath);
        Path path = Paths.get(folderPath);
        try {
            if (!Files.exists(defaultPathPath))
                Files.createDirectory(defaultPathPath);
            if (!Files.exists(path))
                Files.createDirectory(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(folderPath + "\\" + formatter.format(date) + ".txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(folderPath + "\\" + formatter.format(date) + ".txt");
            fileWriter.write(logStr);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logStr = "";
    }

    public ShopItem FindShopItem(String nbt)
    {
        return null;
    }
}
