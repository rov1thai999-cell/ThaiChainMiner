package com.thaichainminer;

import com.thaichainminer.commands.MineCommand;
import com.thaichainminer.listeners.BlockBreakListener;
import com.thaichainminer.managers.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ThaiChainMinerPlugin extends JavaPlugin {
    
    private ConfigManager configManager;
    
    @Override
    public void onEnable() {
        // โหลด config
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        // ลงทะเบียน commands
        getCommand("mine").setExecutor(new MineCommand(this, configManager));
        
        // ลงทะเบียน listeners
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this, configManager), this);
        
        getLogger().info("✅ ThaiChainMiner เปิดใช้งานสำเร็จ!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("❌ ThaiChainMiner ปิดใช้งาน");
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
}