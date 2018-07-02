
public class Particle {
    private double x, y;
    private int min = 0;
    private int max = 600;

    public Particle() {
        x = min + Math.random() * (max - min);
        y = min + Math.random() * (max - min);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
