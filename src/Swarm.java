import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

public class Swarm extends JPanel {
    private int pSize = 10;
    private ArrayList<Particle> particles;

    public Swarm(int num) {
        particles = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            particles.add(new Particle());
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.ORANGE);
        for (Particle p : particles) {
            g.fillOval((int) p.getX(), (int) p.getY(), pSize, pSize);
        }
    }
}
