import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WorldTest {
    @Test
    void gameStartsInProgressAndResetClearsOutcome() {
        world simulation = new world(1200, 800, 42L);

        assertEquals(GameOutcome.IN_PROGRESS, simulation.getOutcome());
        assertFalse(simulation.isGameOver());

        simulation.reset();

        assertEquals(GameOutcome.IN_PROGRESS, simulation.getOutcome());
    }

    @Test
    void difficultyControlsSectionSpecialEntities() {
        world simulation = new world(1200, 800, 42L);

        assertTrue(simulation.setDifficulty(Difficulty.EASY));
        assertEquals(Difficulty.EASY, simulation.getDifficulty());
        assertTrue(simulation.selectStartingSection(0, 0));
        for (int tick = 0; tick < 12; tick++) {
            simulation.tick();
        }

        for (section currentSection : simulation.getSections()) {
            assertEquals(0, currentSection.getTotalEnhancedHealthyCount());
            assertEquals(0, currentSection.getTotalDefenderCount());
        }
    }

    @Test
    void difficultyCannotChangeAfterStarting() {
        world simulation = new world(1200, 800, 42L);
        assertTrue(simulation.selectStartingSection(0, 0));

        assertFalse(simulation.setDifficulty(Difficulty.HARD));
        assertEquals(Difficulty.NORMAL, simulation.getDifficulty());
    }

    @Test
    void rewardAppearsAndMustBeClaimedFromItsCell() {
        world simulation = new world(1200, 800, 42L);
        assertTrue(simulation.selectStartingSection(0, 0));

        for (int tick = 0; tick < 4; tick++) {
            simulation.tick();
        }

        section startingSection = simulation.getSections().get(0);
        int rewardRow = -1;
        int rewardColumn = -1;
        for (int row = 0; row < startingSection.getGridRows(); row++) {
            for (int col = 0; col < startingSection.getGridCols(); col++) {
                if (startingSection.isDnaReward(row, col)) {
                    rewardRow = row;
                    rewardColumn = col;
                }
            }
        }

        assertTrue(rewardRow >= 0);
        assertEquals(0, simulation.getDnaPoints());
        assertTrue(simulation.claimDnaPoint(0, 0, rewardRow, rewardColumn));
        assertEquals(1, simulation.getDnaPoints());
        assertFalse(simulation.hasPendingDnaPoint());
    }

    @Test
    void expiredRewardCannotBeClaimed() {
        world simulation = new world(1200, 800, 42L);
        simulation.selectStartingSection(0, 0);
        for (int tick = 0; tick < 4; tick++) {
            simulation.tick();
        }

        section startingSection = simulation.getSections().get(0);
        int rewardRow = -1;
        int rewardColumn = -1;
        for (int row = 0; row < startingSection.getGridRows(); row++) {
            for (int col = 0; col < startingSection.getGridCols(); col++) {
                if (startingSection.isDnaReward(row, col)) {
                    rewardRow = row;
                    rewardColumn = col;
                }
            }
        }

        startingSection.removeExpiredDnaRewards(
                System.currentTimeMillis() + Entity.DNA_REWARD_LIFETIME_MILLIS + 1);

        assertFalse(startingSection.isDnaReward(rewardRow, rewardColumn));
        assertFalse(simulation.claimDnaPoint(0, 0, rewardRow, rewardColumn));
    }

    @Test
    void upgradesRequireClaimedDnaPoints() {
        world simulation = new world(1200, 800, 42L);
        simulation.selectStartingSection(0, 0);

        assertFalse(simulation.upgradeInfectivity());
        assertFalse(simulation.upgradeTransmission());
    }

    @Test
    void pausePreventsTicksUntilResumed() {
        world simulation = new world(1200, 800, 42L);
        simulation.selectStartingSection(0, 0);
        simulation.togglePaused();

        for (int tick = 0; tick < 4; tick++) {
            simulation.tick();
        }

        assertTrue(simulation.isPaused());
        assertFalse(simulation.hasPendingDnaPoint());
        simulation.togglePaused();
        simulation.tick();
        assertFalse(simulation.hasPendingDnaPoint());
    }

    @Test
    void resetRestoresInitialGameState() {
        world simulation = new world(1200, 800, 42L);
        simulation.selectStartingSection(0, 0);
        simulation.togglePaused();

        simulation.reset();

        assertFalse(simulation.hasStarted());
        assertFalse(simulation.isPaused());
        assertEquals(0, simulation.getDnaPoints());
        assertEquals(0.3, simulation.getInfectionChance());
        assertEquals(10, simulation.getInfectionThreshold());
    }
}
