package org.purpurmc.purpur.memory;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Main class demonstrating the usage of CompressedChunkSection and EntityPool.
 * This serves as an entry point to showcase practical examples of both classes.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("=== Memory Optimization Demo ===\n");
        
        demonstrateCompressedChunkSection();
        System.out.println();
        demonstrateEntityPool();
    }
    
    /**
     * Demonstrates CompressedChunkSection usage:
     * - Setting block states
     * - Retrieving block states
     * - Memory optimization
     */
    private static void demonstrateCompressedChunkSection() {
        System.out.println("--- CompressedChunkSection Demo ---");
        
        // Create a new chunk section at Y=64
        CompressedChunkSection section = new CompressedChunkSection(64);
        System.out.println("Created chunk section: " + section);
        
        // Get some common block states
        BlockState stone = Blocks.STONE.defaultBlockState();
        BlockState dirt = Blocks.DIRT.defaultBlockState();
        BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();
        BlockState diamond = Blocks.DIAMOND_ORE.defaultBlockState();
        
        // Set various block states in the section
        System.out.println("\nSetting block states...");
        
        // Create a simple terrain pattern
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // Bottom layer: stone
                section.setBlockState(x, 0, z, stone);
                
                // Middle layers: dirt
                for (int y = 1; y < 3; y++) {
                    section.setBlockState(x, y, z, dirt);
                }
                
                // Top layer: grass
                section.setBlockState(x, 3, z, grass);
                
                // Add some diamond ore randomly
                if ((x + z) % 7 == 0) {
                    section.setBlockState(x, 1, z, diamond);
                }
            }
        }
        
        System.out.println("Block states set: " + section);
        
        // Retrieve some block states to verify
        System.out.println("\nRetrieving block states:");
        System.out.println("  Block at (0, 0, 0): " + section.getBlockState(0, 0, 0));
        System.out.println("  Block at (0, 1, 0): " + section.getBlockState(0, 1, 0));
        System.out.println("  Block at (0, 3, 0): " + section.getBlockState(0, 3, 0));
        System.out.println("  Block at (7, 1, 0): " + section.getBlockState(7, 1, 0));
        
        // Display memory statistics
        System.out.println("\nMemory statistics:");
        System.out.println("  Non-empty blocks: " + section.getNonEmptyBlockCount());
        System.out.println("  Palette size: " + section.getPaletteSize());
        System.out.println("  Memory used: " + section.getMemorySize() + " bytes");
        System.out.println("  Compression ratio: " + String.format("%.2f%%", section.getCompressionRatio() * 100));
        System.out.println("  Memory saved: " + String.format("%.2f%%", (1.0 - section.getCompressionRatio()) * 100));
        
        // Optimize the palette
        System.out.println("\nOptimizing palette...");
        section.optimizePalette();
        System.out.println("After optimization: " + section);
        
        // Check if section is empty
        System.out.println("Is section empty? " + section.isEmpty());
    }
    
    /**
     * Demonstrates EntityPool usage:
     * - Creating poolable objects
     * - Acquiring from pool
     * - Releasing to pool
     * - Checking pool statistics
     */
    private static void demonstrateEntityPool() {
        System.out.println("--- EntityPool Demo ---");
        
        // Create an entity pool with max size of 10
        EntityPool<SamplePoolableEntity> pool = new EntityPool<>(10);
        System.out.println("Created entity pool with max size: 10");
        
        // Initially, pool is empty, so we need to create objects
        System.out.println("\nAcquiring entities (creating new ones initially):");
        
        SamplePoolableEntity entity1 = pool.acquireOrCreate(
            SamplePoolableEntity.class, 
            () -> new SamplePoolableEntity("Zombie", 20, 100.0, 64.0, 200.0)
        );
        System.out.println("  Acquired: " + entity1);
        
        SamplePoolableEntity entity2 = pool.acquireOrCreate(
            SamplePoolableEntity.class,
            () -> new SamplePoolableEntity("Skeleton", 15, 150.0, 70.0, 250.0)
        );
        System.out.println("  Acquired: " + entity2);
        
        SamplePoolableEntity entity3 = pool.acquireOrCreate(
            SamplePoolableEntity.class,
            () -> new SamplePoolableEntity("Creeper", 10, 120.0, 65.0, 220.0)
        );
        System.out.println("  Acquired: " + entity3);
        
        // Display initial statistics
        System.out.println("\nInitial pool statistics: " + pool.getStats());
        
        // Release entities back to the pool
        System.out.println("\nReleasing entities back to pool...");
        pool.release(entity1);
        pool.release(entity2);
        pool.release(entity3);
        
        System.out.println("Pool statistics after release: " + pool.getStats());
        System.out.println("Total pooled objects: " + pool.getTotalPooledObjects());
        System.out.println("Pool size for SamplePoolableEntity: " + 
            pool.getPoolSize(SamplePoolableEntity.class));
        
        // Acquire again (should come from pool this time)
        System.out.println("\nAcquiring entities again (from pool):");
        
        SamplePoolableEntity reused1 = pool.acquireOrCreate(
            SamplePoolableEntity.class,
            () -> new SamplePoolableEntity("NewEntity", 100, 0, 0, 0)
        );
        System.out.println("  Acquired (reused): " + reused1);
        
        // Set new values on the reused entity
        reused1.setName("Enderman");
        reused1.setHealth(40);
        reused1.setX(300.0);
        reused1.setY(80.0);
        reused1.setZ(400.0);
        System.out.println("  Modified to: " + reused1);
        
        SamplePoolableEntity reused2 = pool.acquireOrCreate(
            SamplePoolableEntity.class,
            SamplePoolableEntity::new
        );
        reused2.setName("Spider");
        reused2.setHealth(12);
        System.out.println("  Acquired (reused): " + reused2);
        
        // Final statistics showing hit rate
        System.out.println("\nFinal pool statistics: " + pool.getStats());
        System.out.println("Hit rate: " + String.format("%.2f%%", pool.getHitRate() * 100));
        
        // Clear the pool
        System.out.println("\nClearing pool...");
        pool.clear();
        System.out.println("Pool statistics after clear: " + pool.getStats());
    }
}
