import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class simulationPanel extends JPanel {
    private world world;

    public simulationPanel(world world) {
        this.world = world;
    }

    int totalEntities;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;

        // Draw grid for each section
        for (section section : world.getSections()) {
            drawSection(g2d, section);
        }

        // Draw statistics
        g2d.setColor(Color.BLACK);
        g2d.drawString("Total Entities: " + totalEntities, 10, getHeight() - 20);
        g2d.drawString("Simulation Grid View - Each cell shows entity count", 10, getHeight() - 5);
        totalEntities = 0;
    }

    private void drawSection(Graphics2D g, section section) {
        int sectionX = (int) section.getX();
        int sectionY = (int) section.getY();
        int sectionWidth = (int) section.getWidth();
        int sectionHeight = (int) section.getHeight();

        int gridRows = section.getGridRows();
        int gridCols = section.getGridCols();

        int cellWidth = sectionWidth / gridCols;
        int cellHeight = sectionHeight / gridRows;

        // Draw grid cells
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                GridCell gridCell = section.getGridCell(row, col);
                int x = sectionX + col * cellWidth;
                int y = sectionY + row * cellHeight;

                // Draw cell background based on infection status
                int healthyCount = gridCell.getHealthyCount();
                int infectedCount = gridCell.getInfectedCount();
                int totalCount = gridCell.getEntityCount();

                if (totalCount == 0) {
                    g.setColor(new Color(240, 240, 240)); // Light gray for empty
                } else if (infectedCount > healthyCount) {
                    g.setColor(new Color(255, 200, 200)); // Light red for infected majority
                } else if (healthyCount > 0) {
                    g.setColor(new Color(200, 255, 200)); // Light green for healthy majority
                }

                g.fillRect(x, y, cellWidth, cellHeight);

                // Draw cell border (thin)
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(1));
                g.drawRect(x, y, cellWidth, cellHeight);

                // Track entity count (no text display since each cell has exactly 1 entity)
                if (totalCount > 0) {
                    totalEntities += totalCount;
                }
            }
        }

        // Draw thick section border
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(4)); // Thick border for section
        g.drawRect(sectionX, sectionY, sectionWidth, sectionHeight);

        // Draw section statistics
        int statsX = sectionX + 5;
        int statsY = sectionY + sectionHeight + 15;

        g.setColor(Color.BLACK);
        g.drawString("Healthy: " + section.getTotalHealthyCount(), statsX, statsY);
        g.drawString("Infected: " + section.getTotalInfectedCount(), statsX, statsY + 15);
    }
}
