package skyshop.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import skychat.skychat.SkyChat;

import java.util.List;

public class DataPackItems {
    public static ItemStack getLeftArrow()
    {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setCustomModelData(1);
        itemMeta.setDisplayName(SkyChat.getGradient("Précédent", "#e7383c", "#f79617"));

        item.setItemMeta(itemMeta);

        return item;
    }

    public static ItemStack getRightArrow()
    {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setCustomModelData(2);
        itemMeta.setDisplayName(SkyChat.getGradient("Suivant", "#e7383c", "#f79617"));

        item.setItemMeta(itemMeta);

        return item;
    }

    public static ItemStack getBack()
    {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setCustomModelData(3);
        itemMeta.setDisplayName(SkyChat.getGradient("Retour", "#ff0000", "#f91217"));

        item.setItemMeta(itemMeta);

        return item;
    }

    public static ItemStack getInvisible(String name, List<String> desc, String from, String to)
    {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setCustomModelData(4);
        itemMeta.setDisplayName(SkyChat.getGradient(name, from, to));
        itemMeta.setLore(desc);

        item.setItemMeta(itemMeta);

        return item;
    }
}
