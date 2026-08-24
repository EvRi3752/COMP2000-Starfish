import java.util.ArrayList;
import java.util.Random;

public class Section {

    private int x;
    private int y;

    private int width;
    private int height;

    private ArrayList<Cell> cells;
    private int initial_count;

    private Random random = new Random();

    public Section(int x, int y, int width, int height, int initial_count, int infected_count, int defender_count) {
        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;

        this.initial_count = initial_count;
        cells = new ArrayList<>();

        for (int i = 0; i < initial_count; i++) {
            boolean validPosition = false;

            while (!validPosition) {
                float randomX = x + random.nextFloat() * (width - 20);
                float randomY = y + random.nextFloat() * (height - 20);

                validPosition = true;

                for (Cell cell : cells) {
                    float dx = randomX - cell.getX();
                    float dy = randomY - cell.getY();

                    if (dx * dx + dy * dy < 25 * 25) { // checks if cells are spawning on top of each other
                        validPosition = false;
                        break;
                    }
                }

                if (validPosition) {
                    if (i < infected_count) {
                        cells.add(new Cell(randomX, randomY, CellState.INFECTED));
                    } else if (i < infected_count + defender_count) {
                        cells.add(new Cell(randomX, randomY, CellState.DEFENDER));
                    } else {
                        cells.add(new Cell(randomX, randomY, CellState.HEALTHY));
                    }
                }
            }
        }

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCellCount() {
        return cells.size();
    }

    public int getHealthyCount() {
        int count = 0;

        for (Cell cell : cells) {
            if (cell.getState() == CellState.HEALTHY) {
                count++;
            }
        }

        return count;
    }

    public int getInfectedCount() {
        int count = 0;

        for (Cell cell : cells) {
            if (cell.getState() == CellState.INFECTED) {
                count++;
            }
        }

        return count;
    }

    public int getDefenderCount() {
        int count = 0;

        for (Cell cell : cells) {
            if (cell.getState() == CellState.DEFENDER) {
                count++;
            }
        }

        return count;
    }

    public int getDeadCount() {
        int count = 0;

        for (Cell cell : cells) {
            if (cell.getState() == CellState.DEAD) {
                count++;
            }
        }

        return count;
    }

    public ArrayList<Cell> getCells() {
        return cells;
    }
}