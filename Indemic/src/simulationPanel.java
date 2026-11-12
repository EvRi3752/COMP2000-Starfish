import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Locale;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class simulationPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final transient world world;
    private final transient AudioManager audioManager;

    public simulationPanel(world world) {
        this(world, new AudioManager());
    }

    @SuppressWarnings("this-escape")
    public simulationPanel(world world, AudioManager audioManager) {
        this.world = world;
        this.audioManager = audioManager;
        SwingUtilities.invokeLater(() -> addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                handleClick(event.getX(), event.getY());
            }
        }));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        int totalEntities = 0;

        // Draw grid for each section
        int sectionIndex = 0;
        for (section section : world.getSections()) {
            totalEntities += drawSection(g2d, section, sectionIndex++);
        }

        // Draw statistics
        g2d.setColor(Color.BLACK);
        g2d.drawString("Total Entities: " + totalEntities, 10, getHeight() - 20);
        g2d.drawString("Simulation Grid View - Each cell shows entity count", 10, getHeight() - 5);
        drawHud(g2d);
    }

    private void handleClick(int x, int y) {
        if (!world.hasStarted() && x < 1200 && y < 800) {
            int col = x / 600;
            int row = y / 400;
            if (world.selectStartingSection(row, col)) {
                audioManager.startSimulationMusic();
            }
        } else if (world.hasStarted() && x < 1200 && y < 800) {
            int sectionCol = x / 600;
            int sectionRow = y / 400;
            int cellWidth = 600 / 5;
            int cellHeight = 400 / 5;
            int cellCol = (x % 600) / cellWidth;
            int cellRow = (y % 400) / cellHeight;
            if (world.claimDnaPoint(sectionRow, sectionCol, cellRow, cellCol)) {
                audioManager.playDnaCollected();
            }
        } else if (y >= 850 && y <= 900) {
            if (x >= 10 && x <= 210) {
                world.upgradeInfectivity();
            } else if (x >= 220 && x <= 420) {
                world.upgradeTransmission();
            } else if (!world.hasStarted() && x >= 450 && x <= 570) {
                world.setDifficulty(Difficulty.EASY);
            } else if (!world.hasStarted() && x >= 580 && x <= 700) {
                world.setDifficulty(Difficulty.NORMAL);
            } else if (!world.hasStarted() && x >= 710 && x <= 830) {
                world.setDifficulty(Difficulty.HARD);
            } else if (x >= 700 && x <= 820) {
                world.togglePaused();
                if (world.isPaused()) {
                    audioManager.playPause();
                    audioManager.startMenuMusic();
                } else {
                    audioManager.startSimulationMusic();
                }
            } else if (x >= 830 && x <= 950) {
                world.reset();
                audioManager.playReset();
                audioManager.startMenuMusic();
            }
        }
        repaint();
    }

    private void drawHud(Graphics2D g) {
        g.setColor(new Color(250, 250, 250));
        g.fillRect(0, 820, getWidth(), 180);
        g.setColor(Color.BLACK);
        g.drawString("DNA points: " + world.getDnaPoints(), 10, 840);
        g.drawString("Infectivity: " + (int) (world.getInfectionChance() * 100) + "%", 150, 840);
        g.drawString("Section spread threshold: " + world.getInfectionThreshold(), 285, 840);

        drawButton(g, 10, 850, 200, 50, "Upgrade Infectivity");
        drawButton(g, 220, 850, 200, 50, "Upgrade Transmission");
        if (!world.hasStarted()) {
            drawButton(g, 450, 850, 120, 50, "Easy");
            drawButton(g, 580, 850, 120, 50, "Normal");
            drawButton(g, 710, 850, 120, 50, "Hard");
        } else {
            drawButton(g, 700, 850, 120, 50, world.isPaused() ? "Resume" : "Pause");
            drawButton(g, 830, 850, 120, 50, "Reset");
        }
        g.drawString(world.getStatusMessage(), 10, 920);
        java.util.List<SimulationEvent> recentEvents = world.getRecentEvents();
        if (!recentEvents.isEmpty()) {
            g.setColor(new Color(115, 60, 25));
            g.drawString("Recent: " + recentEvents.get(0).message(), 10, 945);
            g.setColor(Color.BLACK);
        }
        if (world.isGameOver()) {
            g.setColor(new Color(255, 255, 255, 220));
            g.fillRect(300, 300, 600, 110);
            g.setColor(world.getOutcome() == GameOutcome.WON
                    ? new Color(20, 120, 40)
                    : new Color(170, 30, 30));
            g.drawString(world.getOutcome() == GameOutcome.WON ? "YOU WIN!" : "YOU LOSE!", 550, 345);
            g.setColor(Color.BLACK);
            g.drawString("Click Reset to play again.", 500, 375);
        }
        g.setColor(new Color(70, 150, 70));
        g.fillRect(450, 900, 15, 15);
        g.setColor(Color.BLACK);
        g.drawString("Enhanced healthy", 475, 913);
        g.setColor(new Color(120, 180, 255));
        g.fillRect(620, 900, 15, 15);
        g.setColor(Color.BLACK);
        g.drawString("Defender", 645, 913);

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

    private int drawSection(Graphics2D g, section section, int sectionIndex) {
        int totalEntities = 0;
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

        // Highlight a quarantined section and display the remaining ticks.
        boolean quarantined = world.isQuarantined(sectionIndex);
        if (quarantined) {
            g.setColor(new Color(250, 150, 45, 235));
            g.fillRoundRect(sectionX + sectionWidth - 205, sectionY + 8,
                    195, 32, 8, 8);
            g.setColor(Color.BLACK);
            g.drawString("QUARANTINE: " + world.getQuarantineTicksRemaining(sectionIndex) + " ticks", sectionX + sectionWidth - 195, sectionY + 28);
        }
        g.setColor(quarantined ? new Color(220, 115, 20) : Color.BLACK);
        g.setStroke(new BasicStroke(quarantined ? 6 : 4));
        g.drawRect(sectionX, sectionY, sectionWidth, sectionHeight);
        g.setStroke(new BasicStroke(1));

        // Draw statistics inside each section
        int statsX = sectionX + 10;
        int statsY = sectionY + 20;

        Map<cellState, Long> stateCounts = section.getEntityStateCounts();
        Map<cellState, Double> statePercentages = section.getEntityStatePercentages();

        // Draw a semi-transparent background
        g.setColor(new Color(255, 255, 255, 200));
        g.fillRoundRect(sectionX + 5, sectionY + 5, 230, 91, 8, 8);

        // Draw the statistics
        g.setColor(Color.BLACK);
        g.drawString("Section " + (sectionIndex + 1), statsX, statsY);
        g.drawString(getStateStatistics(cellState.HEALTHY, stateCounts, statePercentages), statsX, statsY + 17);
        g.drawString(getStateStatistics(cellState.INFECTED, stateCounts, statePercentages), statsX, statsY + 34);
        g.drawString(getStateStatistics(cellState.DEFENDER, stateCounts, statePercentages), statsX, statsY + 51);
        g.drawString(getStateStatistics(cellState.ENHANCED_HEALTHY, stateCounts, statePercentages), statsX, statsY + 68);

        return totalEntities;
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
