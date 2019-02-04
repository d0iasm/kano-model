package sps_p;

/**
 * Represent a particle which holds x, y positions and id.
 */
public class Particle {
    public double x, y;
    public int id;

    public Particle(int id) {
        this.id = id;
    }

    /**
     * Initialize x, y positions which become between min and max.
     * @param min The minimum value of positions.
     * @param max The maximum value of positions.
     */
    void initPosition(int min, int max) {
        this.x = init(min, max);
        this.y = init(min, max);
    }

    private double init(int min, int max) {
        return min + Math.random() * (max - min);
    }
}
