package aas.project.tera.com.autoattendancesystem.notice;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import aas.project.tera.com.autoattendancesystem.MyRECOBeaconManager;
import aas.project.tera.com.autoattendancesystem.MySocketIO;
import aas.tera.com.autoattendancesystem.R;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;

/**
 * Created by Administrator on 2015-05-09.
 */

public class NoticeAdapter extends BaseAdapter implements IOCallback {

    ArrayList<NoticeItem> noticeItems;

    String Lname;
    String Lno;
    int Nno;
    String identity;

    private MySocketIO mySocketIO;
    NoticeActivity newNotice;



    NoticeAdapter(String identity) throws URISyntaxException, MalformedURLException {
        noticeItems = new ArrayList<>();
        this.identity = identity;
        mySocketIO = new MySocketIO(this);
        newNotice = null;
    }

    @Override
    public int getCount() {
        return noticeItems.size();
    }

    @Override
    public Object getItem(int position) {
        return noticeItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();
        newNotice = (NoticeActivity) context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.notice_item, parent, false);

        TextView lectureName = (TextView)convertView.findViewById(R.id.lecture_name);
        TextView noticeTitle = (TextView)convertView.findViewById(R.id.notice_title);
        TextView writer = (TextView)convertView.findViewById(R.id.writer);
        TextView date = (TextView)convertView.findViewById(R.id.date);

        TextView notice_modify = (TextView)convertView.findViewById(R.id.btn_notice_modify);
        TextView notice_delete = (TextView)convertView.findViewById(R.id.btn_notice_delete);


        lectureName.setText(noticeItems.get(pos).getLectureName());
        noticeTitle.setText(noticeItems.get(pos).getNoticeTitle());
        writer.setText(noticeItems.get(pos).getWriter());
        date.setText(noticeItems.get(pos).getDate());

        notice_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newDialog(noticeItems.get(pos).getNno(), Lno, pos, context).show();
            }
        });

        if(this.identity.equals("student")) {
//            notice_modify.setText("");
//            notice_modify.setBackgroundColor(999);
            notice_modify.setVisibility(View.INVISIBLE);
            notice_delete.setVisibility(View.INVISIBLE);
//            notice_delete.setText("");
//            notice_delete.setBackgroundColor(999);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Send single item click data to SingleItemView Class
                Intent intent = new Intent(context, SingleNoticeView.class);
                // Pass all data Nno
                intent.putExtra("Nno", noticeItems.get(pos).getNno());
                // Pass all data lecturename
                intent.putExtra("lecturename", (noticeItems.get(pos).getLectureName()));
                // Pass all data noticetitle
                intent.putExtra("noticetitle", (noticeItems.get(pos).getNoticeTitle()));
                // Pass all data witer
                intent.putExtra("witer", noticeItems.get(pos).getWriter());
                // Pass all data body
                intent.putExtra("body", noticeItems.get(pos).getBody());
                // Pass all data date
                intent.putExtra("date", noticeItems.get(pos).getDate());
                // Pass identity
                intent.putExtra("identity", "student");
                // Start SingleItemView Class
                context.startActivity(intent);
            }
        });
        // Listen for notice_modify Item Click
        notice_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Send single item click data to SingleItemView Class
                Intent intent = new Intent(context, SingleNoticeView.class);
                // Pass all data Nno
                intent.putExtra("Nno", noticeItems.get(pos).getNno());
                // Pass all data lecturename
                intent.putExtra("lecturename", (noticeItems.get(pos).getLectureName()));
                // Pass all data noticetitle
                intent.putExtra("noticetitle", (noticeItems.get(pos).getNoticeTitle()));
                // Pass all data witer
                intent.putExtra("witer", noticeItems.get(pos).getWriter());
                // Pass all data body
                intent.putExtra("body", noticeItems.get(pos).getBody());
                // Pass all data date
                intent.putExtra("date", noticeItems.get(pos).getDate());
                // Pass identity
                intent.putExtra("identity", identity);
                // Start SingleItemView Class
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    private Dialog newDialog(final int Nno , final String Lno, final int pos, final Context context) {
        return new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.alert_dialog_two_buttons_title)
                .setNegativeButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mySocketIO.emit("professorNoticeDelete", new IOAcknowledge() {
                            @Override
                            public void ack(Object... objects) {
                                final NoticeActivity test = (NoticeActivity) MyRECOBeaconManager.aQueryMap.get(NoticeActivity.class);

//                                test.handler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        for (int i = 0; i < test.courseN; i++) {
//                                            test.getnoticeadapter()[i].notifyDataSetChanged();
//                                        }
//                                    }
//                                });
//                                ((ListView)test.findViewById(R.id.notice_list)).deferNotifyDataSetChanged();
                                //test.recreate();
//                                newNotice.noticeAdapter[0].remove(pos);
//                                newNotice.noticeAdapter[0].notifyDataSetChanged();
                            }
                        },Nno);
                        newNotice.recreate();
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                })
                .create();

    }
    public void add(NoticeItem item){
        noticeItems.add(item);
    }

    public void remove(int position){
        noticeItems.remove(position);
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
