package org.frags.halloweenTrade.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.frags.halloweenTrade.HalloweenTrade;
import org.jetbrains.annotations.NotNull;

public class ImportCommand implements CommandExecutor {

    private final HalloweenTrade plugin;

    public ImportCommand(HalloweenTrade plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player))
            return true;

        if (!player.isOp())
            return true;

        if (args.length != 1)
            return true;

        if (player.getInventory().getItemInMainHand().getType() == Material.AIR)
            return true;

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        itemStack.setAmount(1);

        String key = args[0];

        plugin.items.getConfig().set(key, itemStack);
        plugin.items.saveConfig();

        player.sendMessage("Item guardado en " + key);

        return true;
    }
}
