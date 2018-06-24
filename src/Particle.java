
public class Particle {
    private float x;
    private float y;
    private int min = 0;
    private int max = 600;

    public Particle() {
        x = (float) (min + Math.random() * (max - min));
        y = (float) (min + Math.random() * (max - min));
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
