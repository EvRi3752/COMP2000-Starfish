Cell (abstract)
├── fields: x, y, velocityX, velocityY
├── move()          — shared, same for everyone
├── isTouching()     — shared, same for everyone
└── interact(Cell other)  — abstract, differs per subclass

├── healthyCell extends Cell
├── infectedCell extends Cell
└── defenderCell extends Cell

Two or more classes share common fields or methods — can a superclass extract that shared state?
- A cell superclass would work here as the "cell" itself is the base class for every derived class that there would be for "healthy, infected, and defender" cells... 
- For now the only difference between each "cell" class would be how they interact with each other when they come into "contact" on the grid. 

• A method’s behaviour should differ depending on the subclass — where would @Override let you specialise?
- That would be the interact() method as for example if an InfectedCell interacts with a HealthyCell that would cause the HealthyCell to convert into an InfectedCell.

• A method needs to handle different parameter types — could overloading provide a cleaner interface?
- In our case, it would not due to the fact that a double dispatch problem would occur meaning if I overload the interact() method, it wouldn't do the specialised behaviour I want it to do. 
- For example... 
Interact(healthyCell other)
Interact(infectedCell other)
Interact(defenderCell other)
- Java would only pick the generic Interact(cell other) instead of the methods above due to the code. 

