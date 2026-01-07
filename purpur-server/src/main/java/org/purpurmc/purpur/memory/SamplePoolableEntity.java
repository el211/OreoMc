package org.purpurmc.purpur.memory;

/**
 * Sample implementation of Poolable interface for demonstration purposes.
 * Represents a simple entity that can be pooled and reused.
 */
public class SamplePoolableEntity implements Poolable {
    
    private String name;
    private int health;
    private double x, y, z;
    
    /**
     * Create a new poolable entity
     */
    public SamplePoolableEntity() {
        reset();
    }
    
    /**
     * Create a new poolable entity with specified values
     */
    public SamplePoolableEntity(String name, int health, double x, double y, double z) {
        this.name = name;
        this.health = health;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public void reset() {
        this.name = null;
        this.health = 0;
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getHealth() {
        return health;
    }
    
    public void setHealth(int health) {
        this.health = health;
    }
    
    public double getX() {
        return x;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public double getY() {
        return y;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    public double getZ() {
        return z;
    }
    
    public void setZ(double z) {
        this.z = z;
    }
    
    @Override
    public String toString() {
        return String.format("SamplePoolableEntity{name='%s', health=%d, pos=(%.1f, %.1f, %.1f)}", 
            name, health, x, y, z);
    }
}
