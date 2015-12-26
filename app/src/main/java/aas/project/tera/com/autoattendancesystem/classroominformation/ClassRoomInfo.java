package aas.project.tera.com.autoattendancesystem.classroominformation;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;

import java.util.ArrayList;
import java.util.Locale;

import aas.project.tera.com.autoattendancesystem.BackPressCloseHandler;
import aas.project.tera.com.autoattendancesystem.CourseListActivity;
import aas.project.tera.com.autoattendancesystem.notice.NoticeActivity;
import aas.tera.com.autoattendancesystem.R;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ClassRoomInfo extends ActionBarActivity {

// Declare Variables
    ListView list;
    ListViewAdapter adapter;
    EditText editsearch;
    String[] building;
    String[] classroom;
    String[] state;
    ArrayList<KITClassRoom> arraylist = new ArrayList<KITClassRoom>();
    private AQuery aQuery;

    /// 추가아아아아
    private ActionBarDrawerToggle dtToggle;
    private BackPressCloseHandler backPressCloseHandler;


    @InjectView(R.id.name) TextView name;
    @InjectView(R.id.class_number) TextView classNumber;
    @InjectView(R.id.dl_activity_main) DrawerLayout dlDrawer;

    public static Activity Classroominfoactivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classroom_main);


        Classroominfoactivity = ClassRoomInfo.this;

        ButterKnife.inject(this);

        aQuery = new AQuery(this);

        initDrawer();

        // Generate sample data
        building = new String[] { "Digital", "Digital", "Digital",
                "Digital", "Digital", "Digital", "Global", "Global",
                "Global", "Techno" };

        classroom = new String[] { "DB127", "DB128", "DB129",
                "D127", "D128", "D129", "G127", "G128",
                "G129", "T127" };

        state = new String[] { "class", "class",
                "none", "class", "none", "none",
                "class", "none", "class", "class" };

        // Locate the ListView in classroom_mainn.xml
        list = (ListView) findViewById(R.id.listview);

        for (int i = 0; i < building.length; i++)
        {
            KITClassRoom wp = new KITClassRoom(building[i], classroom[i],
                    state[i]);
            // Binds all strings into an array
            arraylist.add(wp);
        }

        // Pass results to ListViewAdapter Class
        adapter = new ListViewAdapter(this, arraylist);

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        // Locate the EditText in classroom_mainn.xml
        editsearch = (EditText) findViewById(R.id.search);

        // Capture Text in EditText
        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });
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
        //전체강의실 버튼 클릭시
        aQuery.id(R.id.left_layout).getView().findViewById(R.id.btn_classroom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(aQuery.getContext(), ClassRoomInfo.class);
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

        //공지사항 버튼 클릭시
        aQuery.id(R.id.left_layout).getView().findViewById(R.id.btn_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIdentity().equals("student")) {
                    Intent intent = new Intent(aQuery.getContext(), NoticeActivity.class);
                    intent.putExtra("id", getID());
                    intent.putExtra("identity", getIdentity());
                    intent.putExtra("name", name.getText());
                    intent.putExtra("number", classNumber.getText());
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | intent.FLAG_ACTIVITY_CLEAR_TOP);
                    dlDrawer.closeDrawers();
                    ClassRoomInfo Classroominfoactivity = (ClassRoomInfo) ClassRoomInfo.Classroominfoactivity;
                    Classroominfoactivity.finish();
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(aQuery.getContext(), NoticeActivity.class);
                    intent.putExtra("id", getID());
                    intent.putExtra("identity", getIdentity());
                    intent.putExtra("name", name.getText());
                    intent.putExtra("number", classNumber.getText());
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | intent.FLAG_ACTIVITY_CLEAR_TOP);
                    dlDrawer.closeDrawers();
                    startActivity(intent);
                }
            }
        });

    }
    // Not using options menu in this tutorial
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
