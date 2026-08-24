public enum CellState {
    HEALTHY(1),
    INFECTED(2),
    DEFENDER(3),
    DEAD(4);

    private int id;

    CellState(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}