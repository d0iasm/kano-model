
public class Particle {
    private double x, y, dx, dy, r;
    private int min = 0;
    private int max = 600;

    public Particle() {
        x = min + Math.random() * (max - min);
        y = min + Math.random() * (max - min);
        dx = 1;
        dy = 1;
        r = 10;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void move() {
        if (x > 800 || x < 0) dx = -dx;
        if (y > 600 || y < 0) dy = -dy;
        x += dx;
        y += dy;
    }

    private void separation() {

    }

    private void alignment() {

    }

    private void cohesion() {

    }

    public void accelerate(double ax, double ay) {
        // TODO: Implement this method to update speed and direction
        dx += ax;
        dy += ay;
    }
}
