import java.util.ArrayList;
import java.util.List;

public class world {
    private List<section> sections;

    public world(double width, double height) {
        double sectionWidth = width / 2;
        double sectionHeight = height / 2;

        sections = new ArrayList<>();
        sections.add(new section(new ArrayList<>(), sectionWidth, sectionHeight));
        sections.add(new section(new ArrayList<>(), sectionWidth, sectionHeight));
        sections.add(new section(new ArrayList<>(), sectionWidth, sectionHeight));
        sections.add(new section(new ArrayList<>(), sectionWidth, sectionHeight));
    }

    public void tick() {
        for (section currentSection : sections) {
            currentSection.tick();
        }
    }

    public List<section> getSections() {
        return sections;
    }
}