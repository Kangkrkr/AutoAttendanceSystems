package aas.project.tera.com.autoattendancesystem;

/**
 * Created by Administrator on 2015-06-02.
 */
public class TakeCourseBean {

    private int year;
    private int sno;
    private String sname;

    public TakeCourseBean(int year, int sno, String sname) {
        this.year = year;
        this.sno = sno;
        this.sname = sname;
    }



    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public int getSno() {
        return sno;
    }

    public void setSno(int sno) {
        this.sno = sno;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
