package aas.project.tera.com.autoattendancesystem;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.material.widget.FloatingEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import aas.project.tera.com.autoattendancesystem.classroominformation.ClassRoomInfo;
import aas.project.tera.com.autoattendancesystem.notice.NoticeActivity;
import aas.project.tera.com.autoattendancesystem.notice.SingleNoticeView;
import aas.tera.com.autoattendancesystem.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;


public class CourseListActivity extends ActionBarActivity implements IOCallback {


    /*
    private RECOBeaconManager recoBeaconManager;
    private MyRECOBeaconManager myRECOBeaconManager;
    */
    public static boolean isActivityStarted = false;

    private MyRECOBeaconManager myRECOBeaconManager;
    private MySocketIO mySocketIO;
    private AQuery aQuery;
    private Handler handler = new Handler();
    private Map<String, String> map = MapRegister.getInstance();

    @InjectView(R.id.name) TextView name;
    @InjectView(R.id.class_number) TextView classNumber;
    @InjectView(R.id.dl_activity_main) DrawerLayout dlDrawer;
    @InjectView(R.id.vp_guidePages) ViewPager viewPager;
    @InjectView(R.id.vp_viewGroup) ViewGroup viewPoints;    // layout for small points

    private ActionBarDrawerToggle dtToggle;

    private ArrayList<View> pageViews =  new ArrayList<View>();	    // pages view
    private ImageView imageView;                                    // image => small white and black point
    private ImageView[] imageViews;
    private ExpandableHeightGridView gridview;          // time table

    public static int coursecN;

    private ScrollView sv;

    public ArrayList<String> myCourseList = new ArrayList<String>();

    private LectureInfoBean lectureInfoBean;

    // 6.10 추가
    private BackPressCloseHandler backPressCloseHandler;
    private Context context;
    LoginActivity error = null;
    private Date date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        ButterKnife.inject(this);
        error = (LoginActivity) MyRECOBeaconManager.aQueryMap.get(LoginActivity.class);
        context=this;
        try {
            aQuery = new AQuery(this);
            MyRECOBeaconManager.putAQuery(CourseListActivity.class, this);
            mySocketIO = new MySocketIO(this);

            setInfo();
            initDrawerAndViewPager();
            getLectureList();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String getID(){
        return getIntent().getStringExtra("id");
    }

    private String getPWD(){
        return getIntent().getStringExtra("pwd");
    }

    public String getIdentity() { return getIntent().getStringExtra("identity"); }

    private synchronized void initDrawerAndViewPager() {
        // 6.10
        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

        };
        dlDrawer.setDrawerListener(dtToggle);

        // 액션바 토글(왼쪽 상단)으로 드로어 이용
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LayoutInflater inflater = LayoutInflater.from(aQuery.getContext());

        // View 타입을 받을수 있는 ArrayList에 뷰들을 추가한다.
        pageViews.add(inflater.inflate(R.layout.show_lecture_info, null));
        pageViews.add(inflater.inflate(R.layout.bannar, null));
//        pageViews.add(inflater.inflate(R.layout.page02, null));
//        pageViews.add(inflater.inflate(R.layout.page03, null));

        // 뷰페이저 밑의 점들을 위한 이미지뷰 배열
        // save small points into array
        imageViews = new ImageView[pageViews.size()];

        // set images
        for(int i=0;i<pageViews.size();i++){
            imageView = new ImageView(aQuery.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(40,40));
            imageView.setPadding(20, 0, 20, 0);
            imageViews[i] = imageView;

            if(i==0){
                imageViews[i].setBackgroundResource(R.drawable.circle_white);
            }else{
                imageViews[i].setBackgroundResource(R.drawable.circle_grey);
            }

            viewPoints.addView(imageViews[i]);
        }
        // set page adapter and listener
        viewPager.setAdapter(new GuidePageAdapter(pageViews, this));
        viewPager.setOnPageChangeListener(new GuidePageChangeListener(imageViews));
    }

