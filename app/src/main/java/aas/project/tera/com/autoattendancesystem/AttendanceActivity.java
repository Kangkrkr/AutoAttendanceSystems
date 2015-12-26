package aas.project.tera.com.autoattendancesystem;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.perples.recosdk.RECOBeaconManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;

import aas.tera.com.autoattendancesystem.R;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;


public class AttendanceActivity extends ActionBarActivity implements IOCallback {

    private AQuery aQuery = null;
    private Handler handler = new Handler();

    private HashMap<String, String> map = MapRegister.getInstance();
    private MySocketIO mySocketIO;
    private String id;
    private static boolean isChecked = false;
    private MyRECOBeaconManager myRECOBeaconManager;
    private String rname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance);



        try {
            aQuery = new AQuery(this);
            mySocketIO = new MySocketIO(this);
            id = getIntent().getStringExtra("id");
            isChecked = getIntent().getBooleanExtra("isChecked", false);

            MyRECOBeaconManager.putAQuery(AttendanceActivity.class, this);

            Intent intent = getIntent();
            LectureInfoBean lectureInfoBean = (LectureInfoBean)intent.getSerializableExtra("data");

            String lcode = lectureInfoBean.getLcode();
            int dev = lectureInfoBean.getDev();
            String lname = lectureInfoBean.getLname();
            String pid = lectureInfoBean.getPid();
            String lday = lectureInfoBean.getLday();
            String lperiod = lectureInfoBean.getLperiod();
            rname = lectureInfoBean.getRname();

            aQuery.id(R.id.show_lecture_name).text(lname+" ("+dev+"분반)");
            aQuery.id(R.id.show_professor_name).text(pid);
            aQuery.id(R.id.show_lecture_time).text(lday+"요일 "+lperiod+"교시");
            aQuery.id(R.id.show_lecture_room).text(rname);

            aQuery.id(R.id.request_attendance).clicked(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveTaskToBack(true);
                }
            });

            myRECOBeaconManager = new MyRECOBeaconManager(this);

            try{
                myRECOBeaconManager.startService();
            }catch(Exception e){
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

//        String Lcode = intent.getStringExtra("Lcode");
//        String Dev = intent.getStringExtra("Dev");
//        String Lname = intent.getStringExtra("Lname");
//        String Rname = intent.getStringExtra("Rname");
//
//        aQuery.id(R.id.textView2).text("과목코드 : " + Lcode + " - " + Dev);
//        aQuery.id(R.id.textView3).text("과목명 : "+Lname);
//        aQuery.id(R.id.textView4).text("강의실 : "+Rname);
//
//        // 날짜, 지금 현재 시간에 따른 교시 확인용 코드
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Date date = new Date();
//                        aQuery.id(R.id.textView5).text(map.get(String.valueOf(date.getDay())) + "요일 "
//                                + date.getHours() + "시 " + date.getMinutes() + "분 " + map.get(String.valueOf(date.getHours())) + "교시");
//                    }
//                });
//            }
//        }, 1000*60);    // 1분 마다..
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            RECOBeaconManager.getInstance(this).unbind();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(RECOBeaconManager.getInstance(this).isBound()){
            try {
                RECOBeaconManager.getInstance(this).unbind();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_attendance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public MySocketIO getMySocketIO(){
        if(mySocketIO == null){
            try {
                return new MySocketIO(this);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return mySocketIO;
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
        System.out.println("출석요청에서 받는 이벤트 "+s);
        final String event = s;
        Log.i("이벤트", event);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(event.equals("notifyProfessorEnterd")){
                            aQuery.id(R.id.request_attendance).text("출석체크를 요청중입니다.");
                            JSONObject jsonObject = new JSONObject();
                            Date date = new Date();
                            try {
                                jsonObject.put("id", id).put("hour", map.get(date.getHours())).put("minute", date.getMinutes()).put("rname", rname);

                                mySocketIO.emit("attendanceRequest", jsonObject);

                                aQuery.id(R.id.request_attendance).text("출석체크 완료");
                                aQuery.id(R.id.request_attendance).enabled(false);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onError(SocketIOException e) {

    }
}
