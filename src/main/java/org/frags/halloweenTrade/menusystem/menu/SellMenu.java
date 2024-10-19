package org.frags.halloweenTrade.menusystem.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.ListPersistentDataType;
import org.bukkit.persistence.ListPersistentDataTypeProvider;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.frags.halloweenTrade.HalloweenTrade;
import org.frags.halloweenTrade.menusystem.Menu;
import org.frags.halloweenTrade.menusystem.PlayerMenuUtility;

import java.util.ArrayList;
import java.util.List;

public class SellMenu extends Menu {

    private FileConfiguration config;
    private ConfigurationSection section;

    private final NamespacedKey itemKey = new NamespacedKey("pl", "item");
    private final NamespacedKey limitKey = new NamespacedKey("key", "limit");

    public SellMenu(HalloweenTrade plugin, PlayerMenuUtility playerMenuUtility) {
        super(plugin, playerMenuUtility);
        config = plugin.getConfig();
        section = config.getConfigurationSection("shop-menu");
    }

    @Override
    public String getMenuName() {
        return miniMessageParser("&0Tienda de Halloween");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack itemStack = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();
        PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
        if (!container.has(HalloweenTrade.requirementsKey))
            return;

        String requirements = container.get(HalloweenTrade.requirementsKey, PersistentDataType.STRING);

        String[] requirement = requirements.split(";");

        boolean remove = true;

        String itemKeyString = container.get(itemKey, PersistentDataType.STRING);

        int restante = container.get(limitKey, PersistentDataType.INTEGER);

        if (restante <= 0) {
            //Player can't buy
            player.closeInventory();
            player.sendMessage(miniMessageParser("&cYa has comprado la máxima cantidad de este item!"));
            return;
        }

        for (String checkRequirement : requirement) {
            String[] amountRequirement = checkRequirement.split(",");
            int amount = Integer.parseInt(amountRequirement[1]);
            String item = amountRequirement[0];

            ItemStack itemToCheck = plugin.items.getConfig().getItemStack(item);
            if (!itemCounter(player, itemToCheck, amount)) {
                remove = false;
                break;
            }
        }

        if (remove) {
            for (String checkRequirement : requirement) {
                String[] amountRequirement = checkRequirement.split(",");
                int amount = Integer.parseInt(amountRequirement[1]);

                String item = amountRequirement[0];
                ItemStack itemToCheck = plugin.items.getConfig().getItemStack(item);

                for (ItemStack itemInInventory : player.getInventory().getContents()) {
                    if (itemInInventory != null && itemInInventory.isSimilar(itemToCheck)) {
                        int inventoryAmount = itemInInventory.getAmount();  // Get the amount in this stack
                        int amountToRemove = Math.min(inventoryAmount, amount);  // Determine how much to remove from this stack

                        // Remove the calculated amount from the stack
                        if (amountToRemove > 0) {
                            itemInInventory.setAmount(inventoryAmount - amountToRemove);  // Adjust the stack's size
                        }

                        // Update the total amount left to remove
                        amount -= amountToRemove;
                    }
                }
            }
            ItemStack itemToGive = plugin.items.getConfig().getItemStack(itemKeyString);
            player.getInventory().addItem(itemToGive);
            player.closeInventory();
            player.sendMessage(miniMessageParser("&aHas comprado exitosamente el item&r " + itemToGive.getItemMeta().getDisplayName()));
            setLimit(itemKeyString, getLimit(itemKeyString) + 1);
        } else {
            player.closeInventory();
            player.sendMessage(miniMessageParser("&cNo tienes suficientes materiales!"));
        }
    }

    @Override
    public void setMenuItems() {

        for (String key : section.getKeys(false)) {
            ConfigurationSection finalSection = section.getConfigurationSection(key);

            String item = finalSection.getString("item");
            if (item == null || item.isEmpty()) {
                Bukkit.getConsoleSender().sendMessage(miniMessageParser("&cItem in key: " +
                        key + " is null!"));
                continue;
            }
            ItemStack itemStack;
            try {
                itemStack = new ItemStack(Material.valueOf(item));
            } catch (IllegalArgumentException e) {
                itemStack = plugin.items.getConfig().getItemStack(item).clone();
            }

            ItemMeta meta = itemStack.getItemMeta();

            List<String> lore = meta.getLore();
            if (lore == null)
                lore = new ArrayList<>();


            List<String> loreAdditive = finalSection.getStringList("loreadditive");
            for (String line : loreAdditive) {
                lore.add(miniMessageParser(line));
            }

            int limit = finalSection.getInt("limit");

            int restante = limit - getLimit(item);

            lore.add(miniMessageParser("&6Puedes comprar: " + restante));

            meta.setLore(lore);

            PersistentDataContainer container = meta.getPersistentDataContainer();

            List<String> requirements = finalSection.getStringList("requirements");

            String requirement = null;

            for (String request : requirements) {
                if (requirement == null || requirement.isEmpty()) {
                    requirement = request;
                    continue;
                }
                requirement = requirement + ";" + request;
            }


            container.set(HalloweenTrade.requirementsKey, PersistentDataType.STRING, requirement);
            container.set(itemKey, PersistentDataType.STRING, item);
            container.set(limitKey, PersistentDataType.INTEGER, restante);
            itemStack.setItemMeta(meta);
            int slot;
            try {
                slot = Integer.parseInt(key);
            } catch (NumberFormatException e) {
                Bukkit.getConsoleSender().sendMessage(miniMessageParser("&cNumber " + key + "isn't a number"));
                continue;
            }
            inventory.setItem(slot, itemStack);
        }

        setFillerGlass(Material.GRAY_STAINED_GLASS_PANE);
    }

    private boolean itemCounter(Player player, ItemStack itemStack, int amount) {
        int count = 0;
        PlayerInventory playerInventory = player.getInventory();
        for (ItemStack item : playerInventory.getContents()) {
            if (count >= amount) {
                return true;
            }
            if (item != null && item.isSimilar(itemStack)) {
                count = count + item.getAmount();
            }
        }
        return count >= amount;
    }
}
