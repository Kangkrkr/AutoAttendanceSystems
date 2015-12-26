package aas.project.tera.com.autoattendancesystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;

import aas.tera.com.autoattendancesystem.R;

public class CurrentAttendanceStateForProfessorAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<CurrentAttendanceStateForProfessorBean> data = new ArrayList<>();
    private JSONArray jsonArray;

    public CurrentAttendanceStateForProfessorAdapter(Context context) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.jsonArray = jsonArray;
    }

    @Override
    public int getCount() {
        //return data.size();
        return data.size();
    }

    @Override
    public Object getItem(int position) { return data.get(position); }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        ProfessorAttendanceViewHolder viewHolder;

        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.professor_attendancebook_item, null);
            viewHolder = new ProfessorAttendanceViewHolder();
            viewHolder.mDate = (TextView)convertView.findViewById(R.id.class_list_time);
            viewHolder.mLname = (TextView)convertView.findViewById(R.id.class_list_name);
            viewHolder.mAttendance = (Button)convertView.findViewById(R.id.class_list_bt_attendance);
            viewHolder.mLateness = (Button)convertView.findViewById(R.id.class_list_bt_lateness);
            viewHolder.mAbsence = (Button)convertView.findViewById(R.id.class_list_bt_absence);

            convertView.setTag(viewHolder);
            // 아이템이 해당 위치에 잇는지 확인
            // 뽑아낸 데이터의 요일을 확인해서 해당 위치에 텍스트뷰 출력
            // ++ 클릭시 토스트로 강의교실 띄워줌
        } else {
            viewHolder = (ProfessorAttendanceViewHolder) convertView.getTag();
        }

        viewHolder.mDate.setText("임의데이터");
        viewHolder.mLname.setText(data.get(position).getLname());
        viewHolder.mAttendance.setText(data.get(position).getAttendance()+"");
        viewHolder.mLateness.setText(data.get(position).getLateness()+"");
        viewHolder.mAbsence.setText(data.get(position).getAbsence()+"");

        return convertView;
    }

    public void add(CurrentAttendanceStateForProfessorBean currentAttendanceStateForProfessorBean){
        data.add(currentAttendanceStateForProfessorBean);
    }
}
