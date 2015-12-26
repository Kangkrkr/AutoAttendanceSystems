package aas.project.tera.com.autoattendancesystem.classroominformation;

/**
 * Created by HeeJeong on 2015-06-02.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import aas.tera.com.autoattendancesystem.R;

public class SingleItemView extends ActionBarActivity {
    // Declare Variables
    TextView txtclassroom;
    TextView txtstate;
    String classroom;
    String state;
//    private GridView gridview;
//    private ArrayList<GridInfo> data;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.singleitemview);

        View view = (View) findViewById(R.id.singleitemview);

        // Retrieve data from MainActivity on item click event
        Intent i = getIntent();
        // Get the results of classroom
        classroom = i.getStringExtra("classroom");
        // Get the results of state
        state = i.getStringExtra("state");

        // Locate the TextViews in singleitemview.xml
        txtclassroom = (TextView) findViewById(R.id.classroom);
        txtstate = (TextView) findViewById(R.id.state);

        // Load the results into the TextViews
        txtclassroom.setText(classroom);
        txtstate.setText(state);

        if(state.equals("class")){
            txtstate.setTextColor(0xffde1a00);
            view.setBackgroundColor(0x33ff99cc);
        }
        else if(state.equals("none")){
            txtstate.setTextColor(0xff194dc5);
            view.setBackgroundColor(0x3399ccff);
        }


//        gridview = (GridView)findViewById(R.id.gridview_classroom);
//        gridview.setAdapter(new GridAdapter(this, R.layout.grid_item, data));
    }
}
