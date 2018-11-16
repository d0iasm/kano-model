
public class Particle {
    public double x, y;
    public int id;
    private int min = -30;
    private int max = 30;
//    private double min = -1;
//    private double max = 1;

    public Particle(int id) {
        this.id = id;
        x = min + Math.random() * (max - min);
        y = min + Math.random() * (max - min);
    }

    public void printPosition() {
        System.out.println("x: " + x + ", y: " + y);
    }
}
