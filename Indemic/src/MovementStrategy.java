import java.util.List;
import java.util.Random;

public interface MovementStrategy {
    Position chooseDestination(
        Entity entity,
        section currentSection,
        List<Position> emptyNeighbors,
        Random random
    );
}