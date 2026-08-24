import java.util.List; 

public class section {

    private List<Cell> cells;
    private double width; 
    private double height; 

    public section(List<Cell> cells, double width, double height) {
        this.cells = cells;
        this.width = width;
        this.height = height;
    }

    public void tick() {
        if (cells == null) {
            return;
        }

        for (Cell cell : cells) {
            cell.move(width, height);
        }

        for (int firstIndex = 0; firstIndex < cells.size(); firstIndex++) {
            Cell firstCell = cells.get(firstIndex);

            for (int secondIndex = firstIndex + 1; secondIndex < cells.size(); secondIndex++) {
                Cell secondCell = cells.get(secondIndex);

                if (!firstCell.isTouching(secondCell)) {
                    continue;
                }

                if (firstCell.getState() == cellState.INFECTED) {
                    secondCell.infect();
                } else if (secondCell.getState() == cellState.INFECTED) {
                    firstCell.infect();
                }
            }
        }
    }

    public List<Cell> getCells() {
        return cells; 
    }
}