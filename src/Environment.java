import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Environment implements ActionListener {
    private JFrame frame;
    private Swarm swarm;

    public Environment() {
        this.frame = initFrame(800, 800);
        while (true) {
            this.swarm.run();
        }
    }

    public JFrame getEnvironment() {
        return this.frame;
    }

    private JFrame initFrame(int w, int h) {
        JFrame frame = new JFrame();
        frame.setTitle("SPS-P");
        frame.setSize(new Dimension(w, h));
        frame.setBackground(Color.WHITE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                printSwarmParam();
                super.windowClosing(e);
                System.exit(0);
            }
        });

        this.swarm = initSwarm(6, w, h, 2);
        frame.add(this.swarm, BorderLayout.CENTER);
        // TODO: Keep the screen position to chase the middle of particles.

        frame.setVisible(true);
        return frame;
    }

    private Swarm initSwarm(int num, int w, int h, int type) {
        Swarm swarm = new Swarm(num, w, h, type);
        swarm.setBackground(Color.WHITE);
        swarm.setSize(new Dimension(w, h));
        return swarm;
    }

    private void printSwarmParam() {
        this.swarm.printSwarmParam();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO: Implement action event
    }
}
