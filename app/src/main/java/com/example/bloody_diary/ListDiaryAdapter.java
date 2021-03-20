package com.example.bloody_diary;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ListDiaryAdapter extends BaseAdapter {
    private final long dateStart = 25569;
    private final long dateMultiplier = 86400000;
    private final Context mContext;
    private final List<Diary> newDiaryList;

    public ListDiaryAdapter(Context mContext, List<Diary> reqDiaryList) {
        this.mContext = mContext;
        this.newDiaryList = reqDiaryList;
    }

    @Override
    public int getCount() {
        return newDiaryList.size();
    }

    @Override
    public Object getItem(int position) {
        return newDiaryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return newDiaryList.get(position).get_id();
    }

    public int getItemDate(int position) {

        return newDiaryList.get(position).getMdate();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Float value;
        View v;
        v = View.inflate(mContext, R.layout.data_browser, null);

        TextView tvDate = (TextView) v.findViewById(R.id.tvDate);
        TextView tvBBft = (TextView) v.findViewById(R.id.tvBBft);
        TextView tvABft = (TextView) v.findViewById(R.id.tvABft);
        TextView tvBLun = (TextView) v.findViewById(R.id.tvBLun);
        TextView tvALun = (TextView) v.findViewById(R.id.tvALun);
        TextView tvBDin = (TextView) v.findViewById(R.id.tvBDin);
        TextView tvADin = (TextView) v.findViewById(R.id.tvADin);
        TextView tvAverage = (TextView) v.findViewById(R.id.tvAverage);

        Date date = new Date();
        date.setTime(((newDiaryList.get(position).getMdate()) - dateStart)*dateMultiplier);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        tvDate.setText(sdf.format(date));
        tvBBft.setText(String.valueOf(newDiaryList.get(position).getBbft()));
        tvABft.setText(String.valueOf(newDiaryList.get(position).getAbft()));
        tvBLun.setText(String.valueOf(newDiaryList.get(position).getBlun()));
        tvALun.setText(String.valueOf(newDiaryList.get(position).getAlun()));
        tvBDin.setText(String.valueOf(newDiaryList.get(position).getBdin()));
        tvADin.setText(String.valueOf(newDiaryList.get(position).getAdin()));
        tvAverage.setText(String.valueOf(newDiaryList.get(position).getAverage()));

        v.setTag(newDiaryList.get(position).get_id());

        return v;
    }

    public void updateList(List<Diary> lstItem) {
        newDiaryList.clear();
        newDiaryList.addAll(lstItem);
        this.notifyDataSetChanged();
    }
}
