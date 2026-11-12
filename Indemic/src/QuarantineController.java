
public final class QuarantineController implements SimulationObserver {
    public static final int QUARANTINE_DURATION_TICKS = 5;
    private final world simulation;

    public QuarantineController(world simulation) {
        this.simulation = java.util.Objects.requireNonNull(simulation);
    }

    @Override
    public void onSimulationEvent(SimulationEvent event) {
        if (event.type() == SimulationEvent.Type.OUTBREAK_DETECTED) {
            simulation.quarantineSection(event.sectionIndex(), QUARANTINE_DURATION_TICKS);
        }
    }
}
