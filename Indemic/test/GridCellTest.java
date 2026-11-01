import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GridCellTest {
    @Test
    void countsEntitiesByState() {
        GridCell cell = new GridCell(2, 3);
        cell.addEntity(new Entity(2, 3, cellState.HEALTHY));
        cell.addEntity(new Entity(2, 3, cellState.INFECTED));
        cell.addEntity(new Entity(2, 3, cellState.DEFENDER));
        cell.addEntity(new Entity(2, 3, cellState.ENHANCED_HEALTHY));

        assertEquals(4, cell.getEntityCount());
        assertEquals(1, cell.getHealthyCount());
        assertEquals(1, cell.getInfectedCount());
        assertEquals(1, cell.getDefenderCount());
        assertEquals(1, cell.getEnhancedHealthyCount());
    }

    @Test
    void removingEntityUpdatesCounts() {
        GridCell cell = new GridCell(0, 0);
        Entity entity = new Entity(0, 0, cellState.INFECTED);
        cell.addEntity(entity);

        cell.removeEntity(entity);

        assertEquals(0, cell.getEntityCount());
        assertEquals(0, cell.getInfectedCount());
    }
}
