package org.frags.halloweenTrade.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.frags.halloweenTrade.HalloweenTrade;
import org.frags.halloweenTrade.menusystem.PlayerMenuUtility;
import org.frags.halloweenTrade.menusystem.menu.SellMenu;
import org.jetbrains.annotations.NotNull;

public class OpenCommand implements CommandExecutor {

    private final HalloweenTrade plugin;

    public OpenCommand(HalloweenTrade plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player))
            return true;

        new SellMenu(plugin, new PlayerMenuUtility(player)).open();



        return true;
    }
}
