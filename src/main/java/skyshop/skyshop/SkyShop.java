package skyshop.skyshop;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import skychat.skychat.SkyChat;
import skyfont.skyfont.SkyFont;
import skyshop.categories.Category;
import skyshop.categories.CategoryLoader;
import skyshop.commands.ShopCommand;
import skyshop.events.ShopEvent;

import java.util.ArrayList;
import java.util.List;

public final class SkyShop extends JavaPlugin {
    public SkyFont skyFont;
    public SkyChat skyChat;
    public Economy economy;

    public List<Category> categories = new ArrayList<>();
    CategoryLoader categoryLoader = new CategoryLoader();

    public static SkyShop skyShop;

    @Override
    public void onEnable() {
        skyShop = this;

        //Load dependencies
        skyFont = SkyFont.getPlugin();
        skyChat = SkyChat.getPlugin();

        //Get vault economy
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        economy = rsp.getProvider();

        //TODO : Make a category in config.yml to load it in proper way
        loadCategories();

        getServer().getPluginManager().registerEvents(new ShopEvent(this), this);

        registerCommands();

        getLogger().info("plugin started");
    }

    void loadCategories()
    {
        categories.add(categoryLoader.loadCategory(this, "ores"));
    }

    public Category getCategory(String name)
    {
        for (Category cat : categories)
            if (cat.name.equals(name))
                return cat;

        return null;
    }

    public Category getCategoryByContainingInventoryName(String name)
    {
        for (Category cat : categories)
            if (name.contains(cat.invName))
                return cat;

        return null;
    }

    @Override
    public void onDisable() {
        getLogger().info("plugin stopped");
    }

    void registerCommands()
    {
        this.getServer().getCommandMap().register("NeoShop", new ShopCommand("shop", this));
    }
}
