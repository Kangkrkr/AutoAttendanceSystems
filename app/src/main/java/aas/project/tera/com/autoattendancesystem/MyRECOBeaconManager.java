package aas.project.tera.com.autoattendancesystem;

import android.app.Activity;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.androidquery.AQuery;
import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconManager;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOBeaconRegionState;
import com.perples.recosdk.RECOErrorCode;
import com.perples.recosdk.RECOMonitoringListener;
import com.perples.recosdk.RECORangingListener;
import com.perples.recosdk.RECOServiceConnectListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import aas.tera.com.autoattendancesystem.R;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;

public class MyRECOBeaconManager implements RECORangingListener, RECOServiceConnectListener, RECOMonitoringListener, IOCallback {

    private Activity activity;
    private AQuery aQuery;
    private MySocketIO mySocketIO;

    private Handler handler = new Handler();

    protected RECOBeaconManager recoBeaconManager;
    protected ArrayList<RECOBeaconRegion> regions;
    public Map<String, String> map = MapRegister.getInstance();
    public static Map<Class<?>, Activity> aQueryMap = new HashMap<>();

    private CourseListActivity courseListActivity = (CourseListActivity) aQueryMap.get(CourseListActivity.class);
//    private AttendanceActivity attendanceActivity = (AttendanceActivity) aQueryMap.get(AttendanceActivity.class);
//    private AttendanceBookActivity attendanceBookActivity = (AttendanceBookActivity)aQueryMap.get(AttendanceBookActivity.class);

    public static final String RECO_UUID = "24DDF411-8CF1-440C-87CD-E368DAF9C93E";

    private final String IN = "들어옴";
    private final String OUT = "나감";

    public MyRECOBeaconManager(Activity activity){
        try {
            this.activity = activity;
            this.aQuery = new AQuery(this.activity);
            this.mySocketIO = new MySocketIO(this);
            recoBeaconManager = RECOBeaconManager.getInstance(activity, true);
            regions = generateBeaconRegionFromServer();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void putAQuery(Class<?> cls, Activity activity){
        aQueryMap.put(cls, activity);
    }


    public void startService(){
        if(!recoBeaconManager.isBound()){
            recoBeaconManager.setRangingListener(this);
            recoBeaconManager.setMonitoringListener(this);
            recoBeaconManager.bind(this);
        }
    }

    private ArrayList<RECOBeaconRegion> generateBeaconRegionFromServer() {
        final ArrayList<RECOBeaconRegion> regions = new ArrayList<>();

        mySocketIO.emit("getBeaconInfos", new IOAcknowledge() {
            @Override
            public void ack(Object... objects) {
                String message = objects[0].toString();
                try {
                    JSONArray jsonArray = new JSONArray(message);
                    for(int i=0; i<jsonArray.length(); i++){
                        int major = jsonArray.getJSONObject(i).getInt("Bmajor");
                        int minor = jsonArray.getJSONObject(i).getInt("Bminor");
                        String rname = jsonArray.getJSONObject(i).getString("Rname");

                        // 강의실에 들어왔는지, 나갔는지 체크용.
                        map.put(rname, OUT);

                        RECOBeaconRegion region = new RECOBeaconRegion(RECO_UUID, major, minor, rname);
                        regions.add(region);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return regions;
    }

    public void tearDownService(){
        for(RECOBeaconRegion region : regions){
            try {
                recoBeaconManager.stopMonitoringForRegion(region);
                recoBeaconManager.stopRangingBeaconsInRegion(region);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<RECOBeacon> recoBeacons, RECOBeaconRegion recoBeaconRegion) {
        ArrayList<RECOBeacon> arrayList = new ArrayList(recoBeacons);

        String identity = courseListActivity.getIdentity();
        String currentLectureRoom = (identity.equals("student")) ? aQuery.id(R.id.show_lecture_room).getText().toString() : aQuery.id(R.id.current_lecture_room).getText().toString();


        if(arrayList != null && arrayList.size() > 0){
            // 비콘은 한개씩만 인식함.
            // 현재 수업을 진행중인 강의실에 입장시 -
            if(currentLectureRoom.equals(recoBeaconRegion.getUniqueIdentifier()) && map.get(currentLectureRoom).equals(OUT)){
                if(identity.equals("student")){
                    NotificationBuilder.doEnterNotify(aQuery, "알림 - 강의실 입장", "출석체크", currentLectureRoom + "에 입장하셨습니다.");
                    aQuery.id(R.id.request_attendance).text("강의실 입장 완료");
                    ((AttendanceActivity) aQueryMap.get(AttendanceActivity.class)).getMySocketIO().emit("studentEntered", currentLectureRoom);
                }else{
                    NotificationBuilder.doEnterNotify(aQuery, "알림 - 강의실 입장", "출석체크", currentLectureRoom + "에 입장하셨습니다.");
                    ((AttendanceBookActivity) aQueryMap.get(AttendanceBookActivity.class)).getMySocketIO().emit("professorEntered", currentLectureRoom);
                }
                // 현재 강의실에 해당하는 데이터를 IN으로 설정한다. (아래 코드도 동일)
                map.put(currentLectureRoom, IN);
            }
        } else{
            // 현재 수업을 진행중인 강의실에 퇴장시 -
            if(currentLectureRoom.equals(recoBeaconRegion.getUniqueIdentifier()) && map.get(currentLectureRoom).equals(IN)){
                if(identity.equals("student")){
                    NotificationBuilder.doExitNotify(aQuery, aQuery.id(R.id.show_lecture_room).getText().toString(), "퇴장 알림", "강의실 나감", aQuery.id(R.id.show_lecture_room).getText().toString() + "을 나감.");
                }else{
                    NotificationBuilder.doExitNotify(aQuery, aQuery.id(R.id.show_lecture_room).getText().toString(), "퇴장 알림", "강의실 나감", aQuery.id(R.id.show_lecture_room).getText().toString() + "을 나감.");
                }

                map.put(currentLectureRoom, OUT);
            }
        }
    }

    @Override
    public void rangingBeaconsDidFailForRegion(RECOBeaconRegion recoBeaconRegion, RECOErrorCode recoErrorCode) {

    }

    @Override
    public void onServiceConnect() {
        for(RECOBeaconRegion region : regions) {
            try {
                recoBeaconManager.startRangingBeaconsInRegion(region);
                recoBeaconManager.startMonitoringForRegion(region);
            } catch (RemoteException e) {
                Log.i("RECORangingActivity", "Remote Exception");
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.i("RECORangingActivity", "Null Pointer Exception");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onServiceFail(RECOErrorCode recoErrorCode) {

    }

    /* 아래 4개 함수는 RECOMonitoringListener 인터페이스의 함수들이다. */

    // 단말기가 비콘 신호를 최초 인식시 호출됨.
    // enter와 exit 이벤트는 didBeaconInRegion 에서 각각 처리하였으므로 , 사용하지 않는다.
    @Override
    public void didEnterRegion(RECOBeaconRegion recoBeaconRegion, Collection<RECOBeacon> collection) {

    }

    @Override
    public void didExitRegion(RECOBeaconRegion recoBeaconRegion) {}

    @Override
    public void didStartMonitoringForRegion(RECOBeaconRegion recoBeaconRegion) {}

    @Override
    public void didDetermineStateForRegion(RECOBeaconRegionState recoBeaconRegionState, RECOBeaconRegion recoBeaconRegion) {
//        if (recoBeaconRegionState.toString().equals("RECOBeaconRegionInside")){}
    }


    @Override
    public void monitoringDidFailForRegion(RECOBeaconRegion recoBeaconRegion, RECOErrorCode recoErrorCode) {}

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