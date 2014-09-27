package com.oakraw.gmap.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.oakraw.gmap.Database;
import com.oakraw.gmap.R;
import com.oakraw.gmap.StatisticActivity;
import com.oakraw.gmap.model.Record;

import java.util.ArrayList;

/**
 * Created by Rawipol on 9/25/14 AD.
 */
public class RecordAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<Record> mRecords;

    public RecordAdapter(Context context, ArrayList<Record> records){
        mContext = context;
        mRecords = records;
    }

    @Override
    public int getCount() {
        return mRecords.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        convertView= LayoutInflater.from(mContext).inflate(R.layout.record_list, null);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StatisticActivity.class);
                intent.putExtra("name",mRecords.get(position).getmName());
                intent.putExtra("route",mRecords.get(position).getmRoute());
                mContext.startActivity(intent);
            }
        });

        ((ImageButton)convertView.findViewById(R.id.deleteBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        final BaseAdapter b = this;

        TextView recordName = (TextView) convertView.findViewById(R.id.recordName);
        recordName.setText(mRecords.get(position).getmName());

        ImageButton deleteBtn = (ImageButton)convertView.findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database db = new Database(mContext);
                db.deleteRecord(mRecords.get(position).getmId());
                mRecords.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
