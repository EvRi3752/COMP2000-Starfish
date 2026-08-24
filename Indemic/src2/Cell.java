import java.util.Random;

public class Cell {

    private int x;
    private int y;
    private cellState state;

    private int dx;
    private int dy;

    private Random random = new Random();

    public Cell(int x, int y, cellState state) {
        this.x = x;
        this.y = y;
        this.state = state;

        dx = random.nextBoolean() ? 2 : -2;
        dy = random.nextBoolean() ? 2 : -2;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public cellState getState() {
        return state;
    }

    public void move(int sectionX, int sectionY, int width, int height) {

        x += dx;
        y += dy;

        if (x <= sectionX || x >= sectionX + width - 20) {
            dx = -dx;
        }

        if (y <= sectionY || y >= sectionY + height - 20) {
            dy = -dy;
        }
    }
}