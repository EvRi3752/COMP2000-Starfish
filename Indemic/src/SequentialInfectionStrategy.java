public class SequentialInfectionStrategy implements InfectionStrategy {
    @Override
    public void spread(section simulationSection) {
        simulationSection.spreadInfectionSequential();
    }
}
