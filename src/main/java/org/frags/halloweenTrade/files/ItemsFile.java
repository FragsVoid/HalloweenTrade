package org.frags.halloweenTrade.files;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.frags.halloweenTrade.HalloweenTrade;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class ItemsFile {

    private final HalloweenTrade plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;


    public ItemsFile(HalloweenTrade plugin) {
        this.plugin = plugin;
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (this.configFile == null)
            this.configFile = new File(plugin.getDataFolder(), "items.yml");

        dataConfig = YamlConfiguration.loadConfiguration(configFile);

        InputStream defaultStream = plugin.getResource("items.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.
                    loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (this.dataConfig == null)
            reloadConfig();
        return dataConfig;
    }

    public void saveConfig() {
        if (dataConfig == null || configFile == null)
            return;

        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't save " +
                    this.configFile, e);
        }
    }

    public void saveDefaultConfig() {
        if (this.configFile == null)
            this.configFile = new File(plugin.getDataFolder(), "items.yml");

        if (!configFile.exists()) {
            plugin.saveResource("items.yml", false);
        }
    }
}
