package com.thaichainminer.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    
    private JavaPlugin plugin;
    private FileConfiguration config;
    private File configFile;
    
    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        
        config = YamlConfiguration.loadConfiguration(configFile);
        plugin.getLogger().info("✅ โหลดไฟล์ config สำเร็จ");
    }
    
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("❌ ไม่สามารถบันทึกไฟล์ config: " + e.getMessage());
        }
    }
    
    public void reloadConfig() {
        loadConfig();
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    // ดึงรายชื่อโลกที่เปิดใช้งาน
    public List<String> getEnabledWorlds() {
        return config.getStringList("enabled-worlds");
    }
    
    // ตรวจสอบว่าโลกเปิดใช้งานหรือไม่
    public boolean isWorldEnabled(String world) {
        return getEnabledWorlds().contains(world);
    }
    
    // เพิ่มโลก
    public void addWorld(String world) {
        List<String> worlds = getEnabledWorlds();
        if (!worlds.contains(world)) {
            worlds.add(world);
            config.set("enabled-worlds", worlds);
            saveConfig();
        }
    }
    
    // ลบโลก
    public void removeWorld(String world) {
        List<String> worlds = getEnabledWorlds();
        worlds.remove(world);
        config.set("enabled-worlds", worlds);
        saveConfig();
    }
    
    // ดึงจำนวนบล็อก STONE ต่ำสุดในสาย
    public int getStoneMinVeinSize() {
        return config.getInt("stone-miner.min-vein-size", 10);
    }
    
    // ดึงจำนวนบล็อก STONE สูงสุดที่ขุดได้
    public int getStoneMaxBlocks() {
        return config.getInt("stone-miner.max-blocks-per-break", 4);
    }
    
    // ตรวจสอบว่า Stone Miner เปิดใช้งานหรือไม่
    public boolean isStoneMinerEnabled() {
        return config.getBoolean("stone-miner.enabled", true);
    }
    
    // ดึงค่าสูงสุดของแร่ที่กำหนด
    public int getOreMaxBlocks(String ore) {
        return config.getInt("vein-miner." + ore + ".max-blocks", 32);
    }
    
    // ตรวจสอบว่าแร่เปิดใช้งานหรือไม่
    public boolean isOreEnabled(String ore) {
        return config.getBoolean("vein-miner." + ore + ".enabled", true);
    }
    
    // ดึงความเสียหายต่อบล็อก
    public int getDamagePerBlock(String pickaxeTier) {
        return config.getInt("pickaxe-damage." + pickaxeTier + ".damage-per-block", 1);
    }
    
    // ข้อความ
    public String getMessage(String key) {
        return config.getString("messages." + key, "");
    }
    
    // ข้อความพร้อมแทนที่ตัวแปร
    public String getMessage(String key, String... replacements) {
        String message = getMessage(key);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
            }
        }
        return message;
    }
    
    // ดึงรายชื่อผู้เล่นที่ถูกแบน
    public List<String> getBannedPlayers() {
        return config.getStringList("banned-players");
    }
    
    // เพิ่มผู้เล่นลงในบัญชีแบน
    public void addBannedPlayer(String playerName) {
        List<String> bannedPlayers = getBannedPlayers();
        if (!bannedPlayers.contains(playerName)) {
            bannedPlayers.add(playerName);
            config.set("banned-players", bannedPlayers);
            saveConfig();
        }
    }
    
    // ลบผู้เล่นออกจากบัญชีแบน
    public void removeBannedPlayer(String playerName) {
        List<String> bannedPlayers = getBannedPlayers();
        bannedPlayers.remove(playerName);
        config.set("banned-players", bannedPlayers);
        saveConfig();
    }
    
    // ตรวจสอบว่าผู้เล่นถูกแบนหรือไม่
    public boolean isBanned(String playerName) {
        return getBannedPlayers().contains(playerName);
    }
}