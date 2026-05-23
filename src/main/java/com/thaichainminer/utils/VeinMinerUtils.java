package com.thaichainminer.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;

public class VeinMinerUtils {
    
    /**
     * ค้นหาสายของบล็อกชนิดเดียวกันที่เชื่อมต่อกัน
     */
    public static Set<Block> findVein(Block startBlock, Material material, int maxBlocks) {
        Set<Block> vein = new HashSet<>();
        Queue<Block> queue = new LinkedList<>();
        
        queue.add(startBlock);
        vein.add(startBlock);
        
        while (!queue.isEmpty() && vein.size() < maxBlocks) {
            Block current = queue.poll();
            
            // ตรวจสอบบล็อกข้างเคียงทั้ง 6 ด้าน
            Block[] neighbors = {
                    current.getRelative(1, 0, 0),
                    current.getRelative(-1, 0, 0),
                    current.getRelative(0, 1, 0),
                    current.getRelative(0, -1, 0),
                    current.getRelative(0, 0, 1),
                    current.getRelative(0, 0, -1)
            };
            
            for (Block neighbor : neighbors) {
                if (!vein.contains(neighbor) && neighbor.getType() == material && vein.size() < maxBlocks) {
                    vein.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        
        return vein;
    }
}