package com.thaichainminer.commands;

import com.thaichainminer.ThaiChainMinerPlugin;
import com.thaichainminer.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MineCommand implements CommandExecutor {
    
    private ThaiChainMinerPlugin plugin;
    private ConfigManager configManager;
    
    public MineCommand(ThaiChainMinerPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "on":
                commandOn(sender);
                return true;
            case "off":
                commandOff(sender);
                return true;
            case "list":
                commandList(sender);
                return true;
            case "reload":
                commandReload(sender);
                return true;
            case "ban":
                if (args.length < 2) {
                    sender.sendMessage("❌ ใช้: /mine ban <ชื่อผู้เล่น>");
                    return true;
                }
                commandBan(sender, args[1]);
                return true;
            case "unban":
                if (args.length < 2) {
                    sender.sendMessage("❌ ใช้: /mine unban <ชื่อผู้เล่น>");
                    return true;
                }
                commandUnban(sender, args[1]);
                return true;
            case "banlist":
                commandBanlist(sender);
                return true;
            case "help":
                showHelp(sender);
                return true;
            default:
                sender.sendMessage("❌ คำสั่งไม่ถูกต้อง ใช้ /mine help เพื่อดูรายชื่อคำสั่ง");
                return true;
        }
    }
    
    private void commandOn(CommandSender sender) {
        if (!sender.hasPermission("thaichainminer.command.mine")) {
            sender.sendMessage(configManager.getMessage("no-permission"));
            return;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("❌ เฉพาะผู้เล่นเท่านั้นที่สามารถใช้คำสั่งนี้ได้");
            return;
        }
        
        Player player = (Player) sender;
        String worldName = player.getWorld().getName();
        
        if (configManager.isWorldEnabled(worldName)) {
            sender.sendMessage("⚠️ ระบบขุดแร่สายเปิดใช้งานอยู่แล้วในโลก: " + worldName);
            return;
        }
        
        configManager.addWorld(worldName);
        sender.sendMessage(configManager.getMessage("enabled", "world", worldName));
    }
    
    private void commandOff(CommandSender sender) {
        if (!sender.hasPermission("thaichainminer.command.mine")) {
            sender.sendMessage(configManager.getMessage("no-permission"));
            return;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("❌ เฉพาะผู้เล่นเท่านั้นที่สามารถใช้คำสั่งนี้ได้");
            return;
        }
        
        Player player = (Player) sender;
        String worldName = player.getWorld().getName();
        
        if (!configManager.isWorldEnabled(worldName)) {
            sender.sendMessage("⚠️ ระบบขุดแร่สายปิดใช้งานแล้วในโลก: " + worldName);
            return;
        }
        
        configManager.removeWorld(worldName);
        sender.sendMessage(configManager.getMessage("disabled", "world", worldName));
    }
    
    private void commandList(CommandSender sender) {
        if (!sender.hasPermission("thaichainminer.command.mine")) {
            sender.sendMessage(configManager.getMessage("no-permission"));
            return;
        }
        
        java.util.List<String> worlds = configManager.getEnabledWorlds();
        
        sender.sendMessage(configManager.getMessage("list-header"));
        for (String world : worlds) {
            sender.sendMessage(configManager.getMessage("list-item", "world", world));
        }
        sender.sendMessage(configManager.getMessage("list-footer"));
    }
    
    private void commandReload(CommandSender sender) {
        if (!sender.hasPermission("thaichainminer.command.mine")) {
            sender.sendMessage(configManager.getMessage("no-permission"));
            return;
        }
        
        configManager.reloadConfig();
        sender.sendMessage(configManager.getMessage("reloaded"));
    }
    
    private void commandBan(CommandSender sender, String playerName) {
        if (!sender.hasPermission("thaichainminer.command.mine")) {
            sender.sendMessage(configManager.getMessage("no-permission"));
            return;
        }
        
        if (configManager.isBanned(playerName)) {
            sender.sendMessage(configManager.getMessage("already-banned", "player", playerName));
            return;
        }
        
        configManager.addBannedPlayer(playerName);
        sender.sendMessage(configManager.getMessage("banned", "player", playerName));
    }
    
    private void commandUnban(CommandSender sender, String playerName) {
        if (!sender.hasPermission("thaichainminer.command.mine")) {
            sender.sendMessage(configManager.getMessage("no-permission"));
            return;
        }
        
        if (!configManager.isBanned(playerName)) {
            sender.sendMessage(configManager.getMessage("not-banned", "player", playerName));
            return;
        }
        
        configManager.removeBannedPlayer(playerName);
        sender.sendMessage(configManager.getMessage("unbanned", "player", playerName));
    }
    
    private void commandBanlist(CommandSender sender) {
        if (!sender.hasPermission("thaichainminer.command.mine")) {
            sender.sendMessage(configManager.getMessage("no-permission"));
            return;
        }
        
        java.util.List<String> bannedPlayers = configManager.getBannedPlayers();
        
        if (bannedPlayers.isEmpty()) {
            sender.sendMessage(configManager.getMessage("banlist-empty"));
            return;
        }
        
        sender.sendMessage(configManager.getMessage("banlist-header"));
        for (String player : bannedPlayers) {
            sender.sendMessage(configManager.getMessage("banlist-item", "player", player));
        }
        sender.sendMessage(configManager.getMessage("banlist-footer"));
    }
    
    private void showHelp(CommandSender sender) {
        sender.sendMessage(configManager.getMessage("help-header"));
        sender.sendMessage(configManager.getMessage("help-on"));
        sender.sendMessage(configManager.getMessage("help-off"));
        sender.sendMessage(configManager.getMessage("help-list"));
        sender.sendMessage(configManager.getMessage("help-reload"));
        sender.sendMessage(configManager.getMessage("help-ban"));
        sender.sendMessage(configManager.getMessage("help-unban"));
        sender.sendMessage(configManager.getMessage("help-banlist"));
        sender.sendMessage(configManager.getMessage("help-footer"));
    }
}