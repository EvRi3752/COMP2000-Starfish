import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class world {
    private final double width;
    private final double height;
    private List<section> sections;
    private section[][] sectionGrid;
    private int infectionThreshold = 10;
    private double infectionChance = 0.3;
    private int dnaPoints;
    private int pendingDnaPoints;
    private int ticks;
    private section startingSection;
    private String statusMessage = "Select a section to start the infection";
    private Random random;
    private boolean paused;
    private Difficulty difficulty = Difficulty.NORMAL;
    private GameOutcome outcome = GameOutcome.IN_PROGRESS;

    public world(double width, double height) {
        this(width, height, new Random());
    }

    public world(double width, double height, long seed) {
        this(width, height, new Random(seed));
    }

    private world(double width, double height, Random random) {
        this.width = width;
        this.height = height;
        this.random = random;
        createSections();
    }

    private void createSections() {
        double sectionWidth = width / 2;
        double sectionHeight = height / 2;

        sections = new ArrayList<>();
        sectionGrid = new section[2][2];
        
        // Create 2x2 grid of sections, each with a 5x5 grid of cells and a sparse starting population so there is still room to move.
        section s00 = new section(0.0, 0.0, sectionWidth, sectionHeight, 5, 5, 15, new Random(random.nextLong()));
        section s01 = new section(sectionWidth, 0.0, sectionWidth, sectionHeight, 5, 5, 15, new Random(random.nextLong()));
        section s10 = new section(0.0, sectionHeight, sectionWidth, sectionHeight, 5, 5, 15, new Random(random.nextLong()));
        section s11 = new section(sectionWidth, sectionHeight, sectionWidth, sectionHeight, 5, 5, 15, new Random(random.nextLong()));
        
        sections.add(s00);
        sections.add(s01);
        sections.add(s10);
        sections.add(s11);
        
        sectionGrid[0][0] = s00;
        sectionGrid[0][1] = s01;
        sectionGrid[1][0] = s10;
        sectionGrid[1][1] = s11;

        for (section currentSection : sections) {
            currentSection.setDifficulty(difficulty);
        }
        
    }

    public void tick() {
        if (startingSection == null || paused || outcome != GameOutcome.IN_PROGRESS) {
            return;
        }

        ticks++;
        expireDnaRewards();
        if (ticks % 4 == 0) {
            if (startingSection.addDnaReward()) {
                pendingDnaPoints++;
                statusMessage = "DNA point appeared in the grid";
            }
        }

        // Tick all sections
        for (section currentSection : sections) {
            currentSection.tick();
        }
        
        // Check for inter-section infection spread
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                section currentSection = sectionGrid[row][col];
                
                int infectedCount = currentSection.getTotalInfectedCount();
                int totalCount = currentSection.getTotalEntityCount();
                boolean sectionIsFullyInfected = totalCount > 0 && infectedCount == totalCount;

                if (infectedCount >= infectionThreshold || sectionIsFullyInfected) {
                    // Spread to adjacent sections
                    spreadToAdjacentSection(row, col, 0, -1); // Up
                    spreadToAdjacentSection(row, col, 0, 1);  // Down
                    spreadToAdjacentSection(row, col, -1, 0); // Left
                    spreadToAdjacentSection(row, col, 1, 0);  // Right
                }
            }
        }

        updateGameOutcome();
    }

    private void updateGameOutcome() {
        int infectedEntities = 0;
        int susceptibleEntities = 0;

        for (section currentSection : sections) {
            infectedEntities += currentSection.getTotalInfectedCount();
            susceptibleEntities += currentSection.getTotalHealthyCount()
                    + currentSection.getTotalEnhancedHealthyCount()
                    + currentSection.getTotalDefenderCount();
        }

        if (susceptibleEntities == 0) {
            outcome = GameOutcome.WON;
            paused = true;
            statusMessage = "You win! No healthy entities remain.";
        } else if (infectedEntities == 0) {
            outcome = GameOutcome.LOST;
            paused = true;
            statusMessage = "You lose! No infected entities remain.";
        }
    }

    private void expireDnaRewards() {
        int expiredRewards = 0;
        long currentTimeMillis = System.currentTimeMillis();

        for (section currentSection : sections) {
            expiredRewards += currentSection.removeExpiredDnaRewards(currentTimeMillis);
        }

        pendingDnaPoints = Math.max(0, pendingDnaPoints - expiredRewards);
        if (expiredRewards > 0) {
            statusMessage = "A DNA point expired";
        }
    }

    private void spreadToAdjacentSection(int currentRow, int currentCol, int dRow, int dCol) {
        int newRow = currentRow + dRow;
        int newCol = currentCol + dCol;
        
        // Boundary check
        if (newRow >= 0 && newRow < 2 && newCol >= 0 && newCol < 2) {
            section adjacentSection = sectionGrid[newRow][newCol];
            
            // Only spread if the adjacent section still has room to accept new infected entities.
            if (adjacentSection.getTotalEntityCount() < 20) {
                // Add 2-3 infected entities to the adjacent section
                int infectedToAdd = random.nextInt(2) + 2; // 2-3 entities
                adjacentSection.spreadInfectionToEmptyCells(infectedToAdd);
            }
        }
    }

    public List<section> getSections() {
        return sections;
    }

    public boolean selectStartingSection(int row, int col) {
        if (startingSection != null || row < 0 || row >= 2 || col < 0 || col >= 2) {
            return false;
        }

        startingSection = sectionGrid[row][col];
        startingSection.setInitialInfection();
        statusMessage = "Infection started in section " + (row * 2 + col + 1);
        return true;
    }

    public boolean setDifficulty(Difficulty difficulty) {
        if (difficulty == null || startingSection != null) {
            return false;
        }

        this.difficulty = difficulty;
        for (section currentSection : sections) {
            currentSection.setDifficulty(difficulty);
        }
        statusMessage = difficulty.getLabel() + " difficulty selected";
        return true;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void togglePaused() {
        paused = !paused;
        statusMessage = paused ? "Simulation paused" : "Simulation resumed";
    }

    public boolean isPaused() {
        return paused;
    }

    public GameOutcome getOutcome() {
        return outcome;
    }

    public boolean isGameOver() {
        return outcome != GameOutcome.IN_PROGRESS;
    }

    public void reset() {
        random = new Random(random.nextLong());
        infectionThreshold = 10;
        infectionChance = 0.3;
        dnaPoints = 0;
        pendingDnaPoints = 0;
        ticks = 0;
        startingSection = null;
        paused = false;
        outcome = GameOutcome.IN_PROGRESS;
        statusMessage = "Select a section to start the infection";
        createSections();
    }

    public boolean upgradeInfectivity() {
        if (dnaPoints < 1) {
            statusMessage = "You need a DNA point for this upgrade";
            return false;
        }

        dnaPoints--;
        infectionChance = Math.min(0.8, infectionChance + 0.1);
        for (section currentSection : sections) {
            currentSection.setInfectionChance(infectionChance);
        }
        statusMessage = "Infectivity upgraded to " + (int) (infectionChance * 100) + "%";
        return true;
    }

    public boolean upgradeTransmission() {
        if (dnaPoints < 1) {
            statusMessage = "You need a DNA point for this upgrade";
            return false;
        }

        dnaPoints--;
        infectionThreshold = Math.max(3, infectionThreshold - 2);
        statusMessage = "Transmission threshold reduced to " + infectionThreshold;
        return true;
    }

    public int getDnaPoints() {
        return dnaPoints;
    }

    public int getPendingDnaPoints() {
        return pendingDnaPoints;
    }

    public boolean hasPendingDnaPoint() {
        return pendingDnaPoints > 0;
    }

    public boolean claimDnaPoint(int row, int col) {
        if (startingSection == null || !startingSection.claimDnaReward(row, col)) {
            return false;
        }

        pendingDnaPoints--;
        dnaPoints++;
        statusMessage = "DNA point collected - choose an upgrade";
        return true;
    }

    public boolean claimDnaPoint(int sectionRow, int sectionCol, int cellRow, int cellCol) {
        if (sectionRow < 0 || sectionRow >= sectionGrid.length
                || sectionCol < 0 || sectionCol >= sectionGrid[sectionRow].length) {
            return false;
        }

        section selectedSection = sectionGrid[sectionRow][sectionCol];
        if (!selectedSection.claimDnaReward(cellRow, cellCol)) {
            return false;
        }

        pendingDnaPoints--;
        dnaPoints++;
        statusMessage = "DNA point collected - choose an upgrade";
        return true;
    }

    public double getInfectionChance() {
        return infectionChance;
    }

    public int getInfectionThreshold() {
        return infectionThreshold;
    }

    public boolean hasStarted() {
        return startingSection != null;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}