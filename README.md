## Indemic - Epidemic Spread Simulation Project for COMP2000

A game-like project to simulate Epidemic spread... 

## Display

There will be 4 grids of square cells in one screen.
![Initial visual diagram](Indemic/images/PXL_20260810_064904740.png)

## Rules

Start with a single red (infected) cell
Green (healthy) cells move around within their section
When a green cell contacts a red cell, it becomes infected (turns red)
Infected cells multiply, increasing red cell count over time

Introduce a second cell type that acts as a counter to the red cells
On contact, this cell can either revert a red cell back to healthy, or kill it outright
This gives the simulation something to "fight against" rather than just one-directional spread