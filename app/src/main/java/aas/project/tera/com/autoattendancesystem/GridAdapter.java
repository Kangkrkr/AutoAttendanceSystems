package aas.project.tera.com.autoattendancesystem;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import aas.tera.com.autoattendancesystem.R;

public class GridAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<String> data = new ArrayList<>();
    private ArrayList<String> lecture = new ArrayList<>();
    private JSONArray jsonArray;
    private int layout;

    private final int ROWS = 12;
    private final int COLS = 6;

    // 6.10
    int now_index = 0;


    String[] Weekday = {
            //index
            //0   1     2     3     4     5
            " ", "월", "화", "수", "목", "금"
    };

    // (열의갯수 * 해당교시에맞는INDEX) + 해당요일의INDEX
    // 화9 = (6 * 9) + 2 = 56

    String[] Time = {
            //index
            //0   1    2    3    4    5    6    7    8    9   10   11
            " ", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B"
    };


    // 6.10 어댑터 생성자 수정
    public GridAdapter(Context context, int layout, JSONArray jsonArray, String Nday, String Ntime) {
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout=layout;
        this.jsonArray = jsonArray;

        for(int row=0; row<Time.length; row++){
            for(int col=0; col<Weekday.length; col++){
                int index = (COLS * row) + col;

                if(index < 6) { // 요일 표시
                    data.add(Weekday[index]);
                }else if(index%6 == 0) { // 교시 표시

                    // 6.10
                    data.add(Time[index/6]+"교시");
                }else{
                    data.add(" ");
                }
            }
        }

        for(int i=0; i<jsonArray.length(); i++){
            try {
                // (열의갯수 * 해당교시에맞는INDEX) + 해당요일의INDEX
                String lname = jsonArray.getJSONObject(i).getString("Lname");
                String lday = jsonArray.getJSONObject(i).getString("Lday");
                String lperiod = jsonArray.getJSONObject(i).getString("Lperiod");

                int day = 0, period = 0;
                for(int j=0; j<Weekday.length; j++){
                    if(Weekday[j].equals(lday)){
                        day = j;
                        break;
                    }
                }

                for(int k=0; k<Time.length; k++){
                    if(Time[k].equals(lperiod)){
                        period = k;
                        break;
                    }
                }

                int index = (COLS * period) + day;
                data.set(index, lname);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        int nday = 0, nperiod = 0;

        for(int k=0; k<Time.length; k++){
            if(Time[k].equals(Ntime)){

                String nowday = Nday;

                for (int j = 0; j < Weekday.length; j++) {

                    if (Weekday[j].equals(nowday)) {
                        nday += j;
                        break;
                    }


                    nperiod = k;

                }

                now_index = (COLS * nperiod) + nday;


                break;
            }
        }
    }


    // 강의실 시간표
    public GridAdapter(Context context, int layout, JSONArray jsonArray, String Nday, String Ntime, int z) {


        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout=layout;
        this.jsonArray = jsonArray;

        for(int row=0; row<Time.length; row++){
            for(int col=0; col<Weekday.length; col++){
                int index = (COLS * row) + col;

                if(index < 6) { // 요일 표시
                    data.add(Weekday[index]);
                }else if(index%6 == 0) { // 교시 표시

                    // 6.10
                    data.add(Time[index/6]+"교시");
                }else{
                    data.add(" ");
                }
            }
        }

        for(int i=0; i<jsonArray.length(); i++){
            try {
                // (열의갯수 * 해당교시에맞는INDEX) + 해당요일의INDEX
                String lname = jsonArray.getJSONObject(i).getString("Lname");
                String lday = jsonArray.getJSONObject(i).getString("Lday");
                String lperiod = jsonArray.getJSONObject(i).getString("Lperiod");

                int day = 0, period = 0;
                for(int j=0; j<Weekday.length; j++){
                    if(Weekday[j].equals(lday)){
                        day = j;
                        break;
                    }
                }

                for(int k=0; k<Time.length; k++){
                    if(Time[k].equals(lperiod)){
                        period = k;
                        break;
                    }
                }

                int index = (COLS * period) + day;
                data.set(index, lname);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        int nday = 0, nperiod = 0;

        for(int k=0; k<Time.length; k++){
            if(Time[k].equals(Ntime)){

                String nowday = Nday;

                for (int j = 0; j < Weekday.length; j++) {

                    if (Weekday[j].equals(nowday)) {
                        nday += j;
                        break;
                    }


                    nperiod = k;

                }

                now_index = (COLS * nperiod) + nday;


                break;
            }
        }
    }

    @Override
    public int getCount() {
        //return data.size();
        return 72;
    }

    @Override
    public Object getItem(int position) { return data.get(position); }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        MyViewHolder viewHolder;

        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.grid_item, null);

            // 6.10 setLayoutParams 추가
            convertView.setLayoutParams((new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 120)));
            viewHolder = new MyViewHolder();
            viewHolder.mTextView = (TextView)convertView.findViewById(R.id.lec_name);

            if(position<6 || position%6 == 0) {
                convertView.setBackgroundResource(R.drawable.xml_border2);
                // ((TextView)convertView).setTextColor(Color.WHITE);
            }

            // 6.10 if문 추가(현재 시간) 색상 변경
            if(now_index != 0) {
                if (position == now_index)
                    convertView.setBackgroundColor(0x550a82ff);
            }

            convertView.setTag(viewHolder);
            // 아이템이 해당 위치에 잇는지 확인
            // 뽑아낸 데이터의 요일을 확인해서 해당 위치에 텍스트뷰 출력
            // ++ 클릭시 토스트로 강의교실 띄워줌
        } else {
            viewHolder = (MyViewHolder) convertView.getTag();
        }

        viewHolder.mTextView.setText(data.get(position));

        return convertView;
    }
}
