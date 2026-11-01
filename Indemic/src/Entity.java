public class Entity {
    public static final long DNA_REWARD_LIFETIME_MILLIS = 60_000L;
    private cellState state;
    private int gridX; // Grid position
    private int gridY; // Grid position
    private long expiresAt;

    public Entity(int gridX, int gridY, cellState state) {
        this(gridX, gridY, state, 0L);
    }

    public Entity(int gridX, int gridY, cellState state, long expiresAt) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.state = state;
        this.expiresAt = expiresAt;
    }

    public void infect() {
        if (state == cellState.HEALTHY) {
            state = cellState.INFECTED;
        }
    }

    public void enhanceHealthy() {
        if (state == cellState.HEALTHY) {
            state = cellState.ENHANCED_HEALTHY;
        }
    }

    public void setState(cellState state) {
        this.state = state;
    }

    public void cure() {
        if (state == cellState.INFECTED) {
            state = cellState.HEALTHY;
        }
    }

    public cellState getState() {
        return state;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public void setGridX(int gridX) {
        this.gridX = gridX;
    }

    public void setGridY(int gridY) {
        this.gridY = gridY;
    }

    public boolean isExpired(long currentTimeMillis) {
        return expiresAt > 0 && currentTimeMillis >= expiresAt;
    }
}
