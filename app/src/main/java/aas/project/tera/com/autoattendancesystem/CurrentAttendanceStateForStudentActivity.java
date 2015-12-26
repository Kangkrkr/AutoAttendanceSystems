package aas.project.tera.com.autoattendancesystem;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.androidquery.AQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import aas.tera.com.autoattendancesystem.R;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;


public class CurrentAttendanceStateForStudentActivity extends ActionBarActivity implements IOCallback {

    private CurrentAttendanceStateForStudentAdapter currentAttendanceStateForStudentAdapter;
    private ListView listView;
    private MySocketIO mySocketIO;
    private JSONObject object = new JSONObject();
    private JSONArray jsonArray;
    private Handler handler = new Handler();
    private AQuery aQuery;

    private Map<String, String> map = MapRegister.getInstance();
    private Set<String> lectureName = new HashSet<>();
    private ArrayList<CurrentAttendanceStateForStudentBean> currentAttendanceStatePiece = new ArrayList<>();

    private Date date = new Date();
    private String year = String.valueOf(date.getYear()+1900);
    private String month = String.valueOf(date.getMonth()+1);
    private String _date = String.valueOf(date.getDate());
    private String day = map.get(String.valueOf(date.getDay()));
    private String period = map.get(String.valueOf(date.getHours()));

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_attendancebook);

        aQuery = new AQuery(this);
        id = getIntent().getStringExtra("id");

        listView = (ListView)findViewById(R.id.student_book_listview);
        currentAttendanceStateForStudentAdapter = new CurrentAttendanceStateForStudentAdapter(this);

        try {
            mySocketIO = new MySocketIO(this);
            object.put("id", id);

            mySocketIO.emit("getStudentCourseList", new IOAcknowledge() {
                @Override
                public void ack(Object... objects) {
                    final JSONArray jsonArray;
                    try {
                        jsonArray = new JSONArray(objects[0].toString());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        String lname = jsonArray.getJSONObject(i).getString("Lname");
                                        lectureName.add(lname);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Log.i("현재 듣는 수업", lectureName.toString());

                                mySocketIO.emit("requestCurrentStudentAttendanceState", new IOAcknowledge() {
                                    @Override
                                    public void ack(Object... objects) {
                                        try {
                                            JSONArray jsonArray = new JSONArray(objects[0].toString());
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                String stat = jsonArray.getJSONObject(i).getString("Stat");
                                                String lname = jsonArray.getJSONObject(i).getString("Lname");
                                                String pname = jsonArray.getJSONObject(i).getString("Pname");

//                            currentAttendanceStateForStudentAdapter.add(new CurrentAttendanceStateForStudentBean(stat, lname, pname));
                                                currentAttendanceStatePiece.add(new CurrentAttendanceStateForStudentBean(stat, lname, pname));
                                                Log.i("출결현황", "현황:" + stat + ", 강의명:" + lname + ", 교수명:" + pname);
                                            }

                                            Log.i("후 돌겠네 강의실 갯수는 ?", lectureName.toArray().length + "");

                                            for (int i = 0; i < lectureName.toArray().length; i++) {
                                                Log.i("아이거 뭐야", "ㄷㄷㄷㄷ");
                                                int attendance = 0;
                                                int lateness = 0;
                                                int absence = 0;

                                                String pname = null;

                                                Log.i("조각수는 몇개 ? ", currentAttendanceStatePiece.size() + "");

                                                for (int j = 0; j < currentAttendanceStatePiece.size(); j++) {
                                                    Log.i("같은감 ?", currentAttendanceStatePiece.get(j).getLname().toString() + "..." + lectureName.toArray()[i] + "");
                                                    if (currentAttendanceStatePiece.get(j).getLname().equals(lectureName.toArray()[i])) {
                                                        String stat = currentAttendanceStatePiece.get(j).getStat();
                                                        pname = currentAttendanceStatePiece.get(j).getPname();

                                                        if (stat.equals("출석")) {
                                                            attendance++;
                                                        } else if (stat.equals("지각")) {
                                                            lateness++;
                                                        } else {
                                                            absence++;
                                                        }
                                                    }
                                                }

                                                currentAttendanceStateForStudentAdapter.add(new CurrentAttendanceStateForStudentBean(lectureName.toArray()[i].toString(), pname, attendance, lateness, absence));
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                listView.setAdapter(currentAttendanceStateForStudentAdapter);
                                            }
                                        });
                                    }
                                }, id);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, object);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
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

                    }
                });
            }
        }).start();
    }

    @Override
    public void onError(SocketIOException e) {

    }
}
