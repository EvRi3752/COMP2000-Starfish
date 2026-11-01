import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Locale;
import javax.swing.JPanel;

public class simulationPanel extends JPanel {
    private world world;

    public simulationPanel(world world) {
        this.world = world;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                handleClick(event.getX(), event.getY());
            }
        });
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
        drawHud(g2d);
        totalEntities = 0;
    }

    private void handleClick(int x, int y) {
        if (!world.hasStarted() && x < 1200 && y < 800) {
            int col = x / 600;
            int row = y / 400;
            world.selectStartingSection(row, col);
        } else if (world.hasStarted() && x < 1200 && y < 800) {
            int sectionCol = x / 600;
            int sectionRow = y / 400;
            int cellWidth = 600 / 5;
            int cellHeight = 400 / 5;
            int cellCol = (x % 600) / cellWidth;
            int cellRow = (y % 400) / cellHeight;
            world.claimDnaPoint(sectionRow, sectionCol, cellRow, cellCol);
        } else if (y >= 850 && y <= 900) {
            if (x >= 10 && x <= 210) {
                world.upgradeInfectivity();
            } else if (x >= 220 && x <= 420) {
                world.upgradeTransmission();
            } else if (x >= 700 && x <= 820) {
                world.togglePaused();
            } else if (x >= 830 && x <= 950) {
                world.reset();
            }
        }
        repaint();
    }

    private void drawHud(Graphics2D g) {
        g.setColor(new Color(250, 250, 250));
        g.fillRect(0, 820, getWidth(), 130);
        g.setColor(Color.BLACK);
        g.drawString("DNA points: " + world.getDnaPoints(), 10, 840);
        g.drawString("Infectivity: " + (int) (world.getInfectionChance() * 100) + "%", 150, 840);
        g.drawString("Section spread threshold: " + world.getInfectionThreshold(), 285, 840);

        drawButton(g, 10, 850, 200, 50, "Upgrade Infectivity");
        drawButton(g, 220, 850, 200, 50, "Upgrade Transmission");
        drawButton(g, 700, 850, 120, 50, world.isPaused() ? "Resume" : "Pause");
        drawButton(g, 830, 850, 120, 50, "Reset");
        g.drawString(world.getStatusMessage(), 450, 880);
        g.setColor(new Color(70, 150, 70));
        g.fillRect(450, 900, 15, 15);
        g.setColor(Color.BLACK);
        g.drawString("Enhanced healthy", 475, 912);
        g.setColor(new Color(120, 180, 255));
        g.fillRect(620, 900, 15, 15);
        g.setColor(Color.BLACK);
        g.drawString("Defender", 645, 912);

        if (!world.hasStarted()) {
            g.setColor(new Color(255, 255, 255, 210));
            g.fillRect(0, 0, getWidth(), 50);
            g.setColor(Color.BLACK);
            g.drawString("Click a section to choose where the infection begins", 15, 30);
        }

    }

    private void drawButton(Graphics2D g, int x, int y, int width, int height, String label) {
        g.setColor(new Color(220, 230, 240));
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
        g.drawString(label, x + 15, y + 30);
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
                int enhancedHealthyCount = gridCell.getEnhancedHealthyCount();
                int defenderCount = gridCell.getDefenderCount();
                boolean reward = gridCell.getEntities().stream()
                        .anyMatch(entity -> entity.getState() == cellState.REWARD);
                int totalCount = gridCell.getEntityCount();

                if (totalCount == 0) {
                    g.setColor(new Color(240, 240, 240)); // Light gray for empty
                } else if (reward) {
                    g.setColor(new Color(255, 220, 80));
                } else if (defenderCount > 0) {
                    g.setColor(new Color(120, 180, 255)); // Blue for defenders
                } else if (enhancedHealthyCount > 0) {
                    g.setColor(new Color(70, 150, 70)); // Dark green for enhanced healthy
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

                if (reward) {
                    g.setColor(Color.BLACK);
                    g.drawString("DNA", x + cellWidth / 2 - 12, y + cellHeight / 2);
                    g.drawString("CLICK", x + cellWidth / 2 - 15, y + cellHeight / 2 + 15);
                }

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
        Map<cellState, Long> stateCounts = section.getEntityStateCounts();
        Map<cellState, Double> statePercentages = section.getEntityStatePercentages();

        g.setColor(Color.BLACK);
        g.drawString(getStateStatistics(cellState.HEALTHY, stateCounts, statePercentages), statsX, statsY);
        g.drawString(getStateStatistics(cellState.INFECTED, stateCounts, statePercentages), statsX, statsY + 15);
        g.drawString(getStateStatistics(cellState.ENHANCED_HEALTHY, stateCounts, statePercentages), statsX, statsY + 30);
        g.drawString(getStateStatistics(cellState.DEFENDER, stateCounts, statePercentages), statsX, statsY + 45);
        g.drawString(getStateStatistics(cellState.DEAD, stateCounts, statePercentages), statsX, statsY + 60);
    }

    private String getStateStatistics(
            cellState state,
            Map<cellState, Long> stateCounts,
            Map<cellState, Double> statePercentages) {
        long stateCount = stateCounts.getOrDefault(state, 0L);
        double percentage = statePercentages.getOrDefault(state, 0.0);
        String label = state.name().replace('_', ' ');
        return String.format(
                Locale.ROOT,
                "%s: %d (%.2f%%)",
                label,
                stateCount,
                percentage
        );
    }
}
