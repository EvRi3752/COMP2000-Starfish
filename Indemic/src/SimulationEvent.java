
public record SimulationEvent(
    Type type,
    int tick,
    int sectionIndex,
    String message
) {
    public enum Type {
        OUTBREAK_DETECTED,
        QUARANTINE_STARTED,
        QUARANTINE_ENDED
    }
}