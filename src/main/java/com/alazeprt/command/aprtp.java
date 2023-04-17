package com.alazeprt.command;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.alazeprt.method.rtp.executeRTP;
import static com.alazeprt.method.cfg.reload;

public class aprtp implements CommandExecutor, TabCompleter {
   private FileConfiguration message = YamlConfiguration.loadConfiguration(new File(com.alazeprt.APRandomTeleport.getProvidingPlugin(com.alazeprt.APRandomTeleport.class).getDataFolder(), "message.yml"));
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 0){
            if(commandSender.hasPermission("aprtp.use")){
                Thread thread = new Thread(() -> {
                    executeRTP(commandSender, null);
                }, "APThread-2");
                thread.start();
            } else{
                commandSender.sendMessage(getFormatMessage("no_permission"));
            }
        } else if(strings.length == 1){
            if(strings[0].equals("help")){
                if(commandSender.hasPermission("aprtp.command.help")){
                    int i = 0;
                    for(String content : message.getStringList("help")){
                        if(i == 1){
                            if(commandSender.hasPermission("aprtp.use")){
                                commandSender.sendMessage(content.replace("&", "§"));
                            }
                        } else if(i == 2){
                            if(commandSender.hasPermission("aprtp.command.help")){
                                commandSender.sendMessage(content.replace("&", "§"));
                            }
                        } else if(i == 3){
                            if(commandSender.hasPermission("aprtp.command.reload")){
                                commandSender.sendMessage(content.replace("&", "§"));
                            }
                        } else if(i == 4){
                            if(commandSender.hasPermission("aprtp.world")){
                                commandSender.sendMessage(content.replace("&", "§"));
                            }
                        } else{
                            commandSender.sendMessage(content.replace("&", "§"));
                        }
                        i++;
                    }
                } else{
                    commandSender.sendMessage(getFormatMessage("no_permission"));
                }
            } else if(strings[0].equals("world")){
                if(commandSender.hasPermission("aprtp.use")){
                    commandSender.sendMessage(getFormatMessage("wrong_usage"));
                } else{
                    commandSender.sendMessage(getFormatMessage("no_permission"));
                }
            } else if(strings[0].equals("reload")){
                if(commandSender.hasPermission("aprtp.command.reload")){
                    reload();
                    commandSender.sendMessage(getFormatMessage("reload_successful"));
                } else{
                    commandSender.sendMessage(getFormatMessage("no_permission"));
                }
            } else{
                if(commandSender.hasPermission("aprtp.use")){
                    commandSender.sendMessage(getFormatMessage("wrong_usage"));
                } else{
                    commandSender.sendMessage(getFormatMessage("no_permission"));
                }
            }
        } else if(strings.length == 2){
            if(strings[0].equals("world")){
                if (Bukkit.getWorld(strings[1]) == null) {
                    commandSender.sendMessage(getFormatMessage("unknown_world").replace("{name}", strings[1]));
                } else {
                    if(commandSender.hasPermission("aprtp.world." +strings[1]) || commandSender.hasPermission("aprtp.world.*")){
                        Thread thread = new Thread(() -> {
                            executeRTP(commandSender, Bukkit.getWorld(strings[1]));
                        }, "APThread-2");
                        thread.start();
                    } else{
                        commandSender.sendMessage(getFormatMessage("no_permission"));
                    }
                }
            } else{
                if(commandSender.hasPermission("aprtp.use")){
                    commandSender.sendMessage(getFormatMessage("wrong_usage"));
                } else{
                    commandSender.sendMessage(getFormatMessage("no_permission"));
                }
            }
        } else{
            if(commandSender.hasPermission("aprtp.use")){
                commandSender.sendMessage(getFormatMessage("wrong_usage"));
            } else{
                commandSender.sendMessage(getFormatMessage("no_permission"));
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if(command.getName().equalsIgnoreCase("rtp")){
            int length = args.length;
            if(length == 1){
                if(sender.hasPermission("aprtp.world")){
                    list.add("world");
                }
                if(sender.hasPermission("aprtp.command.help")){
                    list.add("help");
                }
                if(sender.hasPermission("aprtp.command.reload")){
                    list.add("reload");
                }
            } else if(length == 2){
                for(World world : Bukkit.getWorlds()){
                    if(sender.hasPermission("aprtp.world." + world.getName()) || sender.hasPermission("aprtp.world.*")){
                        list.add(world.getName());
                    }
                }
            }
        }
        return list;
    }

    private String getFormatMessage(String pos){
        return message.getString("prefix").replace("&", "§") + message.getString(pos).replace("&", "§");
    }
}
