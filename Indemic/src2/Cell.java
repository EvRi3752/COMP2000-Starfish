import java.util.Random;

public class Cell {

    private float x;
    private float y;
    private CellState state;

    private float dx;
    private float dy;

    private Random random = new Random();

    public Cell(float x, float y, CellState state) {
        this.x = x;
        this.y = y;
        this.state = state;

        dx = -2 + random.nextFloat() * 4;
        dy = -2 + random.nextFloat() * 4;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    public void move(int sectionX, int sectionY, int width, int height) {
        x += dx;
        y += dy;

        if (x <= sectionX) {
            x = sectionX;
            dx = -dx;
        }

        if (x >= sectionX + width - 15) {
            x = sectionX + width - 15;
            dx = -dx;
        }

        if (y <= sectionY) {
            y = sectionY;
            dy = -dy;
        }

        if (y >= sectionY + height - 15) {
            y = sectionY + height - 15;
            dy = -dy;
        }
    }

    public boolean isColliding(Cell other) {
        float dxDistance = x - other.x;
        float dyDistance = y - other.y;

        return dxDistance * dxDistance + dyDistance * dyDistance < 15 * 15;
    }

    public void collide(Cell other) {
        float dxDistance = other.x - x;
        float dyDistance = other.y - y;

        if (Math.abs(dxDistance) > Math.abs(dyDistance)) { // ai
            dx = -dx;
        } else {
            dy = -dy;
        }
    }

    public void separate(Cell other) { // moves cells slightly appart after collision so they dont repeatedly collide, ai code
        if (x < other.x) {
            x--;
            other.x++;
        } else {
            x++;
            other.x--;
        }

        if (y < other.y) {
            y--;
            other.y++;
        } else {
            y++;
            other.y--;
        }
    }

    public void getInfected() {
        setState(CellState.INFECTED);
    }

    public void getCured() {
        setState(CellState.HEALTHY);
    }

    public void death() { // not sure what logic to implement for cells dying yet, if cells stay infected for x seconds they die?
        setState(CellState.DEAD);
    }
}