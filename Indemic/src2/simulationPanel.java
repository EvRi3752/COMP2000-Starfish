import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;

public class simulationPanel extends JPanel { //stolen off gpt

    private world world;

    public simulationPanel(world world) {
        this.world = world;
    }

    int totalCells;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);

        g.drawLine(0, 0, 0, getHeight());
        g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());

        g.drawLine(0, 0, getWidth(), 0);
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

        g.drawLine(0, 400, getWidth(), 400);
        g.drawLine(0, 800, getWidth(), 800);

        g.drawLine(600, 0, 600, 800);


        for (section section : world.getSections()) {

            for (Cell cell : section.getCells()) {

                if (cell.getState() == cellState.HEALTHY) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.RED);
                }

                g.fillOval(cell.getX(), cell.getY(), 15, 15);
            }

            int textX = section.getX() + 10;
            int textY = section.getY() + 20;

            g.setColor(Color.BLACK);

            g.drawString("Healthy: " + section.getCellCount(), textX, textY);
            g.drawString("Dead: " + "0", textX, textY + 20);
            g.drawString("Defenders: " + "0", textX, textY + 40);

            totalCells += section.getCellCount();
        }

        g.setColor(Color.BLACK);
        g.drawString("Total Cells: " + totalCells, 10, 820);
        totalCells = 0;
    }
}