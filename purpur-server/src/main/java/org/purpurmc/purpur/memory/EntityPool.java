package org.purpurmc.purpur.memory;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * High-performance object pool for entities and other frequently-created objects.
 * Reduces garbage collection pressure by reusing objects instead of creating new ones.
 *
 * Thread-safe implementation using per-class pools.
 *
 * @param <T> The type of objects to pool
 */
public class EntityPool<T extends Poolable> {

    private final Object2ObjectOpenHashMap<Class<? extends T>, ObjectArrayList<T>> pools;
    private final int maxPoolSize;
    private final int initialCapacity;

    // Statistics
    private long hits = 0;
    private long misses = 0;
    private long releases = 0;

    /**
     * Create a new entity pool
     * @param maxPoolSize Maximum number of objects to keep in each class pool
     */
    public EntityPool(int maxPoolSize) {
        this(maxPoolSize, 16);
    }

    /**
     * Create a new entity pool
     * @param maxPoolSize Maximum number of objects to keep in each class pool
     * @param initialCapacity Initial capacity hint for each pool
     */
    public EntityPool(int maxPoolSize, int initialCapacity) {
        this.maxPoolSize = maxPoolSize;
        this.initialCapacity = initialCapacity;
        this.pools = new Object2ObjectOpenHashMap<>();
    }

    /**
     * Acquire an object from the pool, or null if none available
     * @param clazz The class of object to acquire
     * @return A pooled object or null
     */
    @Nullable
    public synchronized T acquire(@NotNull Class<? extends T> clazz) {
        ObjectArrayList<T> pool = pools.get(clazz);

        if (pool != null && !pool.isEmpty()) {
            T object = pool.remove(pool.size() - 1);
            hits++;
            return object;
        }

        misses++;
        return null;
    }

    /**
     * Acquire an object from pool or create a new one using the supplier
     * @param clazz The class of object to acquire
     * @param supplier Supplier to create a new object if pool is empty
     * @return A pooled or new object
     */
    @NotNull
    public T acquireOrCreate(@NotNull Class<? extends T> clazz, @NotNull Supplier<T> supplier) {
        T object = acquire(clazz);
        if (object == null) {
            object = supplier.get();
        }
        return object;
    }

    /**
     * Release an object back to the pool
     * @param object The object to release (must be reset/cleaned before releasing)
     */
    public synchronized void release(@NotNull T object) {
        if (object == null) {
            return;
        }

        // Reset the object before pooling
        object.reset();

        @SuppressWarnings("unchecked")
        Class<? extends T> clazz = (Class<? extends T>) object.getClass();

        ObjectArrayList<T> pool = pools.computeIfAbsent(clazz,
            k -> new ObjectArrayList<>(initialCapacity));

        if (pool.size() < maxPoolSize) {
            pool.add(object);
            releases++;
        }
    }

    /**
     * Clear all pools
     */
    public synchronized void clear() {
        pools.values().forEach(ObjectArrayList::clear);
        pools.clear();

        // Reset statistics
        hits = 0;
        misses = 0;
        releases = 0;
    }

    /**
     * Clear a specific class pool
     */
    public synchronized void clear(@NotNull Class<? extends T> clazz) {
        ObjectArrayList<T> pool = pools.get(clazz);
        if (pool != null) {
            pool.clear();
        }
    }

    /**
     * Get the current size of a specific pool
     */
    public synchronized int getPoolSize(@NotNull Class<? extends T> clazz) {
        ObjectArrayList<T> pool = pools.get(clazz);
        return pool != null ? pool.size() : 0;
    }

    /**
     * Get the total number of pooled objects across all classes
     */
    public synchronized int getTotalPooledObjects() {
        return pools.values().stream()
            .mapToInt(ObjectArrayList::size)
            .sum();
    }

    /**
     * Get hit rate (0.0 to 1.0)
     */
    public double getHitRate() {
        long total = hits + misses;
        return total == 0 ? 0.0 : (double) hits / total;
    }

    /**
     * Get pool statistics
     */
    public PoolStats getStats() {
        return new PoolStats(hits, misses, releases, getTotalPooledObjects());
    }

    /**
     * Pool statistics
     */
    public static class PoolStats {
        public final long hits;
        public final long misses;
        public final long releases;
        public final int currentPooled;

        public PoolStats(long hits, long misses, long releases, int currentPooled) {
            this.hits = hits;
            this.misses = misses;
            this.releases = releases;
            this.currentPooled = currentPooled;
        }

        public double getHitRate() {
            long total = hits + misses;
            return total == 0 ? 0.0 : (double) hits / total;
        }

        @Override
        public String toString() {
            return String.format("PoolStats{hits=%d, misses=%d, releases=%d, pooled=%d, hitRate=%.2f%%}",
                hits, misses, releases, currentPooled, getHitRate() * 100);
        }
    }
}
