import java.util.List;
import java.util.Random;

public final class RandomMovement implements MovementStrategy {

    @Override
    public Position chooseDestination(
            Entity entity,
            section currentSection,
            List<Position> emptyNeighbors,
            Random random) {

        if (emptyNeighbors.isEmpty()) {
            return null;
        }

        return emptyNeighbors.get(
            random.nextInt(emptyNeighbors.size())
        );
    }
}
