package org.frags.halloweenTrade;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.frags.halloweenTrade.commands.ImportCommand;
import org.frags.halloweenTrade.commands.OpenCommand;
import org.frags.halloweenTrade.files.DataFile;
import org.frags.halloweenTrade.files.ItemsFile;
import org.frags.halloweenTrade.menusystem.MenuListener;
import org.frags.halloweenTrade.menusystem.PlayerMenuUtility;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class HalloweenTrade extends JavaPlugin {


    public DataFile data;
    public ItemsFile items;

    public static final NamespacedKey requirementsKey = new NamespacedKey("halloween", "requirements");

    private final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();


    @Override
    public void onEnable() {
        // Plugin startup logic
        this.data = new DataFile(this);
        this.items = new ItemsFile(this);

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        getCommand("halloimport").setExecutor(new ImportCommand(this));
        getCommand("halloopen").setExecutor(new OpenCommand(this));

        getServer().getPluginManager().registerEvents(new MenuListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("halloreload")) {
            if (sender instanceof Player player) {
                if (player.isOp()) {
                    reloadConfig();
                    player.sendMessage("Se ha reconfigurado la configuracion");
                }
            }
        }

        return true;
    }

    public PlayerMenuUtility getPlayerMenuUtilityMap(Player player) {
        PlayerMenuUtility playerMenuUtility;
        if (!(playerMenuUtilityMap.containsKey(player))) {
            playerMenuUtility = new PlayerMenuUtility(player);
            playerMenuUtilityMap.put(player, playerMenuUtility);
            return playerMenuUtility;
        } else {
            return playerMenuUtilityMap.get(player);
        }
    }



}
