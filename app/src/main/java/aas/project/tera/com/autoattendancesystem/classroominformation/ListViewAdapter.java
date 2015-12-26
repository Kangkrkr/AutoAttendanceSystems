package aas.project.tera.com.autoattendancesystem.classroominformation;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aas.tera.com.autoattendancesystem.R;

/**
 * Created by HeeJeong on 2015-06-02.
 */
public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<KITClassRoom> kitclassroomlist = null;
    private ArrayList<KITClassRoom> arraylist;

    public ListViewAdapter(Context context, List<KITClassRoom> kitclassroomlist) {
        mContext = context;
        this.kitclassroomlist = kitclassroomlist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<KITClassRoom>();
        this.arraylist.addAll(kitclassroomlist);
    }

    public class ViewHolder {
        TextView building;
        TextView classroom;
        TextView state;
        RelativeLayout bg;
    }

    @Override
    public int getCount() {
        return kitclassroomlist.size();
    }

    @Override
    public KITClassRoom getItem(int position) {
        return kitclassroomlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.classroom_item, null);
            // Locate the TextViews in classroom_item.xmll
            holder.building = (TextView) view.findViewById(R.id.building);
            holder.classroom = (TextView) view.findViewById(R.id.classroom);
            holder.state = (TextView) view.findViewById(R.id.state);

            holder.bg = (RelativeLayout) view.findViewById(R.id.classroomlayout);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.building.setText(kitclassroomlist.get(position).getBuilding());
        holder.classroom.setText(kitclassroomlist.get(position).getClassroom());
        holder.state.setText(kitclassroomlist.get(position).getState());


        if(holder.state.getText().equals("class")){
            holder.bg.setBackgroundColor(0xddff99cc);
        }
        else if(holder.state.getText().equals("none")){
            holder.bg.setBackgroundColor(0xdd99ccff);
        }


        // Listen for ListView Item Click
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Send single item click data to SingleItemView Class
                Intent intent = new Intent(mContext, SingleItemView.class);
                // Pass all data classroom
                intent.putExtra("classroom",(kitclassroomlist.get(position).getClassroom()));
                // Pass all data state
                intent.putExtra("state",(kitclassroomlist.get(position).getState()));
                // Pass all data flag
                // Start SingleItemView Class
                mContext.startActivity(intent);
            }
        });

        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        kitclassroomlist.clear();

        if (charText.length() == 0) {
            kitclassroomlist.addAll(arraylist);
        }
        else
        {
            for (KITClassRoom wp : arraylist)
            {
                if (wp.getClassroom().toLowerCase(Locale.getDefault()).contains(charText) ||
                        wp.getState().toLowerCase(Locale.getDefault()).contains(charText))
                {

                    kitclassroomlist.add(wp);

                }
            }
        }
        notifyDataSetChanged();
    }

}
