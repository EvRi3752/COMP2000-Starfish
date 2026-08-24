import javax.swing.Timer;

public class SimulationLoop { // stolen from gpt

    private World world;
    private SimulationPanel panel;

    public SimulationLoop(World world, SimulationPanel panel) {
        this.world = world;
        this.panel = panel;
    }

    public void start() { 

        Timer timer = new Timer(15, e -> {

            update();

            panel.repaint();
        });

        timer.start();
    }

    public void update() {

        for (Section Section : world.getSections()) {

            for (Cell cell : Section.getCells()) {
                cell.move(Section.getX(), Section.getY(), Section.getWidth(), Section.getHeight());
            }

            for (int i = 0; i < Section.getCells().size(); i++) {
                
                for (int j = i + 1; j < Section.getCells().size(); j++) {
                
                    Cell cell1 = Section.getCells().get(i);
                    Cell cell2 = Section.getCells().get(j);

                    if (cell1.isColliding(cell2)) { // collision check, works but cells bouncing off each other is still kinda buggy
                        cell1.separate(cell2);

                        cell1.collide(cell2);
                        cell2.collide(cell1);

                        if (cell1.getState() == CellState.INFECTED && cell2.getState() == CellState.HEALTHY) {
                            cell2.getInfected();
                        } else if (cell2.getState() == CellState.INFECTED && cell1.getState() == CellState.HEALTHY) {
                            cell1.getInfected();
                        }

                        else if (cell1.getState() == CellState.DEFENDER && cell2.getState() == CellState.INFECTED) {
                            cell2.getCured();
                        } else if (cell2.getState() == CellState.DEFENDER && cell1.getState() == CellState.INFECTED) {
                            cell1.getCured();
                        }
                    }
                }
            }
        }
    }
}