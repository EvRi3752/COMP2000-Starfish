public class ParallelInfectionStrategy implements InfectionStrategy {
    @Override
    public void spread(section simulationSection) {
        simulationSection.spreadInfectionParallelCalculation();
    }
}
