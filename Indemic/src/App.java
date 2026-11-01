import javax.swing.JFrame;

public class App {

    public static void main(String[] args) {
        try {
            world world = new world(1200, 800);
            AudioManager audioManager = new AudioManager();
            audioManager.startMenuMusic();

            JFrame frame = new JFrame("Indemic - Plague Simulation");

            simulationPanel panel = new simulationPanel(world, audioManager);

            frame.add(panel);

            frame.setSize(1200, 1000);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent event) {
                    audioManager.stop();
                }
            });

            simulationLoop loop = new simulationLoop(world, panel);
            loop.start();
        } catch (java.awt.HeadlessException exception) {
            System.err.println("Indemic requires a graphical environment to run.");
        }
    }
}