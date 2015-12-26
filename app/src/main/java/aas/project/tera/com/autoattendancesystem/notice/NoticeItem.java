package aas.project.tera.com.autoattendancesystem.notice;

/**
 * Created by Administrator on 2015-05-09.
 */
public class NoticeItem {
    private int Nno;
    private String lectureName;
    private String noticeTitle;
    private String writer;
    private String date;
    private String body;

    NoticeItem(int Nno, String lectureName, String noticeTitle, String writer, String date, String body){
        this.Nno = Nno;
        this.lectureName = lectureName;
        this.noticeTitle = noticeTitle;
        this.writer = writer;
        this.date = date;
        this.body = body;
    }

    public int getNno() { return Nno;   }

    public String getBody() {  return body;   }

    public void setBody(String body) {
        this.body = body;
    }


    public String getDate() {  return date;   }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLectureName() { return lectureName;  }

    public void setLectureName(String lectureName) {
        this.lectureName = lectureName;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }
}
