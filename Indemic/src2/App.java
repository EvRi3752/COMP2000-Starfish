import javax.swing.JFrame;
import java.awt.Dimension;

public class App {

    public static void main(String[] args) {

        World world = new World();

        JFrame frame = new JFrame("Cell Simulation");

        SimulationPanel panel = new SimulationPanel(world);    
        
        panel.setPreferredSize(new Dimension(1200, 950));
        
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        SimulationLoop loop = new SimulationLoop(world, panel);
        loop.start();
    }
}