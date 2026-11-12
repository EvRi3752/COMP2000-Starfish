
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class PinpointInfection implements MovementStrategy {
    private final MovementStrategy fallback = new RandomMovement();

    @Override
    public Position chooseDestination(
            Entity entity,
            section currentSection,
            List<Position> emptyNeighbors,
            Random random) {

        if (emptyNeighbors.isEmpty()) {
            return null;
        }

        if (currentSection.getTotalInfectedCount() == 0) {
            return fallback.chooseDestination(entity, currentSection, emptyNeighbors, random
            );
        }

        int bestDistance = Integer.MAX_VALUE;
        List<Position> bestMoves = new ArrayList<>();

        for (Position candidate : emptyNeighbors) {
            int distance = currentSection.distanceToNearestInfected(candidate.getRow(), candidate.getColumn());

            if (distance < bestDistance) {
                bestDistance = distance;
                bestMoves.clear();
                bestMoves.add(candidate);

            } else if (distance == bestDistance) {
                bestMoves.add(candidate);
            }
        }

        return bestMoves.get(random.nextInt(bestMoves.size()));
    }
}
