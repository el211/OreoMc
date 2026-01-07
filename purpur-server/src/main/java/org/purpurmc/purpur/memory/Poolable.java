package org.purpurmc.purpur.memory;

/**
 * Interface for objects that can be pooled.
 * Objects implementing this interface can be reused via EntityPool.
 */
public interface Poolable {
    /**
     * Reset the object to its initial state before returning to pool.
     * This method should clear all state and make the object ready for reuse.
     */
    void reset();
}
