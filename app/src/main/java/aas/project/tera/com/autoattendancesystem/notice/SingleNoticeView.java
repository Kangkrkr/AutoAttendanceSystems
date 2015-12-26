package aas.project.tera.com.autoattendancesystem.notice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.Date;

import aas.project.tera.com.autoattendancesystem.MyRECOBeaconManager;
import aas.project.tera.com.autoattendancesystem.MySocketIO;
import aas.tera.com.autoattendancesystem.R;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;

/**
 * Created by HeeJeong on 2015-06-07.
 */
public class SingleNoticeView extends ActionBarActivity implements IOCallback {
    // Declare Variables
    TextView txtlecturename;
    TextView txtnoticetitle;
    TextView txnoticewiter;
    TextView txnoticebody;
    TextView txnoticedate;
    TextView txbtn_notice_ok;

    String lecturename;
    String noticetitle;
    String witer;
    String body;
    String date;

    android.os.Handler handler = new android.os.Handler();

    String Pid;
    String Lno;
    int Nno;

    private MySocketIO mySocketIO;

    Activity activity;

    Intent i;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            mySocketIO = new MySocketIO(this);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        activity = this;

        i = getIntent();

        if(i.getStringExtra("identity").equals("student")) {
            setContentView(R.layout.singlenoticeview);
        }
        else if(i.getStringExtra("identity").equals("professorwrite")){
            setContentView(R.layout.notice_writepage);
            Lno = i.getStringExtra("Lno");
            Pid = i.getStringExtra("Pid");
        }
        else{
            setContentView(R.layout.singlenoticeview_professor);
            Lno = i.getStringExtra("Lno");
            Pid = i.getStringExtra("Pid");
            Nno = i.getIntExtra("Nno",0);
        }

        Log.i("테스트...", i.getStringExtra("identity"));

//        View view = (View) findViewById(R.id.singlenoticeview);

        // Retrieve data from MainActivity on item click event

        // Get the results of classroom
        lecturename = i.getStringExtra("lecturename");
        // Get the results of state
        noticetitle = i.getStringExtra("noticetitle");
        // Get the results of witer
        witer = i.getStringExtra("witer");
        // Get the results of body
        body = i.getStringExtra("body");

        date = i.getStringExtra("date");



        // Locate the TextViews in singleitemview.xml
        txtlecturename = (TextView) findViewById(R.id.lecturename);
        txtnoticetitle = (TextView) findViewById(R.id.noticetitle);
        txnoticewiter = (TextView) findViewById(R.id.noticewiter);
        txnoticebody = (TextView) findViewById(R.id.noticebody);
        txnoticedate = (TextView) findViewById(R.id.noticeitem_date);
        txbtn_notice_ok = (TextView) findViewById(R.id.btn_notice_ok);

        if(!(i.getStringExtra("identity").equals("professorwrite"))) {
            // Load the results into the TextViews
            txtlecturename.setText(lecturename);
            txtnoticetitle.setText(noticetitle);
            txnoticewiter.setText(witer);
            txnoticebody.setText(body);
            txnoticedate.setText(date);
        }
        else{
            txtnoticetitle.setText("");
            txnoticebody.setText("");
        }



        Date date = new Date();
        final String newdate = String.valueOf(date.getYear()+1900)+"-"+String.valueOf(date.getMonth() + 1)+"-"+String.valueOf(date.getDate());

        if((i.getStringExtra("identity").equals("professorwrite"))) {
            txbtn_notice_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("Lno", Lno).put("Pid", Pid).put("Lname", lecturename).put("Ntitle", txtnoticetitle.getText())
                                .put("Pname", witer).put("Nbody", txnoticebody.getText())
                                .put("Ndate", newdate);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mySocketIO.emit("professorNoticeAdd",  new IOAcknowledge() {
                        @Override
                        public void ack(Object... objects) {

                            final NoticeActivity test = (NoticeActivity) MyRECOBeaconManager.aQueryMap.get(NoticeActivity.class);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    test.recreate();
                                }
                            });
                            activity.finish();
                        }
                    },object);
                }
            });
        }
        else if(i.getStringExtra("identity").equals("professor")){
            txbtn_notice_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("Nno",Nno).put("Lno", Lno).put("Pid", Pid).put("Lname", lecturename).put("Ntitle", txtnoticetitle.getText())
                                .put("Pname", witer).put("Nbody", txnoticebody.getText())
                                .put("Ndate", newdate);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mySocketIO.emit("professorNoticeUpdate", new IOAcknowledge() {
                        @Override
                        public void ack(Object... objects) {

                            final NoticeActivity test = (NoticeActivity) MyRECOBeaconManager.aQueryMap.get(NoticeActivity.class);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    test.recreate();
                                }
                            });
                            activity.finish();
                        }
                    },object);
                }
            });
        }



        //notice_writepage



    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onMessage(String s, IOAcknowledge ioAcknowledge) {

    }

    @Override
    public void onMessage(JSONObject jsonObject, IOAcknowledge ioAcknowledge) {

    }

    @Override
    public void on(String s, IOAcknowledge ioAcknowledge, Object... objects) {

    }

    @Override
    public void onError(SocketIOException e) {

    }
}
