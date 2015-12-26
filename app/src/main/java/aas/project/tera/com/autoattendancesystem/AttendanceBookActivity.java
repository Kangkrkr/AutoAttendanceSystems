package aas.project.tera.com.autoattendancesystem;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.perples.recosdk.RECOBeaconManager;
import com.rey.material.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import aas.tera.com.autoattendancesystem.R;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;


public class AttendanceBookActivity extends ActionBarActivity implements IOCallback {

    private AttendanceBookAdapter attendanceBookAdapter;
    private ListView listView;
    private MySocketIO mySocketIO;
    private JSONObject object = new JSONObject();
    private JSONArray jsonArray;
    private Handler handler = new Handler();
    private AQuery aQuery;

    private MyRECOBeaconManager myRECOBeaconManager;

    private Map<String, String> map = MapRegister.getInstance();

    private Date date = new Date();
    private String year = String.valueOf(date.getYear()+1900);
    private String month = String.valueOf(date.getMonth()+1);
    private String _date = String.valueOf(date.getDate());
    private String day = map.get(String.valueOf(date.getDay()));
    private String period = map.get(String.valueOf(date.getHours()));

    private ExecutorService es = Executors.newFixedThreadPool(2);
    private Future future;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_book);

        aQuery = new AQuery(this);

        MyRECOBeaconManager.putAQuery(AttendanceBookActivity.class, this);

        aQuery.id(R.id.current_date).text(year+"년 "+month+"월 "+_date+"일 "+day+"요일 "+period+"교시 출석부");

        listView = (ListView)findViewById(R.id.student_list);
        attendanceBookAdapter = new AttendanceBookAdapter(aQuery);

        Intent intent = getIntent();
        final LectureInfoBean lectureInfoBean = (LectureInfoBean)intent.getSerializableExtra("data");

        aQuery.id(R.id.current_lecture_room).text(lectureInfoBean.getRname());

        try{
            this.mySocketIO = new MySocketIO(this);
            myRECOBeaconManager = new MyRECOBeaconManager(this);
            MyRECOBeaconManager.putAQuery(AttendanceActivity.class, this);
            myRECOBeaconManager.startService();

            startAttendance(lectureInfoBean);
        }catch(Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void startAttendance(final LectureInfoBean lectureInfoBean) throws JSONException {

        object.put("lcode", lectureInfoBean.getLcode()).put("dev", lectureInfoBean.getDev())
                .put("lname", lectureInfoBean.getLname()).put("pid", lectureInfoBean.getPid())
                .put("lday", lectureInfoBean.getLday()).put("lperiod", lectureInfoBean.getLperiod())
                .put("rname", lectureInfoBean.getRname()).put("lno", lectureInfoBean.getLno());

        mySocketIO.emit("getTakeCourseList", new IOAcknowledge() {
            @Override
            public void ack(Object... objects) {
                try {
                    String message = objects[0].toString();
                    jsonArray = new JSONArray(message);

                    future = es.submit(new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    int year = jsonArray.getJSONObject(i).getInt("Year");
                                    int sno = jsonArray.getJSONObject(i).getInt("Sno");
                                    String sname = jsonArray.getJSONObject(i).getString("Sname");

                                    attendanceBookAdapter.addItem(new TakeCourseBean(year, sno, sname));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            return null;
                        }
                    });

                    while(!future.isDone()){Log.i("쓰레드가 동작중입니다..", "쓰레드 동작중");}
                    Log.i("JSONArray 길이 ", String.valueOf(jsonArray.length()));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listView.setAdapter(attendanceBookAdapter);
                        }
                    });

                    Date wakeTime = new Date();
                    int toLectureTime = 50 - wakeTime.getMinutes();
                    Log.i("수업해야 할 시간", toLectureTime + "");
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(aQuery.getContext(), "수업 끝", Toast.LENGTH_SHORT).show();
                                    for (int i = 0; i < aQuery.id(R.id.student_list).getListView().getChildCount(); i++) {
                                        TextView grade = ((TextView) aQuery.id(R.id.student_list).getListView().getChildAt(i).findViewById(R.id.grade_title));
                                        TextView number = ((TextView) aQuery.id(R.id.student_list).getListView().getChildAt(i).findViewById(R.id.class_number_title));
                                        TextView name = ((TextView) aQuery.id(R.id.student_list).getListView().getChildAt(i).findViewById(R.id.name_title));

                                        RelativeLayout container = ((RelativeLayout) aQuery.id(R.id.student_list).getListView().getChildAt(i).findViewById(R.id.button_container));

                                        int tag = 0, curPos = 0;
                                        try {
                                            while ((tag = Integer.parseInt(container.getChildAt(curPos).getTag().toString())) == 0) {
                                                curPos++;
                                                if (curPos >= 2) {
                                                    break;
                                                }
                                            }

                                        } catch (NullPointerException e) {
                                            Log.i("에러 인포", "tag : " + tag + " , curPos : " + curPos);
                                        }

                                        String attendanceState = (tag == 1) ? ((Button) container.getChildAt(curPos)).getText().toString() : "결석";
                                        Log.i("테스트", name.getText().toString() + " - " + attendanceState + "");
                                        JSONObject attendanceResult = new JSONObject();
                                        try {
                                            attendanceResult.put("Lno", lectureInfoBean.getLno())
                                                    .put("Sno", Integer.parseInt(number.getText().toString()))
                                                    .put("Date", year + "-" + month + "-" + _date)//new java.sql.Date(Integer.parseInt(year)-1900, Integer.parseInt(month)-1, Integer.parseInt(_date))
                                                    .put("Stat", attendanceState);
                                            mySocketIO.emit("sendAttendanceResult", attendanceResult);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    }, toLectureTime * 2500);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, object);
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
    public void on(String s, IOAcknowledge ioAcknowledge, final Object... objects) {
        System.out.println("출석부에서 받는 이벤트 "+s);
        // check 이벤트를 받음..
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(objects[0].toString());

                            String client_id = jsonObject.getString("id");
                            int client_minute = Integer.parseInt(jsonObject.getString("minute"));

                            Toast.makeText(aQuery.getContext(), client_id+", "+client_minute, Toast.LENGTH_SHORT).show();

//                            RelativeLayout buttonContainer = (RelativeLayout) aQuery.id(R.id.button_container).getView();

                            for(int i=0; i<aQuery.id(R.id.student_list).getListView().getChildCount(); i++){
                                String server_id = ((TextView)aQuery.id(R.id.student_list).getListView().getChildAt(i).findViewById(R.id.class_number_title)).getText().toString();
                                RelativeLayout buttonContainer = ((RelativeLayout) aQuery.id(R.id.student_list).getListView().getChildAt(i).findViewById(R.id.button_container));
                                if(client_id.equals(server_id)){
                                    for (int childIndex = 0; childIndex < buttonContainer.getChildCount(); childIndex++) {
                                        Button button = (Button) buttonContainer.getChildAt(childIndex);
//                                        Log.i("테스트", i+"번 아이템의 태그들 - "+button.getTag()+"");
                                        if(client_minute >= 0 && client_minute <= 10){
                                            ((Button)buttonContainer.getChildAt(0)).setTextColor(0xff21ff0f);
                                            ((Button)buttonContainer.getChildAt(0)).setTag(1);
//                                            break;
                                        } else if(client_minute > 10 && client_minute <= 50){
                                            ((Button)buttonContainer.getChildAt(1)).setTextColor(0xfffbff1a);
                                            ((Button)buttonContainer.getChildAt(1)).setTag(1);
//                                            break;
                                        } else {
                                            ((Button)buttonContainer.getChildAt(2)).setTextColor(0xffff2320);
                                            ((Button)buttonContainer.getChildAt(2)).setTag(1);
//                                            break;
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IndexOutOfBoundsException e){
                            Toast.makeText(aQuery.getContext(), "정보를 받아오는데 실패하였습니다.", Toast.LENGTH_SHORT).show();
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
