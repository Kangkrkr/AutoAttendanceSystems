//
//package aas.project.tera.com.autoattendancesystem;
//
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothManager;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.RemoteException;
//import android.widget.Button;
//
//import com.perples.recosdk.RECOBeaconManager;
//
//import aas.tera.com.autoattendancesystem.R;
//
//
//public class TestActivity extends Activity {
//
//    private static final int REQUEST_ENABLE_BT = 1;
//    private static final int SEND_PICTURE = 2;
//
//    private Uri pictureUri = null;
//
//    private RECOBeaconManager recoBeaconManager;
//    private MyRECOBeaconManager myRECOBeaconManager;
//
//    private BluetoothManager mBluetoothManager = null;
//    private BluetoothAdapter mBluetoothAdapter = null;
//
//    private Button registPictureButton = null;
//
//    private String path;
//
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
//
//        /*
//        registPictureButton = (Button) findViewById(R.id.registPicture);
//        registPictureButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(Intent.ACTION_PICK,
////                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
////                startActivityForResult(intent, SEND_PICTURE);
//                Intent fintent = new Intent(Intent.ACTION_GET_CONTENT);
//                fintent.setType("image/jpeg");
//                try {
//                    startActivityForResult(fintent, SEND_PICTURE);
//                } catch (ActivityNotFoundException e) {
//                }
//            }
//        });*/
//
//        //사용자가 블루투스를 켜도록 요청합니다.
//        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = mBluetoothManager.getAdapter();
//
//        /* RECOBeaconManager의 인스턴스를 생성합니다.
//           RECOBeaconManager.getInstance(Context)
//           그 후, 매니저에 RangingListener를 등록합니다. */
//        recoBeaconManager = RECOBeaconManager.getInstance(this);
//        myRECOBeaconManager = new MyRECOBeaconManager(this, recoBeaconManager);
//
//        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
//        } else {
//            try {
//                myRECOBeaconManager.doManaging();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        myRECOBeaconManager.stopChecking();
//    }
//
//    /*
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
//            //사용자가 블루투스 요청을 허용하지 않았을 경우, 어플리케이션은 종료됩니다.
//            finish();
//            return;
//        } else {
//            try {
//                myRECOBeaconManager.doManaging();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (requestCode == SEND_PICTURE && resultCode == RESULT_OK) {
//            ImageView studentPicture = (ImageView) findViewById(R.id.studentPicture);
//            path = getPathFromURI(data.getData());
//            studentPicture.setImageURI(data.getData());
//
//            studentPicture.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    createDialog().show();
//                }
//            });
//        }
//    }
//
//    private String getPathFromURI(Uri contentUri) {
//
//        String[] proj = {MediaStore.Images.Media.DATA};
//        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
//        Cursor cursor = loader.loadInBackground();
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }
//
//    public AlertDialog createDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("이 사진으로 등록하시겠습니까?");
//
//        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//
//        return builder.create();
//    }
//    */
//}