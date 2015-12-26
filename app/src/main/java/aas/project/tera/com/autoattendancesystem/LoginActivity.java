package aas.project.tera.com.autoattendancesystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.material.widget.FloatingEditText;
import com.rey.material.widget.Button;

import org.json.JSONObject;

import java.net.MalformedURLException;

import aas.tera.com.autoattendancesystem.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;


public class LoginActivity extends Activity implements IOCallback {

    @InjectView(R.id.id) FloatingEditText inputID;
    @InjectView(R.id.password) FloatingEditText inputPasswod;
    @InjectView(R.id.login_button) Button loginButton;
    @OnClick(R.id.login_button) public void onButtonClick(View view){
        switch(view.getId()){
            case R.id.login_button :
                loginCheck();
                break;
        }
    }

    private MyRECOBeaconManager myRECOBeaconManager;
    private MySocketIO mySocketIO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            ButterKnife.inject(this);
            mySocketIO = new MySocketIO(this);
//            myRECOBeaconManager = new MyRECOBeaconManager(this);
//            myRECOBeaconManager.startService();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void loginCheck(){
        String ID = inputID.getText().toString();
        String PWD = inputPasswod.getText().toString();

        // 로그인시 비콘체크 서비스를 시작함.
//        myRECOBeaconManager.startService();
        if (inputID.getText().toString().matches("\\d{8}") && inputPasswod.getText().toString().matches("[0-9A-Za-z][0-9A-Za-z]{7,11}")) {
            Toast.makeText(getApplicationContext(), "학생으로 로그인 하였습니다.", Toast.LENGTH_SHORT).show();
            showActivity(getApplicationContext(), CourseListActivity.class, ID, PWD, "student");
        } else if (inputID.getText().toString().matches("[A-Z]\\d{5}") && inputPasswod.getText().toString().matches("[0-9A-Za-z][0-9A-Za-z]{7,11}")) {
            Toast.makeText(getApplicationContext(), "교수로 로그인 하였습니다.", Toast.LENGTH_SHORT).show();
            showActivity(getApplicationContext(), CourseListActivity.class, ID, PWD, "professor");
//                    mySocketIO.emit("professorLogin", ID);
        } else {
            inputID.setError("아이디나 패스워드를 올바르게 입력하십시오.");
        }
    }

    public void showActivity(Context context, Class<?> cls, String... strings){
        Intent intent = new Intent(context, cls);
        intent.putExtra("id", strings[0]);
        intent.putExtra("pwd", strings[1]);
        intent.putExtra("identity", strings[2]);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    /*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(RECOBeaconManager.getInstance(this).isBound()){
                RECOBeaconManager.getInstance(this).unbind();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    */

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

