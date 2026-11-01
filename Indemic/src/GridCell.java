import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class GridCell {
    private List<Entity> entities;
    private int gridX;
    private int gridY;

    public GridCell(int gridX, int gridY) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.entities = new ArrayList<>();
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public int getEntityCount() {
        return entities.size();
    }

    public int getHealthyCount() {
        return countEntities(entity -> entity.getState() == cellState.HEALTHY);
    }

    public int getInfectedCount() {
        return countEntities(entity -> entity.getState() == cellState.INFECTED);
    }

    public int getEnhancedHealthyCount() {
        return countEntities(entity -> entity.getState() == cellState.ENHANCED_HEALTHY);
    }

    public int getDefenderCount() {
        return countEntities(entity -> entity.getState() == cellState.DEFENDER);
    }

    private int countEntities(Predicate<Entity> condition) {
        return (int) entities.stream()
                .filter(condition)
                .count();
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }
}
