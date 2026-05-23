package com.thaichainminer.listeners;

import com.thaichainminer.ThaiChainMinerPlugin;
import com.thaichainminer.managers.ConfigManager;
import com.thaichainminer.utils.VeinMinerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class BlockBreakListener implements Listener {
    
    private ThaiChainMinerPlugin plugin;
    private ConfigManager configManager;
    
    public BlockBreakListener(ThaiChainMinerPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack tool = player.getInventory().getItemInMainHand();
        
        // ตรวจสอบว่าเปิดใช้งานในโลกนี้หรือไม่
        if (!configManager.isWorldEnabled(block.getWorld().getName())) {
            return;
        }
        
        // ตรวจสอบว่าผู้เล่นถูกแบนหรือไม่
        if (configManager.isBanned(player.getName())) {
            return;
        }
        
        // ตรวจสอบว่าผู้เล่นถือที่ขุดหรือไม่
        if (!isPickaxe(tool.getType())) {
            return;
        }
        
        Material blockType = block.getType();
        
        // ตรวจสอบว่าเป็นบล็อก STONE หรือแร่ที่รองรับหรือไม่
        if (blockType == Material.STONE) {
            handleStoneMiner(event, player, block, tool);
        } else if (isOreBlock(blockType)) {
            handleVeinMiner(event, player, block, tool);
        }
    }
    
    private void handleStoneMiner(BlockBreakEvent event, Player player, Block block, ItemStack tool) {
        if (!configManager.isStoneMinerEnabled()) {
            return;
        }
        
        // ค้นหาสายหิน
        Set<Block> stoneVein = VeinMinerUtils.findVein(block, Material.STONE, configManager.getStoneMaxBlocks());
        
        // ตรวจสอบว่ามีเพียงพอหิน
        if (stoneVein.size() < configManager.getStoneMinVeinSize()) {
            return; // ขุดปกติแค่ 1 บล็อก
        }
        
        // ยกเลิกดรอปของบล็อกแรก
        event.setDropItems(false);
        
        // ทำลายหินทั้งหมดแบบทยอยเป็นระลอก
        List<Block> blockList = new ArrayList<>(stoneVein);
        breakBlocksCascade(player, blockList, tool, "STONE");
    }
    
    private void handleVeinMiner(BlockBreakEvent event, Player player, Block block, ItemStack tool) {
        Material blockType = block.getType();
        String oreName = blockType.toString();
        
        // ตรวจสอบว่าแร่เปิดใช้งานหรือไม่
        if (!configManager.isOreEnabled(oreName)) {
            return;
        }
        
        // ตรวจสอบเทียร์ของที่ขุด
        if (!canMineOre(tool.getType(), blockType)) {
            event.setDropItems(false);
            return;
        }
        
        // ค้นหาสายแร่
        int maxBlocks = configManager.getOreMaxBlocks(oreName);
        Set<Block> oreVein = VeinMinerUtils.findVein(block, blockType, maxBlocks);
        
        // ยกเลิกดรอปของบล็อกแรก
        event.setDropItems(false);
        
        // ทำลายแร่ทั้งหมดแบบทยอยเป็นระลอก
        List<Block> blockList = new ArrayList<>(oreVein);
        breakBlocksCascade(player, blockList, tool, oreName);
    }
    
    private void breakBlocksCascade(Player player, List<Block> blocks, ItemStack tool, String blockName) {
        int delay = 0;
        int soundDelay = configManager.getConfigManager().getConfig().getInt("sound.sound-delay", 100);
        boolean doubleSoundSimulation = configManager.getConfigManager().getConfig().getBoolean("sound.double-sound-simulation", true);
        
        for (int i = 0; i < blocks.size(); i++) {
            Block b = blocks.get(i);
            final Block blockToBreak = b;
            
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if (blockToBreak.getType() != Material.AIR) {
                    // ลดความทนทาน
                    damageTool(tool, player, 1);
                    
                    // ทำลายบล็อก
                    blockToBreak.breakNaturally(tool);
                    
                    // เล่นเสียง
                    playBreakSound(player, blockToBreak, blocks.size(), doubleSoundSimulation);
                }
            }, delay);
            
            delay += soundDelay;
        }
    }
    
    private void playBreakSound(Player player, Block block, int totalBlocks, boolean doubleSoundSimulation) {
        if (!configManager.getConfigManager().getConfig().getBoolean("sound.enabled", true)) {
            return;
        }
        
        Random random = new Random();
        float pitch = 0.9f + (random.nextFloat() * 0.2f); // 0.9 - 1.1
        float volumeMultiplier = configManager.getConfigManager().getConfig().getFloat("sound.volume-multiplier", 1.0f);
        float volume = (float) (0.5 + (totalBlocks * 0.1)) * volumeMultiplier;
        
        // เล่นเสียงจริง
        player.getWorld().playSound(block.getLocation(), org.bukkit.Sound.BLOCK_STONE_BREAK, volume, pitch);
        
        // เล่นเสียงจำลอง (Double Sound Simulation)
        if (doubleSoundSimulation) {
            player.getWorld().playSound(block.getLocation(), org.bukkit.Sound.BLOCK_STONE_BREAK, volume * 0.8f, pitch * 1.1f);
        }
    }
    
    private void damageTool(ItemStack tool, Player player, int damage) {
        if (tool == null || !(tool.getItemMeta() instanceof Damageable)) {
            return;
        }
        
        Damageable damageable = (Damageable) tool.getItemMeta();
        
        // ตรวจสอบ Unbreaking
        int unbreakingLevel = tool.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.UNBREAKING);
        if (unbreakingLevel > 0) {
            Random random = new Random();
            int chance = unbreakingLevel + 1;
            if (random.nextInt(chance) != 0) {
                return; // ไม่ลดความทนทาน
            }
        }
        
        // คำนวณความเสียหาย
        int newDamage = damageable.getDamage() + damage;
        int maxDamage = tool.getType().getMaxDurability();
        
        if (newDamage >= maxDamage) {
            // ไม่มีข้อความแจ้งเตือน - เล่นเสียงไอเทมพังดั้งเดิมและลบทีอย่างเงียบๆ
            player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            player.getInventory().setItemInMainHand(null);
        } else {
            damageable.setDamage(newDamage);
            tool.setItemMeta((ItemMeta) damageable);
        }
    }
    
    private boolean isPickaxe(Material material) {
        return material == Material.WOODEN_PICKAXE
                || material == Material.STONE_PICKAXE
                || material == Material.IRON_PICKAXE
                || material == Material.GOLDEN_PICKAXE
                || material == Material.DIAMOND_PICKAXE
                || material == Material.NETHERITE_PICKAXE;
    }
    
    private boolean isOreBlock(Material material) {
        return material.toString().contains("ORE") || material.toString().contains("DEBRIS");
    }
    
    private boolean canMineOre(Material pickaxe, Material ore) {
        // ตรวจสอบตามระดับของแร่
        if (ore == Material.DIAMOND_ORE || ore == Material.DEEPSLATE_DIAMOND_ORE) {
            return pickaxe == Material.IRON_PICKAXE || pickaxe == Material.DIAMOND_PICKAXE || pickaxe == Material.NETHERITE_PICKAXE;
        }
        if (ore == Material.EMERALD_ORE || ore == Material.DEEPSLATE_EMERALD_ORE) {
            return pickaxe == Material.IRON_PICKAXE || pickaxe == Material.DIAMOND_PICKAXE || pickaxe == Material.NETHERITE_PICKAXE;
        }
        if (ore == Material.ANCIENT_DEBRIS) {
            return pickaxe == Material.DIAMOND_PICKAXE || pickaxe == Material.NETHERITE_PICKAXE;
        }
        if (ore == Material.GOLD_ORE || ore == Material.DEEPSLATE_GOLD_ORE) {
            return pickaxe == Material.IRON_PICKAXE || pickaxe == Material.DIAMOND_PICKAXE || pickaxe == Material.NETHERITE_PICKAXE;
        }
        if (ore == Material.IRON_ORE || ore == Material.DEEPSLATE_IRON_ORE) {
            return pickaxe == Material.STONE_PICKAXE || pickaxe == Material.IRON_PICKAXE || pickaxe == Material.DIAMOND_PICKAXE || pickaxe == Material.NETHERITE_PICKAXE;
        }
        
        // แร่อื่นๆ ต้องใช้ที่ขุดหิน
        return pickaxe == Material.WOODEN_PICKAXE || pickaxe == Material.STONE_PICKAXE || pickaxe == Material.IRON_PICKAXE || pickaxe == Material.GOLDEN_PICKAXE || pickaxe == Material.DIAMOND_PICKAXE || pickaxe == Material.NETHERITE_PICKAXE;
    }
}