package aas.project.tera.com.autoattendancesystem;

import java.io.Serializable;

/**
 * Created by Administrator on 2015-06-02.
 */
public class LectureInfoBean implements Serializable{
    String lcode;
    int dev;
    String lname;
    String pid;
    String lday;
    String lperiod;
    String rname;
    int lno;

    public LectureInfoBean(String lcode, int dev, String lname, String pid, String lday, String lperiond, String rname, int lno){
        this.lcode = lcode;
        this.dev = dev;
        this.lname = lname;
        this.pid = pid;
        this.lday = lday;
        this.lperiod = lperiond;
        this.rname = rname;
        this.lno = lno;
    }

    public int getLno() {
        return lno;
    }

    public void setLno(int lno) {
        this.lno = lno;
    }

    public int getDev() {
        return dev;
    }

    public void setDev(int dev) {
        this.dev = dev;
    }

    public String getLcode() {
        return lcode;
    }

    public void setLcode(String lcode) {
        this.lcode = lcode;
    }

    public String getLday() {
        return lday;
    }

    public void setLday(String lday) {
        this.lday = lday;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getLperiod() {
        return lperiod;
    }

    public void setLperiod(String lperiod) {
        this.lperiod = lperiod;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }
}
