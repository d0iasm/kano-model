import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Swarm extends JPanel {
    ArrayList<Particle> particles;

    public Swarm(int num) {

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.GREEN);
        g.fillOval(300, 300, 50, 50);
    }
}
