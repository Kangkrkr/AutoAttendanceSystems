package aas.project.tera.com.autoattendancesystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.rey.material.widget.Button;

import java.util.ArrayList;

import aas.tera.com.autoattendancesystem.R;

/**
 * Created by Administrator on 2015-06-02.
 */
public class AttendanceBookAdapter extends BaseAdapter{

    private ArrayList<TakeCourseBean> items = new ArrayList<>();
    private AQuery aQuery;

    public AttendanceBookAdapter(AQuery aQuery){
        this.aQuery = aQuery;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context c = aQuery.id(R.id.item_container).getContext();

        LayoutInflater inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.attendance_book_item, null);

        TextView grade = (TextView)v.findViewById(R.id.grade_title);
        TextView classNumber = (TextView)v.findViewById(R.id.class_number_title);
        TextView name = (TextView)v.findViewById(R.id.name_title);

        Button attendance = (Button)v.findViewById(R.id.attendance);
        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setTag(1);
            }
        });
        Button lateness = (Button)v.findViewById(R.id.lateness);
        lateness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setTag(2);
            }
        });
        Button absence = (Button)v.findViewById(R.id.absence);
        absence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setTag(3);
            }
        });

        grade.setText(String.valueOf(items.get(position).getYear()));
        classNumber.setText(String.valueOf(items.get(position).getSno()));
        name.setText(items.get(position).getSname());

        return v;
    }

    public void addItem(TakeCourseBean takeCourseBean) {
        items.add(takeCourseBean);
    }
}
