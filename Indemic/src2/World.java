import java.util.ArrayList;

public class World {

    private ArrayList<Section> sections;

    public World() {
        sections = new ArrayList<>();
        sections.add(new Section(0, 0, 600, 400, 6, 1, 1));
        sections.add(new Section(600, 0, 600, 400, 8, 1, 1));
        sections.add(new Section(0, 400, 600, 400, 9, 1, 1));
        sections.add(new Section(600, 400, 600, 400, 7, 1, 1));
    }

    public ArrayList<Section> getSections() {
        return sections;
    }
}