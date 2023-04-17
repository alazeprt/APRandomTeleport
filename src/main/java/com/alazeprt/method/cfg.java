package com.alazeprt.method;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class cfg {
    private static final JavaPlugin plugin = com.alazeprt.APRandomTeleport.getPlugin(com.alazeprt.APRandomTeleport.class);
    private static FileConfiguration message = YamlConfiguration.loadConfiguration(new File(com.alazeprt.APRandomTeleport.getProvidingPlugin(com.alazeprt.APRandomTeleport.class).getDataFolder(), "message.yml"));
    private static FileConfiguration config = YamlConfiguration.loadConfiguration(new File(com.alazeprt.APRandomTeleport.getProvidingPlugin(com.alazeprt.APRandomTeleport.class).getDataFolder(), "config.yml"));
    public static void reload(){
        File messageFile = new File(plugin.getDataFolder(), "message.yml");
        if (!messageFile.exists()) {
            plugin.saveResource("message.yml", false);
        }
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        message = YamlConfiguration.loadConfiguration(messageFile);
        config = YamlConfiguration.loadConfiguration(configFile);
    }
}
