import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EntityTest {
    @Test
    void healthyEntityCanBecomeInfected() {
        Entity entity = new Entity(1, 2, cellState.HEALTHY);

        entity.infect();

        assertEquals(cellState.INFECTED, entity.getState());
    }

    @Test
    void enhancedHealthyEntityCanBecomeInfected() {
        Entity entity = new Entity(1, 2, cellState.ENHANCED_HEALTHY);

        entity.infect();

        assertEquals(cellState.INFECTED, entity.getState());
    }

    @Test
    void defenderCanBecomeInfected() {
        Entity entity = new Entity(1, 2, cellState.DEFENDER);

        entity.infect();

        assertEquals(cellState.INFECTED, entity.getState());
    }

    @Test
    void infectedEntityCanBeCured() {
        Entity entity = new Entity(1, 2, cellState.INFECTED);

        entity.cure();

        assertEquals(cellState.HEALTHY, entity.getState());
        entity.infect();
        assertEquals(cellState.HEALTHY, entity.getState());
    }

    @Test
    void onlyHealthyEntityCanBeEnhanced() {
        Entity healthy = new Entity(0, 0, cellState.HEALTHY);
        Entity infected = new Entity(1, 1, cellState.INFECTED);

        healthy.enhanceHealthy();
        infected.enhanceHealthy();

        assertEquals(cellState.ENHANCED_HEALTHY, healthy.getState());
        assertEquals(cellState.INFECTED, infected.getState());
    }
}
