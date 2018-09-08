
public class Particle {
    public double x, y;
    public int id;
//    private int min = -300;
//    private int max = 300;
    private double min = -0.5;
    private double max = 0.5;

    public Particle(int id) {
        this.id = id;
        x = min + Math.random() * (max - min);
        y = min + Math.random() * (max - min);
    }
}