    private synchronized void getLectureList() {
        // 로그인 액티비티에서 로그인 정보를 받아온다.
        String id = getID();
        String pwd = getPWD();

        JSONObject object = new JSONObject();
        try {
            // 받아온 정보를 JSONObject로 만든다.
            object.put("id", id).put("pwd", pwd);
            // 가공한 JSONObject를 서버로 보내고, 서버가 보내온 응답을 IOAcknowledge 객체를 통해 받는다.
            if(getIdentity().equals("student")){
                mySocketIO.emit("getStudentCourseList", new IOAcknowledge() {
                    @Override
                    public void ack(Object... objects) {
                        try {
                            final String message = objects[0].toString();
                            final JSONArray jsonArray = new JSONArray(message);

                            setDataToListView(jsonArray);       // 새로운 쓰레드를 통해 UI를 제어한다.
                            startLectureListMatching();
                        } catch (JSONException e) { e.printStackTrace(); }
                    }
                }, object);
            } else {
                mySocketIO.emit("getProfessorCourseList", new IOAcknowledge() {
                    @Override
                    public void ack(Object... objects) {
                        try {
                            final String message = objects[0].toString();
                            final JSONArray jsonArray = new JSONArray(message);

                            setDataToListView(jsonArray);       // 새로운 쓰레드를 통해 UI를 제어한다.
                            startLectureListMatching();
                        } catch (JSONException e) { e.printStackTrace(); }
                    }
                }, object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private synchronized void setInfo(){
        // 서버에 로그인시 io.set() 으로 설정해두었던 값을 가지고와서 설정하도록......
        final String id = getID();

        aQuery.id(R.id.left_layout).getView().findViewById(R.id.btn_attendance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String identity = getIdentity();
                if(identity.equals("student")){
                    Intent intent = new Intent(aQuery.getContext(), CurrentAttendanceStateForStudentActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(aQuery.getContext(), CurrentAttendanceStateForProfessorActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            }
        });

        if(getIdentity().equals("student")){
            mySocketIO.emit("studentLogin", new IOAcknowledge() {
                @Override
                public void ack(Object... objects) {
                    try {
                        final String message = objects[0].toString();
                        final JSONArray jsonArray = new JSONArray(message);

                        final String studentName = jsonArray.getJSONObject(0).getString("Sname");
                        final String studentClassNumber = jsonArray.getJSONObject(0).getString("Sno");

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(studentName.equals("null")){
                                    Toast.makeText(context,"아이디 오류 입니다. 다시 실행하십시오.",Toast.LENGTH_SHORT).show();


                                    ((FloatingEditText)error.findViewById(R.id.id)).setText("");
                                    ((FloatingEditText)error.findViewById(R.id.password)).setText("");

                                    error.recreate();

                                    recreate();

                                    name.setText("null");
                                    classNumber.setText("null");

                                    finish();
                                }
                                name.setText(studentName);
                                classNumber.setText(studentClassNumber);
                            }
                        });

//                        name.setText(studentName);
//                        classNumber.setText(studentClassNumber);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, id);
            mySocketIO.emit("getNewNoticeList", new IOAcknowledge() {
                @Override
                public void ack(Object... objects) {
                    try {
                        final String message = objects[1].toString();
                        final JSONArray newnoticejsonArray = new JSONArray(message);

                        final JSONArray coursesjsonArray = new JSONArray(objects[0].toString());
                        coursecN = coursesjsonArray.length();
                        int Nno = 0;
                        String Lname = null;
                        String Pname = null;
                        String Ntitle = null;
                        String Nbody = null;
                        String Ndate = null;

                        for (int i = 0; i < 3; i++) {

                            try {
                                Nno = newnoticejsonArray.getJSONObject(i).getInt("Nno");
                                Lname = newnoticejsonArray.getJSONObject(i).getString("Lname");
                                Ntitle = newnoticejsonArray.getJSONObject(i).getString("Ntitle");
                                Pname = newnoticejsonArray.getJSONObject(i).getString("Pname");
                                Ndate = newnoticejsonArray.getJSONObject(i).getString("Ndate");
                                Nbody = newnoticejsonArray.getJSONObject(i).getString("Nbody");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(i==0) {
                                aQuery.id(R.id.noticebanner1).id(R.id.lecture_name1).text(Lname);
                                aQuery.id(R.id.noticebanner1).id(R.id.notice_title1).text(Ntitle);
                                aQuery.id(R.id.noticebanner1).id(R.id.writer1).text(Pname);
                                aQuery.id(R.id.noticebanner1).id(R.id.date1).text(Ndate);
                                final int finalNno = Nno;
                                final String finalLname = Lname;
                                final String finalNtitle = Ntitle;
                                final String finalPname = Pname;
                                final String finalNbody = Nbody;
                                final String finalNdate = Ndate;
                                aQuery.id(R.id.noticebanner1).clicked(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Send single item click data to SingleItemView Class
                                        Intent intent = new Intent(context, SingleNoticeView.class);
                                        // Pass all data Nno
                                        intent.putExtra("Nno", finalNno);
                                        // Pass all data lecturename
                                        intent.putExtra("lecturename", finalLname);
                                        // Pass all data noticetitle
                                        intent.putExtra("noticetitle", finalNtitle);
                                        // Pass all data witer
                                        intent.putExtra("witer", finalPname);
                                        // Pass all data body
                                        intent.putExtra("body", finalNbody);
                                        // Pass all data date
                                        intent.putExtra("date", finalNdate);
                                        // Pass identity
                                        intent.putExtra("identity", "student");
                                        // Start SingleItemView Class
                                        context.startActivity(intent);
                                    }
                                });
                            }
                            else if(i==1) {
                                aQuery.id(R.id.noticebanner2).id(R.id.lecture_name2).text(Lname);
                                aQuery.id(R.id.noticebanner2).id(R.id.notice_title2).text(Ntitle);
                                aQuery.id(R.id.noticebanner2).id(R.id.writer2).text(Pname);
                                aQuery.id(R.id.noticebanner2).id(R.id.date2).text(Ndate);
                                final int finalNno = Nno;
                                final String finalLname = Lname;
                                final String finalNtitle = Ntitle;
                                final String finalPname = Pname;
                                final String finalNbody = Nbody;
                                final String finalNdate = Ndate;
                                aQuery.id(R.id.noticebanner2).clicked(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Send single item click data to SingleItemView Class
                                        Intent intent = new Intent(context, SingleNoticeView.class);
                                        // Pass all data Nno
                                        intent.putExtra("Nno", finalNno);
                                        // Pass all data lecturename
                                        intent.putExtra("lecturename", finalLname);
                                        // Pass all data noticetitle
                                        intent.putExtra("noticetitle", finalNtitle);
                                        // Pass all data witer
                                        intent.putExtra("witer", finalPname);
                                        // Pass all data body
                                        intent.putExtra("body", finalNbody);
                                        // Pass all data date
                                        intent.putExtra("date", finalNdate);
                                        // Pass identity
                                        intent.putExtra("identity", "student");
                                        // Start SingleItemView Class
                                        context.startActivity(intent);
                                    }
                                });
                            }
                            else{
                                aQuery.id(R.id.noticebanner3).id(R.id.lecture_name3).text(Lname);
                                aQuery.id(R.id.noticebanner3).id(R.id.notice_title3).text(Ntitle);
                                aQuery.id(R.id.noticebanner3).id(R.id.writer3).text(Pname);
                                aQuery.id(R.id.noticebanner3).id(R.id.date3).text(Ndate);
                                final int finalNno = Nno;
                                final String finalLname = Lname;
                                final String finalNtitle = Ntitle;
                                final String finalPname = Pname;
                                final String finalNbody = Nbody;
                                final String finalNdate = Ndate;
                                aQuery.id(R.id.noticebanner3).clicked(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Send single item click data to SingleItemView Class
                                        Intent intent = new Intent(context, SingleNoticeView.class);
                                        // Pass all data Nno
                                        intent.putExtra("Nno", finalNno);
                                        // Pass all data lecturename
                                        intent.putExtra("lecturename", finalLname);
                                        // Pass all data noticetitle
                                        intent.putExtra("noticetitle", finalNtitle);
                                        // Pass all data witer
                                        intent.putExtra("witer", finalPname);
                                        // Pass all data body
                                        intent.putExtra("body", finalNbody);
                                        // Pass all data date
                                        intent.putExtra("date", finalNdate);
                                        // Pass identity
                                        intent.putExtra("identity", "student");
                                        // Start SingleItemView Class
                                        context.startActivity(intent);
                                    }
                                });
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            },id);

        } else {
            mySocketIO.emit("professorLogin", new IOAcknowledge() {
                @Override
                public void ack(Object... objects) {
                    try {
                        final String message = objects[0].toString();
                        final JSONArray jsonArray = new JSONArray(message);

                        String professorName = jsonArray.getJSONObject(0).getString("Pname");
                        String professorNumber = jsonArray.getJSONObject(0).getString("Pid");

                        name.setText(professorName);
                        classNumber.setText(professorNumber);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, id);
        }
        // 6.10
        backPressCloseHandler = new BackPressCloseHandler(this); // 뒤로가기 이벤트 핸들러
    }


    // 6.10
    // 액션바 토글 동기화, 상태에 따른 작업
    protected void onPostCreate(Bundle saveInstanceState){
        super.onPostCreate(saveInstanceState);
        dtToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dtToggle.onConfigurationChanged(newConfig);
    }

    // 홈 버튼 클릭시 모든 이벤트 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(dtToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        if(dlDrawer.isDrawerOpen(Gravity.LEFT))
            dlDrawer.closeDrawers();

        else
            backPressCloseHandler.onBackPressed();
    }


    // 6.10 끗

    private synchronized void startLectureListMatching() {
        // 타이머는 cancel() : 취소, purge() : 제거의 방법을 사용한다.
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        Date date = new Date();
                        String curPeriod = (map.get(String.valueOf(date.getHours())) == null) ? "" : map.get(String.valueOf(date.getHours())) + "교시";
                        aQuery.id(R.id.current_time).text(map.get(String.valueOf(date.getDay())) + "요일 "
                                + date.getHours() + "시 " + date.getMinutes() + "분 " + curPeriod);
                    }
                });
            }
        }, 1000, 1000 * 10);    // 5분 마다..
        for(int i=0,j=0;i<3;i++,j++) {
            if (!classNumber.getText().toString().equals(getID())){
                i=0;
            }
            if(classNumber.getText().toString().equals("null")){
                finish();
            }
            if(j==1000000){
                j=0;this.recreate();
            }
        }
    }

    private synchronized void setDataToListView(final JSONArray jsonArray) {
        new AsyncTask<JSONArray, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(JSONArray... params) {

                date = new Date();

                String identity  = getIdentity();

                sv = (ScrollView)findViewById(R.id.main_scroll);

                if(identity.equals("student")){
                    for (int i = 0; i < params[0].length(); i++) {
                        try {
                            String lcode = params[0].getJSONObject(i).getString("Lcode");
                            int dev = params[0].getJSONObject(i).getInt("Dev");
                            String lname = params[0].getJSONObject(i).getString("Lname");
                            String pid = params[0].getJSONObject(i).getString("Pid");
                            String lday = params[0].getJSONObject(i).getString("Lday");
                            String lperiod = params[0].getJSONObject(i).getString("Lperiod");
                            String rname = params[0].getJSONObject(i).getString("Rname");
                            int lno = params[0].getJSONObject(i).getInt("Lno");

                            try{
                                if(map.get(String.valueOf(date.getDay())).equals(lday) && map.get(String.valueOf(date.getHours())).equals(lperiod)){
                                    myCourseList.add(lname+" "+lday +" "+lperiod+" "+rname);
                                    lectureInfoBean = new LectureInfoBean(lcode, dev, lname, pid, lday, lperiod, rname, lno);
                                }
                            }catch(NullPointerException e){
                                Toast.makeText(aQuery.getContext(), "처리중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                } else {
                    for (int i = 0; i < params[0].length(); i++) {
                        try {
                            // Lname, Lday, Lperiod, Rname
                            String lcode = params[0].getJSONObject(i).getString("Lcode");
                            int dev = params[0].getJSONObject(i).getInt("Dev");
                            String lname = params[0].getJSONObject(i).getString("Lname");
                            String pid = params[0].getJSONObject(i).getString("Pid");
                            String lday = params[0].getJSONObject(i).getString("Lday");
                            String lperiod = params[0].getJSONObject(i).getString("Lperiod");
                            String rname = params[0].getJSONObject(i).getString("Rname");
                            int lno = params[0].getJSONObject(i).getInt("Lno");

                            if(map.get(String.valueOf(date.getDay())).equals(lday) && map.get(String.valueOf(date.getHours())).equals(lperiod)){
                                myCourseList.add(lname+" "+lday +" "+lperiod+" "+rname);
                                lectureInfoBean = new LectureInfoBean(lcode, dev, lname, pid, lday, lperiod, rname, lno);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {

                if(!result){
                    Toast.makeText(context, "데이터를 받아오는데 실패했습니다.", Toast.LENGTH_LONG).show();
                }

                aQuery.id(R.id.current_time).text(map.get(String.valueOf(date.getDay())) + "요일 "
                        + date.getHours() + "시 " + date.getMinutes() + "분 " + map.get(String.valueOf(date.getHours())) + "교시");

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(aQuery.getContext(), android.R.layout.simple_list_item_1, myCourseList);
                aQuery.id(R.id.listView).adapter(arrayAdapter);

                if(aQuery.id(R.id.listView).getListView().getAdapter().getCount() <= 0){
                    aQuery.id(R.id.non_exists_lecture).visibility(View.VISIBLE);
                    aQuery.id(R.id.listView).visibility(View.INVISIBLE);
                    aQuery.id(R.id.left_layout).getView().findViewById(R.id.btn_check).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(aQuery.getContext(), "현재 강의가 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    aQuery.id(R.id.non_exists_lecture).text("현재 수업중인 강의");
                    aQuery.id(R.id.listView).visibility(View.VISIBLE);
                    aQuery.id(R.id.left_layout).getView().findViewById(R.id.btn_check).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(getIdentity().equals("student")){
                                Intent intent = new Intent(aQuery.getContext(), AttendanceActivity.class);
                                intent.putExtra("data", lectureInfoBean);
                                intent.putExtra("id", getID());
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(aQuery.getContext(), AttendanceBookActivity.class);
                                intent.putExtra("data", lectureInfoBean);
                                intent.putExtra("id", getID());
                                startActivity(intent);
                            }
                        }
                    });
                }

                // 6.10 setAdapter까지

                String Nday = map.get(String.valueOf(date.getDay()));
                String Ntime = map.get(String.valueOf(date.getHours()));

                gridview = (ExpandableHeightGridView) findViewById(R.id.gridview);
                gridview.setExpanded(true);
                final GridAdapter adapter = new GridAdapter(aQuery.getContext(), R.layout.grid_item, jsonArray, Nday, Ntime);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        gridview.setAdapter(adapter);
                    }
                });

                adapter.notifyDataSetChanged();

                sv.smoothScrollTo(0, 0);

                //공지사항 버튼 클릭시
                aQuery.id(R.id.left_layout).getView().findViewById(R.id.btn_note).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getIdentity().equals("student")) {
                            Intent intent = new Intent(aQuery.getContext(), NoticeActivity.class);
                            intent.putExtra("data", lectureInfoBean);
                            intent.putExtra("id", getID());
                            intent.putExtra("identity", getIdentity());
                            intent.putExtra("name", name.getText());
                            intent.putExtra("number", classNumber.getText());
                            dlDrawer.closeDrawers();
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(aQuery.getContext(), NoticeActivity.class);
                            intent.putExtra("data", lectureInfoBean);
                            intent.putExtra("id", getID());
                            intent.putExtra("identity", getIdentity());
                            intent.putExtra("name", name.getText());
                            intent.putExtra("number", classNumber.getText());
                            dlDrawer.closeDrawers();
                            startActivity(intent);
                        }
                    }
                });

                //전체강의실 버튼 클릭시
                aQuery.id(R.id.left_layout).getView().findViewById(R.id.btn_classroom).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(aQuery.getContext(), ClassRoomInfo.class);
                        intent.putExtra("data", lectureInfoBean);
                        intent.putExtra("id", getID());
                        intent.putExtra("identity", getIdentity());
                        intent.putExtra("name", name.getText());
                        intent.putExtra("number", classNumber.getText());
                        dlDrawer.closeDrawers();
                        startActivity(intent);
                    }
                });

                //홈버튼 클릭시
                aQuery.id(R.id.left_layout).getView().findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(aQuery.getContext(), CourseListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        dlDrawer.closeDrawers();
                        startActivity(intent);
                    }
                });

            }
        }.execute(jsonArray);
    }

    public synchronized LectureInfoBean getLectureInfoBean() {
        return lectureInfoBean;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }


    @Override
    public void on(String s, IOAcknowledge ioAcknowledge, Object... objects) {

    }

    @Override
    public void onDisconnect() {}

    @Override
    public void onError(SocketIOException e) { }

    @Override
    public void onConnect() {}

    @Override
    public void onMessage(String s, IOAcknowledge ioAcknowledge) {}

    @Override
    public void onMessage(JSONObject jsonObject, IOAcknowledge ioAcknowledge) {}
}
