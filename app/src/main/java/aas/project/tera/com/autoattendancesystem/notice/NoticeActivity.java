package aas.project.tera.com.autoattendancesystem.notice;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import aas.project.tera.com.autoattendancesystem.BackPressCloseHandler;
import aas.project.tera.com.autoattendancesystem.CourseListActivity;
import aas.project.tera.com.autoattendancesystem.MyRECOBeaconManager;
import aas.project.tera.com.autoattendancesystem.MySocketIO;
import aas.project.tera.com.autoattendancesystem.classroominformation.ClassRoomInfo;
import aas.tera.com.autoattendancesystem.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;


public class NoticeActivity extends ActionBarActivity implements ActionBar.TabListener, IOCallback {


    public static Activity Noticeactivity;

    public static int courseN;
    Handler handler = new Handler();

    private static SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

//    public ListView noticeList;
    public static NoticeAdapter[] noticeAdapter;

    private MyRECOBeaconManager myRECOBeaconManager;
    private MySocketIO mySocketIO;

    private AQuery aQuery;

    private static String identity;
    private static String id;

    static ActionBar actionBar;

    private ActionBarDrawerToggle dtToggle;
    private BackPressCloseHandler backPressCloseHandler;

    @InjectView(R.id.name)
    TextView name;
    @InjectView(R.id.class_number) TextView classNumber;
    @InjectView(R.id.dl_activity_main)
    DrawerLayout dlDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        Noticeactivity = NoticeActivity.this;

