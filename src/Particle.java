
public class Particle {
    private double x, y, dx, dy;
    private int min = 0;
    private int max = 600;

    public Particle() {
        x = min + Math.random() * (max - min);
        y = min + Math.random() * (max - min);
        dx = 1;
        dy = 1;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public void move() {
        if (x > 800 || x < 0) dx = -dx;
        if (y > 600 || y < 0) dy = -dy;
        x += dx;
        y += dy;
    }

    public void straying() {
        x += Math.random() - 0.5;
        y += Math.random() - 0.5;
    }

    private void separation() {

    }

    private void alignment() {

    }

    private void cohesion() {

    }

    public void accelerate(double ax, double ay, double maxSpeed) {
        dx += ax;
        dy += ay;

        double d = dx * dx + dy * dy;
        if (d > maxSpeed * maxSpeed) {
            double normalizationFactor = maxSpeed / Math.sqrt(d);
            dx *= normalizationFactor;
            dy *= normalizationFactor;
        }
    }
}
