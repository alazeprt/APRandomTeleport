package com.alazeprt;

import com.alazeprt.command.aprtp;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class APRandomTeleport extends JavaPlugin implements CommandExecutor {
    public static Map<Player, Integer> cooldown = new HashMap<>();
    public static Map<Player, Boolean> onTeleport = new HashMap<>();
    Thread cooldownThread = new Thread(() -> {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            if(!(cooldown == null)){
                for (Map.Entry<Player, Integer> entry : cooldown.entrySet()) {
                    Player key = entry.getKey();
                    Integer value = entry.getValue();
                    if(!(value <= 0)){
                        cooldown.replace(key, value-1);
                    }
                }
            }
        }, 0L, 20L);
    }, "APThread-2");
    @Override
    public void onEnable() {
        File messageFile = new File(getDataFolder(), "message.yml");
        if (!messageFile.exists()) {
            saveResource("message.yml", false);
        }
        File configFile = new File(getDataFolder(), "config.yml");
        if(!configFile.exists()){
            saveResource("config.yml", false);
        }
        Objects.requireNonNull(getCommand("rtp")).setExecutor(new aprtp());
        Objects.requireNonNull(getCommand("rtp")).setTabCompleter(new aprtp());
        getLogger().log(Level.INFO, "APRandomTeleport plugin enabled.");
        cooldownThread.start();
    }

    @Override
    public void onDisable() {
        cooldownThread.stop();
        getLogger().log(Level.INFO, "APRandomTeleport plugin disabled.");
    }
}
