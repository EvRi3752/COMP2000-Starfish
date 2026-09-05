## Indemic - Epidemic Spread Simulation

Indemic is a Java Swing simulation game for COMP2000. The player chooses where an
infection starts, watches it spread through a 2x2 world, and spends DNA points on
upgrades while the population develops resistance and defenders.

## Running the Project

From the `Indemic` directory:

```sh
javac -d bin src/*.java
java -cp bin App
```

The application opens a fixed-size window with four sections. The simulation
updates every five seconds.

## How to Play

1. Click one of the four sections to choose where the infection begins.
2. Allow the simulation to run. DNA points are awarded periodically.
3. Spend a DNA point on one of the buttons in the bottom panel:
	- **Upgrade Infectivity** increases the chance of infecting a nearby healthy entity.
	- **Upgrade Transmission** lowers the number of infected entities needed to spread to a neighboring section.

The status message beside the upgrade buttons reports new DNA points and upgrade
results. The simulation does not begin until a starting section is selected.

## Entity Types

- **Healthy**: Normal green entities that can become infected.
- **Infected**: Red entities that spread the infection to nearby healthy entities.
- **Enhanced healthy**: Dark-green entities that are harder to infect than normal healthy entities.
- **Defender**: Blue entities that develop as the infection progresses and can cure nearby infected entities.

The section statistics show the current counts of healthy, infected, enhanced
healthy, and defender entities. The color legend is shown in the bottom panel.

## Display

The world contains four sections, each with a 5x5 grid of cells.

![Initial visual diagram](Indemic/Images/PXL_20260810_064904740.png)