import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class world {
    private List<section> sections;
    private section[][] sectionGrid;
    private static final int INFECTION_THRESHOLD = 10;
    private Random random = new Random();

    public world(double width, double height) {
        double sectionWidth = width / 2;
        double sectionHeight = height / 2;

        sections = new ArrayList<>();
        sectionGrid = new section[2][2];
        
        // Create 2x2 grid of sections, each with a 5x5 grid of cells and a sparse starting population so there is still room to move.
        section s00 = new section(0.0, 0.0, sectionWidth, sectionHeight, 5, 5, 15);
        section s01 = new section(sectionWidth, 0.0, sectionWidth, sectionHeight, 5, 5, 15);
        section s10 = new section(0.0, sectionHeight, sectionWidth, sectionHeight, 5, 5, 15);
        section s11 = new section(sectionWidth, sectionHeight, sectionWidth, sectionHeight, 5, 5, 15);
        
        sections.add(s00);
        sections.add(s01);
        sections.add(s10);
        sections.add(s11);
        
        sectionGrid[0][0] = s00;
        sectionGrid[0][1] = s01;
        sectionGrid[1][0] = s10;
        sectionGrid[1][1] = s11;
        
        // Start infection in one random section
        sections.get(random.nextInt(sections.size())).setInitialInfection();
    }

    public void tick() {
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

                if (infectedCount >= INFECTION_THRESHOLD || sectionIsFullyInfected) {
                    // Spread to adjacent sections
                    spreadToAdjacentSection(row, col, 0, -1); // Up
                    spreadToAdjacentSection(row, col, 0, 1);  // Down
                    spreadToAdjacentSection(row, col, -1, 0); // Left
                    spreadToAdjacentSection(row, col, 1, 0);  // Right
                }
            }
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
}