package skyshop.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import skyshop.skyshop.SkyShop;
import skyshop.utils.Utils;

public class ShopCommand extends Command {
    SkyShop plugin;

    public ShopCommand(@NotNull String name, SkyShop _plugin) {
        super(name);

        plugin = _plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player))
        {
            plugin.getLogger().warning("You need to be a player to use this command");

            return false;
        }

        Player player = (Player) sender;

        Utils.openMenu(player);

        return true;
    }
}
