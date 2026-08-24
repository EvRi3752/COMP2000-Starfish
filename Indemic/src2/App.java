import javax.swing.JFrame;

public class App {

    public static void main(String[] args) {

        world world = new world();

        JFrame frame = new JFrame("Cell Simulation");

        simulationPanel panel = new simulationPanel(world);

        frame.add(panel);

        frame.setSize(1200, 1000);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        simulationLoop loop = new simulationLoop(world, panel);
        loop.start();
    }
}