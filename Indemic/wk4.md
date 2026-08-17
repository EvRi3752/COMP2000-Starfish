Cell (abstract)
├── fields: x, y, velocityX, velocityY
├── move()          — shared, same for everyone
├── isTouching()     — shared, same for everyone
└── interact(Cell other)  — abstract, differs per subclass

├── HealthyCell extends Cell
├── InfectedCell extends Cell
└── DefenderCell extends Cell