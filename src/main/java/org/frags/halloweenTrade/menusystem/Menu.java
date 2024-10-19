package org.frags.halloweenTrade.menusystem;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.frags.halloweenTrade.HalloweenTrade;

public abstract class Menu implements InventoryHolder {

    protected final HalloweenTrade plugin;

    protected PlayerMenuUtility playerMenuUtility;

    protected Inventory inventory;

    public Menu(HalloweenTrade plugin, PlayerMenuUtility playerMenuUtility) {
        this.plugin = plugin;
        this.playerMenuUtility = playerMenuUtility;
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent e);

    public abstract void setMenuItems();

    public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        this.setMenuItems();

        playerMenuUtility.getOwner().openInventory(inventory);
    }

    @SuppressWarnings("Deprecated")
    protected String miniMessageParser(String string) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        TextComponent text = (TextComponent) miniMessage.deserialize(string);
        return ChatColor.translateAlternateColorCodes('&', text.content());
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setFillerGlass(Material itemStack) {
        if (itemStack == null)
            return;
        if (itemStack == Material.AIR)
            return;
        ItemStack filler_glass = new ItemStack(itemStack);

        for (int i = 0; i < getSlots(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler_glass);
            }
        }
    }

    public int getLimit(String key) {
        return plugin.data.getConfig().getInt(playerMenuUtility.getOwner().getName() + "."
        + key);
    }

    public void setLimit(String key, int value) {
        plugin.data.getConfig().set(playerMenuUtility.getOwner().getName() + "." +
                key, value);
        plugin.data.saveConfig();
    }
}
