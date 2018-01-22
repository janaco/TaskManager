package com.nandy.taskmanager.adapter;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.nandy.taskmanager.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by razomer on 22.01.18.
 */

public class StatisticsAdapter extends BaseExpandableListAdapter {

    private List<Pair<String, ArrayList>> mData;

    public StatisticsAdapter (List<Pair<String, ArrayList>> data){
        mData = data;
    }

    @Override
    public int getGroupCount() {
        return mData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mData.get(groupPosition).second.size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return mData.get(groupPosition).first;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mData.get(groupPosition).second.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistics_group_view, null);
        }

        TextView textGroup =  convertView.findViewById(R.id.text_title_group);
        textGroup.setText(mData.get(groupPosition).first);

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistics_child_view, null);
        }

        TextView titleTextView = convertView.findViewById(R.id.txt_title);
        TextView spentTimeTextView = convertView.findViewById(R.id.txt_spent_time);


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}