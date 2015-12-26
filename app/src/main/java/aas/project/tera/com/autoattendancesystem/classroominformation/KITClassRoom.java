package aas.project.tera.com.autoattendancesystem.classroominformation;

/**
 * Created by HeeJeong on 2015-06-02.
 */
public class KITClassRoom {
    private String building;
    private String classroom;
    private String state;

    public KITClassRoom(String building, String classroom, String state) {
        this.building = building;
        this.classroom = classroom;
        this.state = state;
    }

    public String getBuilding() {
        return this.building;
    }

    public String getClassroom() {
        return this.classroom;
    }

    public String getState() {
        return this.state;
    }
}