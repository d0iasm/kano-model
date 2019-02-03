package sps_p;

/**
 *
 */
public class Particle {
    public double x, y;
    public int id;
        private int min = -100;
    private int max = 100;
//    private int min = -1;
//    private int max = 1;
//    private int min = -1;
//    private int max = 1;

    public Particle(int id) {
        this.id = id;
        initPosition();
    }

    public void initPosition() {
        this.x = init();
        this.y = init();
    }

    public double init() {
        return min + Math.random() * (max - min);
    }
}
