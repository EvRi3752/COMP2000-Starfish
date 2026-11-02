import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import org.junit.jupiter.api.Test;

class SectionTest {
    @Test
    void rejectsInvalidDimensionsAndPopulation() {
        assertThrows(IllegalArgumentException.class,
                () -> new section(0, 0, 0, 10, 2, 2, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new section(0, 0, 10, 10, 2, 2, 5));
    }

    @Test
    void tickPreservesEntityCountInBothInfectionModes() {
        section sequential = createSection();
        section parallel = createSection();
        parallel.setInfectionStrategy(new ParallelInfectionStrategy());

        for (int i = 0; i < 10; i++) {
            sequential.tick();
            parallel.tick();
        }

        assertEquals(13, sequential.getTotalEntityCount());
        assertEquals(13, parallel.getTotalEntityCount());
    }

    @Test
    void infectionChanceCanBeConfigured() {
        section simulation = createSection();

        simulation.setInfectionChance(0.75);

        assertEquals(0.75, simulation.getInfectionChance());
    }

    @Test
    void infectionStrategyCanBeChanged() {
        section simulation = createSection();

        simulation.setInfectionStrategy(new ParallelInfectionStrategy());
        simulation.spreadInfection();

        assertEquals(13, simulation.getTotalEntityCount());
    }

    @Test
    void nullInfectionStrategyIsRejected() {
        section simulation = createSection();

        assertThrows(IllegalArgumentException.class,
                () -> simulation.setInfectionStrategy(null));
    }

    @Test
    void enhancedHealthyDevelopmentUsesSlowerCadence() {
        section simulation = createSection();
        simulation.setDifficulty(Difficulty.NORMAL);
        simulation.setTicksForTesting(1);
        simulation.tick();

        assertEquals(0, simulation.getTotalEnhancedHealthyCount());
    }

    private static section createSection() {
        section simulation = new section(
                0, 0, 100, 100, 5, 5, 12, new Random(42));
        simulation.setInitialInfection();
        return simulation;
    }
}
