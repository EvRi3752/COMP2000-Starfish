import java.util.ArrayList;

public class world {

    private ArrayList<section> sections;

    public world() {
        sections = new ArrayList<>();
        sections.add(new section(0, 0, 600, 400, 3));
        sections.add(new section(600, 0, 600, 400, 5));
        sections.add(new section(0, 400, 600, 400, 6));
        sections.add(new section(600, 400, 600, 400, 2));
    }

    public ArrayList<section> getSections() {
        return sections;
    }
}