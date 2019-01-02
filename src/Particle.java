
public class Particle {
    public double x, y;
    public int id;
//    private int min = -100;
//    private int max = 100;
//    private int min = -1;
//    private int max = 1;
    private int min = 39;
    private int max = 41;

    public Particle(int id) {
        this.id = id;
        x = min + Math.random() * (max - min);
        y = min + Math.random() * (max - min);
    }

    public void printPosition() {
        System.out.println("x: " + x + ", y: " + y);
    }
}
