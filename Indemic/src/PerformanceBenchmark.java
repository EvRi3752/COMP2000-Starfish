import java.util.Random;

public final class PerformanceBenchmark {
    private static final int WARMUP_RUNS = 3;
    private static final int MEASURED_RUNS = 8;
    private static final int TICKS = 40;

    private PerformanceBenchmark() {
    }

    public static void main(String[] args) {
        benchmark("sequential", false);
        benchmark("parallel", true);
    }

    private static void benchmark(String name, boolean parallel) {
        for (int run = 0; run < WARMUP_RUNS + MEASURED_RUNS; run++) {
            section simulation = createSimulation(parallel);
            long start = System.nanoTime();
            for (int tick = 0; tick < TICKS; tick++) {
                simulation.tick();
            }
            long elapsed = System.nanoTime() - start;

            if (run >= WARMUP_RUNS) {
                System.out.printf(
                        "%s run %d: %.3f ms%n",
                        name,
                        run - WARMUP_RUNS + 1,
                        elapsed / 1_000_000.0);
            }
        }
    }

    private static section createSimulation(boolean parallel) {
        section simulation = new section(
                0,
                0,
                1000,
                1000,
                100,
                100,
                5_000,
                new Random(42));
        simulation.setInitialInfection();
        simulation.setParallelInfectionEnabled(parallel);
        return simulation;
    }
}
