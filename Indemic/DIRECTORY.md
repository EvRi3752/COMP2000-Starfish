Indemic/
├── images/                      
├── src/
│   └── main/
│       └── java/
│           └── com/yourteam/Indemic/
│               ├── App.java              # entry point, starts the window + loop
│               │
│               ├── model/                 # pure simulation logic, no graphics
│               │   ├── Cell.java
│               │   ├── CellState.java     # enum: HEALTHY, INFECTED, DEFENDER, DEAD...
│               │   ├── Section.java       # one of your 4 boxes/quadrants
│               │   └── World.java         # holds all Sections, runs tick()
│               │
│               ├── view/                  # rendering only
│               │   ├── WorldPanel.java    # JPanel, draws the World
│               │   └── ColorMapper.java   # CellState -> Color
│               │
│               ├── controller/
│               │   └── SimulationLoop.java # Timer/thread that calls tick() + repaint()
│               │
│               └── util/
│                   └── RandomUtil.java     # random walk helpers, probability rolls
│
├── src/
│   └── test/
│       └── java/
│           └── com/yourteam/plaguesim/
│               └── model/
│                   └── CellTest.java       # test infection logic without any GUI
│
└── resources/                    # icons, config files, etc. if needed later