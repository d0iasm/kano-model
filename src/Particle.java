
public class Particle {
    private double x, y;
//    private int min = -300;
//    private int max = 300;
    private double min = -0.5;
    private double max = 0.5;
    private int id;

    public Particle(int id) {
        this.id = id;
        x = min + Math.random() * (max - min);
        y = min + Math.random() * (max - min);
    }

    public int getId() {
        return id;
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
