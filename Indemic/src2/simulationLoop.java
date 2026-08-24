import javax.swing.Timer;

public class simulationLoop { // stolen from gpt

    private world world;
    private simulationPanel panel;

    public simulationLoop(world world, simulationPanel panel) {
        this.world = world;
        this.panel = panel;
    }

    public void start() { 

        Timer timer = new Timer(30, e -> {

            update();

            panel.repaint();
        });

        timer.start();
    }

    public void update() {

        for (section section : world.getSections()) {

            for (Cell cell : section.getCells()) {
                cell.move(section.getX(), section.getY(), section.getWidth(), section.getHeight());
            }
        }
    }
}