package aas.project.tera.com.autoattendancesystem;


// �몄옄瑜��꾨� ��媛�졇���꾩슂 �놁쓣�� �섏쨷��肄붾뱶 �뺣━

public class GridInfo {
    private String Lcode;
    private int Dev;
    private String Lname;
    private String Pid;
    private String Lday;
    private String Ltime;
    private String Rname;

    public GridInfo(String lname){
        this.Lname = lname;
    }

    public GridInfo(String lcode, int dev, String lname, String pid, String lday, String ltime, String rname){
        this.Lcode=lcode;
        this.Dev=dev;
        this.Lname=lname;
        this.Pid=pid;
        this.Lday=lday;
        this.Ltime=ltime;
        this.Rname=rname;
    }

    public String getLcode() {  return Lcode;   }
    public int getDev() {   return Dev;    }
    public String getLname() {    return Lname;    }
    public String getPid() {     return Pid;    }
    public String getLday() {    return Lday;    }
    public String getLtime() {    return Ltime;    }
    public String getRname() {    return Rname;    }
}
