import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Environment extends JFrame implements ActionListener {
    private Swarm panel;

    public Environment() {
        int w = 800;
        int h = 600;
        initFrame(w, h);
//        while (true) {
//        }
    }

    private void initFrame(int w, int h) {
        setTitle("Swarm Chemistry Simulator");
        setSize(new Dimension(w, h));
        setBackground(Color.white);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });

        panel = new Swarm(10);
        panel.setBackground(Color.YELLOW);
        Container container = getContentPane();
        container.add(panel, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO: Implement action event
    }
}
