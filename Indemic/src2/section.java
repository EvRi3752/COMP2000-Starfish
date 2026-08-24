import java.util.ArrayList;
import java.util.Random;

public class section {

    private int x;
    private int y;

    private int width;
    private int height;

    private ArrayList<Cell> cells;
    private int cell_count;

    private Random random = new Random();

    public section(int x, int y, int width, int height, int cell_count) {

        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;

        this.cell_count = cell_count;

        cells = new ArrayList<>();

        for (int i = 0; i < cell_count; i++) {

            int randomX = x + random.nextInt(width - 20);
            int randomY = y + random.nextInt(height - 20);

            cells.add(new Cell(randomX, randomY, cellState.HEALTHY));
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
        return cell_count;
    }

    public ArrayList<Cell> getCells() {
        return cells;
    }
}