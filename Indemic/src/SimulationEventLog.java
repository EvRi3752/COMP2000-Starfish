import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class SimulationEventLog implements SimulationObserver { // Keeps history of outbreaks and quarantines 
    private static final int MAX_EVENTS = 6;
    private final Deque<SimulationEvent> events = new ArrayDeque<>();

    @Override
    public void onSimulationEvent(SimulationEvent event) {
        events.addFirst(event);
        if (events.size() > MAX_EVENTS) {
            events.removeLast();
        }
    }

    public List<SimulationEvent> getRecentEvents() {
        return List.copyOf(new ArrayList<>(events));
    }

    public void clear() {
        events.clear();
    }
}
