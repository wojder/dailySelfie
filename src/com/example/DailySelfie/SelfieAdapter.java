package com.example.DailySelfie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by wojder on 24.11.14.
 */
public class SelfieAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<SelfieItem> selfieItems = new ArrayList<SelfieItem>();
    private static LayoutInflater inflater = null;

    private static class ViewHolder {

        ImageView photo;
        TextView name;
    }

    public SelfieAdapter(Context context) {

        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return selfieItems.size();
    }

    @Override
    public Object getItem(int position) {
        return selfieItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View newView = convertView;
        ViewHolder holder;

        SelfieItem curr = selfieItems.get(position);

        if (convertView == null) {

            holder = new ViewHolder();
            newView = inflater.inflate(R.layout.item_list, null);
            holder.photo = (ImageView) newView.findViewById(R.id.photoView);
            holder.name = (TextView) newView.findViewById(R.id.text_item);
            newView.setTag(holder);
        } else {

            holder = (ViewHolder) newView.getTag();

        }
        holder.photo.setImageBitmap(curr.getBitmap());
        holder.name.setText(curr.getName());

        return newView;
    }

    public void add(SelfieItem item) {
        selfieItems.add(item);
        notifyDataSetChanged();
    }

    public void delete(int position) {
        selfieItems.remove(position);
        notifyDataSetChanged();
    }
}
