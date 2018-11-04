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
        initFrame(800, 800);
        while (true) {
            swarm.run();
        }
    }

    private void initFrame(int w, int h) {
        frame = new JFrame();
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

        swarm = new Swarm(60, w, h, 3);
        swarm.setBackground(Color.WHITE);
        swarm.setSize(new Dimension(w, h));
        frame.add(swarm, BorderLayout.CENTER);
        // TODO: Keep the screen position to chase the middle of particles.

        frame.setVisible(true);
    }

    private void printSwarmParam() {
        swarm.printSwarmParam();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO: Implement action event
    }
}
