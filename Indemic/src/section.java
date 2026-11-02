import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class section {
    private static final double ENHANCED_HEALTHY_INFECTION_MULTIPLIER = 0.1;
    private GridCell[][] grid;
    private double x;
    private double y;
    private double width;
    private double height;
    private int gridRows;
    private int gridCols;
    private double infectionChance = 0.3;
    private int ticks;
    private InfectionStrategy infectionStrategy = new SequentialInfectionStrategy();
    private final Random random;
    private Difficulty difficulty = Difficulty.NORMAL;

    public section(double x, double y, double width, double height, int gridRows, int gridCols, int initialEntityCount) {
        this(x, y, width, height, gridRows, gridCols, initialEntityCount, new Random());
    }

    public section(double x, double y, double width, double height, int gridRows, int gridCols, int initialEntityCount, Random random) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Section dimensions must be positive");
        }
        if (gridRows <= 0 || gridCols <= 0) {
            throw new IllegalArgumentException("Grid dimensions must be positive");
        }
        if (initialEntityCount < 0 || initialEntityCount > gridRows * gridCols) {
            throw new IllegalArgumentException("Initial entity count must fit within the grid");
        }

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.gridRows = gridRows;
        this.gridCols = gridCols;
        this.random = random;

        // Initialize grid
        grid = new GridCell[gridRows][gridCols];
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                grid[row][col] = new GridCell(col, row);
            }
        }

        // Populate with initial entities (leave some cells empty for movement)
        int entitiesAdded = 0;
        while (entitiesAdded < initialEntityCount) {
            int randomRow = random.nextInt(gridRows);
            int randomCol = random.nextInt(gridCols);
            
            if (grid[randomRow][randomCol].getEntityCount() == 0) {
                Entity entity = new Entity(randomCol, randomRow, cellState.HEALTHY);
                grid[randomRow][randomCol].addEntity(entity);
                entitiesAdded++;
            }
        }
    }

    public void setInitialInfection() {
        // Always ensure at least one infected entity exists on startup.
        // Prefer an isolated cell, but always fall back to a valid random cell if needed.
        for (int attempts = 0; attempts < 100; attempts++) {
            int row = random.nextInt(gridRows);
            int col = random.nextInt(gridCols);

            boolean adjacentToHealthy = false;
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;
                    int adjRow = row + dr;
                    int adjCol = col + dc;
                    if (adjRow >= 0 && adjRow < gridRows && adjCol >= 0 && adjCol < gridCols) {
                        if (grid[adjRow][adjCol].getEntityCount() > 0 &&
                            grid[adjRow][adjCol].getEntities().get(0).getState() == cellState.HEALTHY) {
                            adjacentToHealthy = true;
                        }
                    }
                }
            }

            if (!adjacentToHealthy) {
                if (grid[row][col].getEntityCount() == 0) {
                    grid[row][col].addEntity(new Entity(col, row, cellState.INFECTED));
                } else {
                    grid[row][col].getEntities().get(0).infect();
                }
                return;
            }
        }

        // Last-resort guarantee: infect a random cell even if it is adjacent to healthy entities.
        int fallbackRow = random.nextInt(gridRows);
        int fallbackCol = random.nextInt(gridCols);
        if (grid[fallbackRow][fallbackCol].getEntityCount() == 0) {
            grid[fallbackRow][fallbackCol].addEntity(new Entity(fallbackCol, fallbackRow, cellState.INFECTED));
        } else {
            grid[fallbackRow][fallbackCol].getEntities().get(0).infect();
        }
    }

    public void spreadInfectionToEmptyCells(int infectedCellsToAdd) {
        if (infectedCellsToAdd < 0) {
            throw new IllegalArgumentException("Number of infected cells to add cannot be negative");
        }

        // Spread infection to empty cells when threshold is met
        for (int i = 0; i < infectedCellsToAdd; i++) {
            List<Position> emptyCells = new ArrayList<>();
            
            // Find all empty cells
            for (int row = 0; row < gridRows; row++) {
                for (int col = 0; col < gridCols; col++) {
                    if (grid[row][col].getEntityCount() == 0) {
                        emptyCells.add(new Position(row, col));
                    }
                }
            }
            
            if (emptyCells.isEmpty()) break;
            
            // Add infected entity to random empty cell
            Position cell = emptyCells.get(random.nextInt(emptyCells.size()));
            Entity entity = new Entity(cell.getColumn(), cell.getRow(), cellState.INFECTED);
            grid[cell.getRow()][cell.getColumn()].addEntity(entity);
        }
    }

    boolean addDnaReward() {
        List<Position> emptyCells = new ArrayList<>();

        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                if (grid[row][col].getEntityCount() == 0) {
                    emptyCells.add(new Position(row, col));
                }
            }
        }

        if (emptyCells.isEmpty()) {
            return false;
        }

        Position cell = emptyCells.get(random.nextInt(emptyCells.size()));
        grid[cell.getRow()][cell.getColumn()].addEntity(
                new Entity(
                        cell.getColumn(),
                        cell.getRow(),
                        cellState.REWARD,
                        System.currentTimeMillis() + Entity.DNA_REWARD_LIFETIME_MILLIS));
        return true;
    }

    int removeExpiredDnaRewards(long currentTimeMillis) {
        int removed = 0;

        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                List<Entity> entities = grid[row][col].getEntities();
                for (int index = entities.size() - 1; index >= 0; index--) {
                    Entity entity = entities.get(index);
                    if (entity.getState() == cellState.REWARD
                            && entity.isExpired(currentTimeMillis)) {
                        entities.remove(index);
                        removed++;
                    }
                }
            }
        }

        return removed;
    }

    boolean isDnaReward(int row, int col) {
        return getGridCell(row, col).getEntities().stream()
                .anyMatch(entity -> entity.getState() == cellState.REWARD);
    }

    boolean claimDnaReward(int row, int col) {
        GridCell cell = getGridCell(row, col);
        Entity reward = cell.getEntities().stream()
                .filter(entity -> entity.getState() == cellState.REWARD
                        && !entity.isExpired(System.currentTimeMillis()))
                .findFirst()
                .orElse(null);

        if (reward == null) {
            return false;
        }

        cell.removeEntity(reward);
        return true;
    }

    public void tick() {
        ticks++;
        moveEntities();

        if (difficulty.isEnhancedHealthyEnabled()) {
            developEnhancedHealthy();
        }
        if (difficulty.areDefendersEnabled()) {
            developDefenders();
        }
        spreadInfection();
        cureNearbyInfections();
    }

    void setTicksForTesting(int ticks) {
        if (ticks < 0) {
            throw new IllegalArgumentException("Tick count cannot be negative");
        }
        this.ticks = ticks;
    }

    void moveEntities() {
        List<Entity> allEntities = new ArrayList<>();

        // Collect all entities
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                allEntities.addAll(grid[row][col].getEntities());
            }
        }

        // Move entities randomly to adjacent empty cells
        for (Entity entity : allEntities) {
            if (entity.getState() == cellState.REWARD) {
                continue;
            }

            int currentRow = entity.getGridY();
            int currentCol = entity.getGridX();

            // Random movement to adjacent cell with 50% chance
            if (random.nextDouble() < 0.5) {
                // Find adjacent empty cells
                List<Position> emptyNeighbors = new ArrayList<>();
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dr == 0 && dc == 0) continue; // Skip current cell
                        
                        int newRow = currentRow + dr;
                        int newCol = currentCol + dc;
                        
                        // Boundary checking
                        if (newRow >= 0 && newRow < gridRows && newCol >= 0 && newCol < gridCols) {
                            if (grid[newRow][newCol].getEntityCount() == 0) {
                                emptyNeighbors.add(new Position(newRow, newCol));
                            }
                        }
                    }
                }

                // Move to a random empty neighbor if available
                if (!emptyNeighbors.isEmpty()) {
                    Position target = emptyNeighbors.get(random.nextInt(emptyNeighbors.size()));
                    grid[currentRow][currentCol].removeEntity(entity);
                    entity.setGridX(target.getColumn());
                    entity.setGridY(target.getRow());
                    grid[target.getRow()][target.getColumn()].addEntity(entity);
                }
            }
        }
    }

    void spreadInfection() {
        infectionStrategy.spread(this);
    }

    void spreadInfectionSequential() {
        // Handle infection spread to adjacent cells
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                GridCell cell = grid[row][col];
                if (cell.getInfectedCount() > 0) {
                    // Infect healthy entities in adjacent cells
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            if (dr == 0 && dc == 0) continue;
                            
                            int adjRow = row + dr;
                            int adjCol = col + dc;
                            
                            if (adjRow >= 0 && adjRow < gridRows && adjCol >= 0 && adjCol < gridCols) {
                                Entity target = getInfectionTarget(grid[adjRow][adjCol]);
                                if (target != null
                                        && random.nextDouble() < getInfectionChance(target)) {
                                    target.infect();
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Calculates infection targets from a stable start-of-phase view, then
     * applies the resulting state changes sequentially. This mode intentionally
     * does not allow same-tick infection chaining.
     */
    void spreadInfectionParallelCalculation() {
        List<InfectionAttempt> attempts = new ArrayList<>();

        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                if (grid[row][col].getInfectedCount() == 0) {
                    continue;
                }

                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dr == 0 && dc == 0) {
                            continue;
                        }

                        int adjRow = row + dr;
                        int adjCol = col + dc;
                        if (adjRow < 0 || adjRow >= gridRows
                                || adjCol < 0 || adjCol >= gridCols) {
                            continue;
                        }

                        Entity target = getInfectionTarget(grid[adjRow][adjCol]);
                        if (target != null) {
                            attempts.add(new InfectionAttempt(
                                    target,
                                    random.nextDouble()));
                        }
                    }
                }
            }
        }

        List<Entity> targets = attempts.parallelStream()
                .filter(attempt -> attempt.target.getState() == cellState.HEALTHY
                        || attempt.target.getState() == cellState.ENHANCED_HEALTHY)
                .filter(attempt -> attempt.roll < getInfectionChance(attempt.target))
                .map(attempt -> attempt.target)
                .collect(Collectors.toList());

        Set<Entity> uniqueTargets =
                Collections.newSetFromMap(new IdentityHashMap<>());
        for (Entity target : targets) {
            if (uniqueTargets.add(target)) {
                target.infect();
            }
        }
    }

    public void setInfectionStrategy(InfectionStrategy infectionStrategy) {
        if (infectionStrategy == null) {
            throw new IllegalArgumentException("Infection strategy cannot be null");
        }
        this.infectionStrategy = infectionStrategy;
    }

    public void setDifficulty(Difficulty difficulty) {
        if (difficulty == null) {
            throw new IllegalArgumentException("Difficulty cannot be null");
        }
        this.difficulty = difficulty;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    void developEnhancedHealthy() {
        if (ticks % 5 != 0) {
            return;
        }

        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                for (Entity entity : grid[row][col].getEntities()) {
                    if (entity.getState() == cellState.HEALTHY && random.nextDouble() < 0.05) {
                        entity.enhanceHealthy();
                    }
                }
            }
        }
    }

    void developDefenders() {
        int defenderLimit = Math.max(1, getTotalEntityCount() / 10);
        if (ticks < 6 || ticks % 3 != 0 || getTotalInfectedCount() == 0
                || getTotalDefenderCount() >= defenderLimit || random.nextDouble() >= 0.25) {
            return;
        }

        List<GridCell> healthyCells = allCells()
                .filter(cell -> cell.getHealthyCount() > 0)
                .collect(java.util.stream.Collectors.toList());

        if (!healthyCells.isEmpty()) {
            GridCell cell = healthyCells.get(random.nextInt(healthyCells.size()));
            Entity defender = cell.getEntities().get(0);
            defender.setState(cellState.DEFENDER);
        }
    }

    void cureNearbyInfections() {
        Set<Entity> entitiesToCure =
                Collections.newSetFromMap(new IdentityHashMap<>());

        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                if (grid[row][col].getDefenderCount() == 0) {
                    continue;
                }

                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int adjRow = row + dr;
                        int adjCol = col + dc;
                        if (adjRow >= 0 && adjRow < gridRows && adjCol >= 0 && adjCol < gridCols) {
                            for (Entity entity : grid[adjRow][adjCol].getEntities()) {
                                if (entity.getState() == cellState.INFECTED
                                        && !entitiesToCure.contains(entity)
                                        && random.nextDouble() < 0.25) {
                                    entitiesToCure.add(entity);
                                }
                            }
                        }
                    }
                }
            }
        }

        entitiesToCure.forEach(Entity::cure);
    }

    public void setInfectionChance(double infectionChance) {
        this.infectionChance = infectionChance;
    }

    public double getInfectionChance() {
        return infectionChance;
    }

    private double getInfectionChance(Entity target) {
        return target.getState() == cellState.ENHANCED_HEALTHY
                || target.getState() == cellState.DEFENDER
                ? infectionChance * ENHANCED_HEALTHY_INFECTION_MULTIPLIER
                : infectionChance;
    }

    private Entity getInfectionTarget(GridCell cell) {
        return cell.getEntities().stream()
                .filter(entity -> entity.getState() == cellState.HEALTHY
                        || entity.getState() == cellState.ENHANCED_HEALTHY
                        || entity.getState() == cellState.DEFENDER)
                .findFirst()
                .orElse(null);
    }

    public GridCell getGridCell(int row, int col) {
        if (row >= 0 && row < gridRows && col >= 0 && col < gridCols) {
            return grid[row][col];
        }
        return null;
    }

    public int getGridRows() {
        return gridRows;
    }

    public int getGridCols() {
        return gridCols;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public int getTotalEntityCount() {
        return (int) allEntities().count();
    }

    public int getTotalHealthyCount() {
        return countEntities(entity -> entity.getState() == cellState.HEALTHY);
    }

    public int getTotalInfectedCount() {
        return countEntities(entity -> entity.getState() == cellState.INFECTED);
    }

    public int getTotalEnhancedHealthyCount() {
        return countEntities(entity -> entity.getState() == cellState.ENHANCED_HEALTHY);
    }

    public int getTotalDefenderCount() {
        return countEntities(entity -> entity.getState() == cellState.DEFENDER);
    }

    public Map<cellState, Long> getEntityStateCounts() {
        return allEntities()
                .collect(Collectors.groupingBy(
                        Entity::getState,
                        Collectors.counting()
                ));
    }

    public Map<cellState, Double> getEntityStatePercentages() {
        Map<cellState, Long> stateCounts = getEntityStateCounts();
        long totalEntities = stateCounts.values().stream()
                .mapToLong(Long::longValue)
                .sum();
        Map<cellState, Double> percentages = new EnumMap<>(cellState.class);

        for (cellState state : cellState.values()) {
            long stateCount = stateCounts.getOrDefault(state, 0L);
            double percentage = totalEntities == 0
                    ? 0.0
                    : (stateCount * 100.0) / totalEntities;
            percentages.put(state, percentage);
        }

        return percentages;
    }

    private Stream<GridCell> allCells() {
        return Arrays.stream(grid).flatMap(Arrays::stream);
    }

    private Stream<Entity> allEntities() {
        return allCells().flatMap(cell -> cell.getEntities().stream());
    }

    private int countEntities(java.util.function.Predicate<Entity> condition) {
        return (int) allEntities()
                .filter(condition)
                .count();
    }

    private static final class InfectionAttempt {
        private final Entity target;
        private final double roll;

        private InfectionAttempt(Entity target, double roll) {
            this.target = target;
            this.roll = roll;
        }
    }
}