        ButterKnife.inject(this);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        try {
            aQuery = new AQuery(this);
            MyRECOBeaconManager.aQueryMap.put(NoticeActivity.class,this);
            mySocketIO = new MySocketIO(this);

            identity = getIdentity();
            id = getID();

            setInfo();

            myRECOBeaconManager = new MyRECOBeaconManager(this);

            initDrawer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        // Set up the action bar.
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.noticepager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });


        for(int i=-1;i < mSectionsPagerAdapter.getCount(); i++ )
                if( mSectionsPagerAdapter.getCount() == 0 ){
                    i=-5;
                }

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        setInfo_drawer();

    }


    private void initDrawer() {

        // 6.10
        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name) {

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

        backPressCloseHandler = new BackPressCloseHandler(this); // 뒤로가기 이벤트 핸들러
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        if(dlDrawer.isDrawerOpen(Gravity.LEFT))
            dlDrawer.closeDrawers();

        else
            finish();

        //else
        //    backPressCloseHandler.onBackPressed();
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

    private String getID(){    return getIntent().getStringExtra("id");    }
    private String getIdentity() { return getIntent().getStringExtra("identity"); }
    private String getName() {return getIntent().getStringExtra("name");}
    private String getNumber() {return getIntent().getStringExtra("number");}

    private void setInfo_drawer(){
        // 서버에 로그인시 io.set() 으로 설정해두었던 값을 가지고와서 설정하도록......
        final String dl_name = getName();
        final String dl_number = getNumber();

        name.setText(dl_name);
        classNumber.setText(dl_number);
        //공지사항 버튼 클릭시
        aQuery.id(R.id.left_layout).getView().findViewById(R.id.btn_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(aQuery.getContext(), NoticeActivity.class);
                intent.putExtra("id", getID());
                intent.putExtra("identity", getIdentity());
                intent.putExtra("name", name.getText());
                intent.putExtra("number", classNumber.getText());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                dlDrawer.closeDrawers();
                startActivity(intent);
            }
        });
        //홈버튼 클릭시
        aQuery.id(R.id.left_layout).getView().findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(aQuery.getContext(), CourseListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | intent.FLAG_ACTIVITY_CLEAR_TOP);
                dlDrawer.closeDrawers();
                startActivity(intent);
            }
        });

        //전체강의실 버튼 클릭시
        aQuery.id(R.id.left_layout).getView().findViewById(R.id.btn_classroom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(aQuery.getContext(), ClassRoomInfo.class);
                intent.putExtra("id", getID());
                intent.putExtra("identity", getIdentity());
                intent.putExtra("name", name.getText());
                intent.putExtra("number", classNumber.getText());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | intent.FLAG_ACTIVITY_CLEAR_TOP);
                dlDrawer.closeDrawers();
                NoticeActivity noticeactivity = (NoticeActivity) NoticeActivity.Noticeactivity;
                noticeactivity.finish();
                startActivity(intent);
            }
        });

    }


    public void setInfo() throws InterruptedException {
        //
        String id = getID();


        if(getIdentity().equals("student")){
            mySocketIO.emit("getNewNoticeList", new IOAcknowledge() {
                @Override
                public void ack(Object... objects) {
                    try {
                        final String message = objects[1].toString();
                        final JSONArray newnoticejsonArray = new JSONArray(message);

                        final JSONArray coursesjsonArray = new JSONArray(objects[0].toString());
                        courseN = coursesjsonArray.length();

                        mSectionsPagerAdapter.cnt = courseN+1;
                        mSectionsPagerAdapter.course = new String[courseN+1];


                        noticeAdapter = new NoticeAdapter[courseN+1];
                        for (int i = 0; i < courseN+1; i++) {
                            noticeAdapter[i] = new NoticeAdapter(getIdentity());
                        }

                        for (int i = 0; i < newnoticejsonArray.length(); i++) {
                            int Nno = 0;
                            String Lname = null;
                            String Pname = null;
                            String Ntitle = null;
                            String Nbody = null;
                            String Ndate = null;
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

                            noticeAdapter[0].add(new NoticeItem(Nno, Lname, Ntitle, Pname, Ndate.split("T")[0], Nbody));
                        }

                        for(int j=0; j<courseN ; j++) {
                            mSectionsPagerAdapter.course[j] = coursesjsonArray.getJSONObject(j).getString("Lname");
                            for (int i = 0; i < newnoticejsonArray.length(); i++) {
                                int Nno = 0;
                                String Lname = null;
                                String Pname = null;
                                String Ntitle = null;
                                String Nbody = null;
                                String Ndate = null;
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

                                if(Lname.equals(coursesjsonArray.getJSONObject(j).getString("Lname"))) {
                                    noticeAdapter[j + 1].add(new NoticeItem(Nno, Lname, Ntitle, Pname, Ndate.split("T")[0], Nbody));
                                }
                            }
                        }

                        mSectionsPagerAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            },id);
        } else {
            mySocketIO.emit("professorNotice", new IOAcknowledge() {
                @Override
                public void ack(Object... objects) {
                    try {
                        final String message = objects[1].toString();
                        final JSONArray newnoticejsonArray = new JSONArray(message);

                        final JSONArray coursesjsonArray = new JSONArray(objects[0].toString());
                        courseN = coursesjsonArray.length();

                        int tetetew =  newnoticejsonArray.length();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(newnoticejsonArray.length()==0){
                                    Toast.makeText(getApplicationContext(), "등록된 공지사항이 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        mSectionsPagerAdapter.course = new String[courseN];


                        noticeAdapter = new NoticeAdapter[courseN];
                        for (int i = 0; i < courseN; i++) {
                            noticeAdapter[i] = new NoticeAdapter(getIdentity());
                        }
                        String Lno = null;
                        String Lname = null;
                        int Nno = 0;
                        for(int j=0; j<courseN ; j++) {
                            mSectionsPagerAdapter.course[j] = coursesjsonArray.getJSONObject(j).getString("Lname");
                            for (int i = 0; i < newnoticejsonArray.length(); i++) {

                                String Pname = null;
                                String Ntitle = null;
                                String Nbody = null;
                                String Ndate = null;
                                try {
                                    Nno = newnoticejsonArray.getJSONObject(i).getInt("Nno");
                                    Lno = newnoticejsonArray.getJSONObject(i).getString("Lno");
                                    Lname = newnoticejsonArray.getJSONObject(i).getString("Lname");
                                    Ntitle = newnoticejsonArray.getJSONObject(i).getString("Ntitle");
                                    Pname = newnoticejsonArray.getJSONObject(i).getString("Pname");
                                    Ndate = newnoticejsonArray.getJSONObject(i).getString("Ndate");
                                    Nbody = newnoticejsonArray.getJSONObject(i).getString("Nbody");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if(Lname.equals(coursesjsonArray.getJSONObject(j).getString("Lname"))) {
                                    noticeAdapter[j].add(new NoticeItem(Nno, Lname, Ntitle, Pname, Ndate.split("T")[0], Nbody));
                                }
                            }
                            noticeAdapter[j].Nno = Nno;
                            noticeAdapter[j].Lno = coursesjsonArray.getJSONObject(j).getString("Lno");
                            noticeAdapter[j].Lname = coursesjsonArray.getJSONObject(j).getString("Lname");
                            noticeAdapter[j].notifyDataSetChanged();
                        }
                        mSectionsPagerAdapter.cnt = courseN;


                        mSectionsPagerAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            },id);
        }





    }

    /* 별로 필요없는 기능들
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_swipey_tabs, menu);
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
    */


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public int cnt=0;
        public String course[] = new String[12];

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        // 타이틀 (페이지) 의 갯수. 후에 데이터 베이스조회시 갯수를 따로 받아와서 설정이 필요할 것 같다.
        @Override
        public int getCount() {
            // Show 3 total pages.
            return cnt;
        }

        // 각 탭의 타이틀을 설정 (이름 설정)
        @Override
        public CharSequence getPageTitle(int position) {

            if(getIdentity().equals("student")) {
                if (position == 0) {
                    return "최신공지";
                } else if (position > 0) {
                    return course[position - 1];
                } else {
                    return null;
                }
            }
            else{
                return course[position];
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_swipey_tabs, container, false);
            Bundle args = getArguments();
            final int index = args.getInt(ARG_SECTION_NUMBER);
            ((ListView)rootView.findViewById(R.id.notice_list)).setAdapter(noticeAdapter[index]);
            Button noticeadd_bt = (Button)rootView.findViewById(R.id.noticeadd_bt);
            if(identity.equals("professor")) {
//                noticeadd_bt.setPadding(0, 50, 0, 0);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 120);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.setMargins(20, 10, 20, 10);
                noticeadd_bt.setLayoutParams(params);
//                noticeadd_bt.setX(130);
//                noticeadd_bt.setY(20);
                noticeadd_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int i = 0;
                        // Send single item click data to SingleItemView Class
                        Intent intent = new Intent(rootView.getContext(), SingleNoticeView.class);
                        // Pass identity
                        intent.putExtra("identity", "professorwrite");

                        for (i = 0; i < courseN; i++) {
                            if (noticeAdapter[i].Lname.equals(actionBar.getSelectedTab().getText())) {
                                // Pass Lno
                                intent.putExtra("Lno", noticeAdapter[i].Lno);
                                break;
                            }
                        }
                        intent.putExtra("AdapterNum", i);
                        // Pass Pid
                        intent.putExtra("Pid", id);
                        // Start SingleItemView Class
                        rootView.getContext().startActivity(intent);
                    }
                });
                noticeadd_bt.setVisibility(View.VISIBLE);

            }
            else{
                noticeadd_bt.setWidth(0);
                noticeadd_bt.setHeight(0);
                noticeadd_bt.setVisibility(View.INVISIBLE);
            }
            return rootView;
        }
    }

}
