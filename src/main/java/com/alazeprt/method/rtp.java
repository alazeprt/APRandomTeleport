package com.alazeprt.method;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Random;

import static com.alazeprt.APRandomTeleport.cooldown;
import static com.alazeprt.APRandomTeleport.onTeleport;

public class rtp {
    private static JavaPlugin plugin = com.alazeprt.APRandomTeleport.getPlugin(com.alazeprt.APRandomTeleport.class);
    private static FileConfiguration message = YamlConfiguration.loadConfiguration(new File(com.alazeprt.APRandomTeleport.getProvidingPlugin(com.alazeprt.APRandomTeleport.class).getDataFolder(), "message.yml"));
    private static FileConfiguration config = YamlConfiguration.loadConfiguration(new File(com.alazeprt.APRandomTeleport.getProvidingPlugin(com.alazeprt.APRandomTeleport.class).getDataFolder(), "config.yml"));
    public static boolean executeRTP(CommandSender commandSender, World world){
        if(commandSender.getName().equalsIgnoreCase("CONSOLE")){
            commandSender.sendMessage(getFormatMessage("terminal"));
            return false;
        } else {
            Player player = (Player) commandSender;
            if(onTeleport.containsKey(player)){
                if(onTeleport.get(player)){
                    commandSender.sendMessage(getFormatMessage("already_in_rtp"));
                    return false;
                }
            }
            onTeleport.put(player, true);
            if(!cooldown.containsKey(player)){
                commandSender.sendMessage(getFormatMessage("find"));
                Location location;
                if(world == null){
                    location = getRandomLocation(player, player.getWorld());
                } else{
                    location = getRandomLocation(player, world);
                }
                if (location == null) {
                    player.sendMessage(getFormatMessage("rtp_failed").replace("{number}", String.valueOf(config.getInt("attempts"))));
                    onTeleport.put(player, false);
                    return false;
                } else {
                    long delay = config.getInt("delay") * 20L;
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        player.teleport(location);
                    }, delay);
                    player.sendMessage(getFormatMessage("rtp_successful"));
                    cooldown.put(player, config.getInt("cooldown"));
                    onTeleport.put(player, false);
                    return true;
                }
            }
            if(!(player.hasPermission("aprtp.bypass"))){
                if (cooldown.get(player) != 0) {
                    player.sendMessage(getFormatMessage("in_cooldown").replace("{number}", String.valueOf(cooldown.get(player))));
                    onTeleport.put(player, false);
                    return false;
                } else {
                    commandSender.sendMessage(getFormatMessage("find"));
                    Location location;
                    if(world == null){
                        location = getRandomLocation(player, player.getWorld());
                    } else{
                        location = getRandomLocation(player, world);
                    }
                    if (location == null) {
                        player.sendMessage(getFormatMessage("rtp_failed").replace("{number}", String.valueOf(config.getInt("attempts"))));
                        onTeleport.put(player, false);
                        return false;
                    } else {
                        long delay = config.getInt("delay") * 20L;
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            player.teleport(location);
                        }, delay);
                        player.sendMessage(getFormatMessage("rtp_successful"));
                        cooldown.put(player, config.getInt("cooldown"));
                        onTeleport.put(player, false);
                        return true;
                    }
                }
            }else {
                commandSender.sendMessage(getFormatMessage("find"));
                Location location;
                if(world == null){
                    location = getRandomLocation(player, player.getWorld());
                } else{
                    location = getRandomLocation(player, world);
                }
                if (location == null) {
                    player.sendMessage(getFormatMessage("rtp_failed").replace("{number}", String.valueOf(config.getInt("attempts"))));
                    onTeleport.put(player, false);
                    return false;
                } else {
                    long delay = config.getInt("delay") * 20L;
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        player.teleport(location);
                    }, delay);
                    player.sendMessage(getFormatMessage("rtp_successful"));
                    cooldown.put(player, config.getInt("cooldown"));
                    onTeleport.put(player, false);
                    return true;
                }
            }
        }
    }

    private static String getFormatMessage(String pos){
        return message.getString("prefix").replace("&", "§") + message.getString(pos).replace("&", "§");
    }

    private static Location getRandomLocation(Player player, World world){
        Random r = new Random();
        int maxy;
        int miny;
        if(world.getEnvironment() == World.Environment.NORMAL){
            maxy = 320-2;
            miny = -64;
        } else if(world.getEnvironment() == World.Environment.NETHER){
            maxy = 128-2;
            miny = 0;
        } else if(world.getEnvironment() == World.Environment.THE_END){
            maxy = 256-2;
            miny = 0;
        } else{
            maxy = 128-2;
            miny = 0;
        }
        if(config.getBoolean("limit_y.enabled")){
            if(config.getInt("limit_y.max_y") <= 128 && config.getInt("limit_y.min_y") >= 0){
                maxy = config.getInt("limit_y.max_y") - 2;
                miny = config.getInt("limit_y.min_y");
            } else{
                player.sendMessage(getFormatMessage("config_error"));
            }
        }
        for(int i=0;i<config.getInt("attempts");i++){
            int x = r.nextInt((config.getInt("distance.max_x") - config.getInt("distance.min_x"))) + config.getInt("distance.min_x");
            int z = r.nextInt((config.getInt("distance.max_x") - config.getInt("distance.min_x"))) + config.getInt("distance.min_x");
            Block block = world.getBlockAt(x, 64, z);
            Chunk chunk = world.getChunkAt(block);
            for(int y=maxy;y>=miny;y--){
                if(chunk.getBlock(x & 15, y+1, z & 15).getType() == Material.AIR || chunk.getBlock(x & 15, y+1, z & 15).getType() == Material.CAVE_AIR
                        && chunk.getBlock(x & 15, y+2, z & 15).getType() == Material.AIR || chunk.getBlock(x & 15, y+2, z & 15).getType() == Material.CAVE_AIR){
                    if(!config.getBoolean("blacklist.air")){
                        if(chunk.getBlock(x & 15, y, z & 15).getType() == Material.AIR || chunk.getBlock(x & 15, y, z & 15).getType() == Material.CAVE_AIR || chunk.getBlock(x & 15, y, z & 15).getType() == Material.VOID_AIR){
                            return new Location(world, x, y+1, z);
                        }
                    }
                    if(!config.getBoolean("blacklist.leaves")){
                        if (chunk.getBlock(x & 15, y, z & 15).getType() == Material.OAK_LEAVES || chunk.getBlock(x & 15, y, z & 15).getType() == Material.SPRUCE_LEAVES || chunk.getBlock(x & 15, y, z & 15).getType() == Material.BIRCH_LEAVES || chunk.getBlock(x & 15, y, z & 15).getType() == Material.JUNGLE_LEAVES || chunk.getBlock(x & 15, y, z & 15).getType() == Material.ACACIA_LEAVES || chunk.getBlock(x & 15, y, z & 15).getType() == Material.DARK_OAK_LEAVES) {
                            return new Location(world, x, y+1, z);
                        }
                    }
                    if(!config.getBoolean("blacklist.water")){
                        if(chunk.getBlock(x & 15, y, z & 15).getType() == Material.WATER){
                            return new Location(world, x, y+1, z);
                        }
                    }
                    if(!config.getBoolean("blacklist.lava")){
                        if(chunk.getBlock(x & 15, y, z & 15).getType() == Material.LAVA){
                            return new Location(world, x, y+1, z);
                        }
                    }
                    if(!config.getBoolean("blacklist.bedrock")){
                        if(chunk.getBlock(x & 15, y, z & 15).getType() == Material.BEDROCK){
                            return new Location(world, x, y+1, z);
                        }
                    } else{
                        if (!(chunk.getBlock(x & 15, y, z & 15).getType() == Material.CAVE_AIR)
                                && !(chunk.getBlock(x & 15, y, z & 15).getType() == Material.VOID_AIR)
                                && !(chunk.getBlock(x & 15, y, z & 15).getType() == Material.AIR)
                                && !(chunk.getBlock(x & 15, y, z & 15).getType() == Material.OAK_LEAVES)
                                && !(chunk.getBlock(x & 15, y, z & 15).getType() == Material.SPRUCE_LEAVES)
                                && !(chunk.getBlock(x & 15, y, z & 15).getType() == Material.BIRCH_LEAVES)
                                && !(chunk.getBlock(x & 15, y, z & 15).getType() == Material.JUNGLE_LEAVES)
                                && !(chunk.getBlock(x & 15, y, z & 15).getType() == Material.ACACIA_LEAVES)
                                && !(chunk.getBlock(x & 15, y, z & 15).getType() == Material.DARK_OAK_LEAVES)
                                && !(chunk.getBlock(x & 15, y, z & 15).getType() == Material.WATER)
                                && !(chunk.getBlock(x & 15, y, z & 15).getType() == Material.LAVA)
                                && !(chunk.getBlock(x & 15, y, z & 15).getType() == Material.BEDROCK)) {
                            return new Location(world, x, y+1, z);
                        }
                    }
                }
            }
        }
        return null;
    }
}
