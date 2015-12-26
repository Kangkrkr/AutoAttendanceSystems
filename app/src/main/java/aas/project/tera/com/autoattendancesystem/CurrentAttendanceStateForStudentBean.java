package aas.project.tera.com.autoattendancesystem;

/**
 * Created by Administrator on 2015-06-02.
 */
public class CurrentAttendanceStateForStudentBean {

    private String stat;
    private String lname;
    private String pname;

    int attendance = 0, lateness = 0, absence = 0;

    public CurrentAttendanceStateForStudentBean(String stat, String lname, String pname) {
        this.stat = stat;
        this.lname = lname;
        this.pname = pname;
    }

    public CurrentAttendanceStateForStudentBean(String lname, String pname, int attendance, int lateness, int absence) {
        this.lname = lname;
        this.pname = pname;

        this.attendance = attendance;
        this.lateness = lateness;
        this.absence = absence;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public int getLateness() {
        return lateness;
    }

    public void setLateness(int lateness) {
        this.lateness = lateness;
    }

    public int getAbsence() {
        return absence;
    }

    public void setAbsence(int absence) {
        this.absence = absence;
    }

    public int getAttendance() {
        return attendance;
    }

    public void setAttendance(int attendance) {
        this.attendance = attendance;
    }
}
