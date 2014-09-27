package com.oakraw.gmap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.oakraw.gmap.adapter.RecordAdapter;
import com.oakraw.gmap.model.Record;

import java.util.ArrayList;


public class MenuActivity extends Activity {

    private AlertDialog.Builder alert;
    private EditText inputRecord;
    private Database database;
    private ArrayList<Record> recordsList;
    public static ListView listView;
    public static RecordAdapter recordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        getActionBar().setIcon(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        listView = (ListView) findViewById(R.id.listView);
        database = new Database(this);
        recordsList = database.getAllRecords();
        recordAdapter = new RecordAdapter(this,recordsList);
        listView.setAdapter(recordAdapter);



    }

    @Override
    protected void onResume() {
        super.onResume();
        recordsList = database.getAllRecords();
        recordAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_new) {
            Intent intent = new Intent(this,MainActivity.class);
            showDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog(){
        alert = new AlertDialog.Builder(this);

        alert.setTitle("New Record");
        inputRecord = new EditText(this);
        inputRecord.setHint("Enter Record Name");
        alert.setView(inputRecord);
        /*FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        layoutParams.setMargins(px,px,px,px);
        inputRecord.setLayoutParams(layoutParams);*/

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(!inputRecord.getText().toString().equals("")) {
                    String value = inputRecord.getText().toString();
                    Log.d("myTAG", value);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("name", value);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please enter record name", Toast.LENGTH_LONG).show();

                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();

    }
}
