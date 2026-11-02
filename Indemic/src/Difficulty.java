public enum Difficulty {
    EASY("Easy", false, false),
    NORMAL("Normal", true, false),
    HARD("Hard", true, true);

    private final String label;
    private final boolean enhancedHealthyEnabled;
    private final boolean defendersEnabled;

    Difficulty(String label, boolean enhancedHealthyEnabled, boolean defendersEnabled) {
        this.label = label;
        this.enhancedHealthyEnabled = enhancedHealthyEnabled;
        this.defendersEnabled = defendersEnabled;
    }

    public String getLabel() {
        return label;
    }

    public boolean isEnhancedHealthyEnabled() {
        return enhancedHealthyEnabled;
    }

    public boolean areDefendersEnabled() {
        return defendersEnabled;
    }
}
