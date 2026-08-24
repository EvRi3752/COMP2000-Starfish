import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;

public class SimulationPanel extends JPanel { // stolen off gpt

    private World world;

    public SimulationPanel(World world) {
        this.world = world;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int totalCells = 0;
        int totalHealthy = 0;
        int totalInfected = 0;
        int totalDefender = 0;
        int totalDead = 0;

        super.paintComponent(g);

        g.setColor(Color.BLACK);

        g.drawLine(0, 0, 0, getHeight());
        g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());

        g.drawLine(0, 0, getWidth(), 0);
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

        g.drawLine(0, 400, getWidth(), 400);
        g.drawLine(0, 800, getWidth(), 800);

        g.drawLine(600, 0, 600, 800);

        for (Section Section : world.getSections()) {
            for (Cell cell : Section.getCells()) {
                if (cell.getState() == CellState.HEALTHY) {
                    g.setColor(Color.GREEN);
                } else if (cell.getState() == CellState.INFECTED) {
                    g.setColor(Color.RED);
                } else if (cell.getState() == CellState.DEFENDER) {
                    g.setColor(Color.BLUE);
                }

                g.fillOval((int) cell.getX(), (int) cell.getY(), 15, 15);

                g.setColor(Color.black);
                g.drawOval((int) cell.getX(), (int) cell.getY(), 15, 15);
            }

            int textX = Section.getX() + 10;
            int textY = Section.getY() + 20;

            g.setColor(Color.BLACK);

            g.drawString("Healthy: " + Section.getHealthyCount(), textX, textY);
            g.drawString("Infected: " + Section.getInfectedCount(), textX, textY + 20);
            g.drawString("Defenders: " + Section.getDefenderCount(), textX, textY + 40);
            g.drawString("Dead: " + Section.getDeadCount(), textX, textY + 60);
            g.drawString("Total: " + Section.getCellCount(), textX + 540, textY);

            totalCells += Section.getCellCount();
            totalHealthy += Section.getHealthyCount();
            totalInfected += Section.getInfectedCount();
            totalDefender += Section.getDefenderCount();
            totalDead += Section.getDeadCount();
        }

        g.setColor(Color.BLACK);
        g.drawString("Total Healthy: " + totalHealthy, 10, 820);
        g.drawString("Total Infected: " + totalInfected, 10, 840);
        g.drawString("Total Defender: " + totalDefender, 10, 860);
        g.drawString("Total Dead: " + totalDead, 10, 880);
        g.drawString("Total Cells: " + totalCells, 10, 900);
        g.drawString("//add more cell info and add options to change number of cells, infection rate, toggle to allow defender cells, reviving, etc", 10, 940);
    }
}