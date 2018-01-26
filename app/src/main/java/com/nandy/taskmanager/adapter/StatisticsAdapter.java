package com.nandy.taskmanager.adapter;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.nandy.taskmanager.R;
import com.nandy.taskmanager.model.StatisticsResult;
import com.nandy.taskmanager.mvp.model.DateFormatModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents duration of each tasks for each month.
 *
 * Created by yana on 22.01.18.
 */

public class StatisticsAdapter extends BaseExpandableListAdapter {

    private DateFormatModel mDateFormatModel;
    private final List<Pair<String, ArrayList<StatisticsResult>>> mData = new ArrayList<>();


    public void setDateFormatModel(DateFormatModel dateFormatModel) {
        mDateFormatModel = dateFormatModel;
    }

    public void setData(List<Pair<String, ArrayList<StatisticsResult>>> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public List<Pair<String, ArrayList<StatisticsResult>>> getData() {
        return mData;
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
    public StatisticsResult getChild(int groupPosition, int childPosition) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistics_group_view, parent, false);
        }

        TextView textGroup = convertView.findViewById(R.id.text_title_group);
        textGroup.setText(mData.get(groupPosition).first);

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistics_child_view, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.txt_title);
        TextView spentTimeTextView = convertView.findViewById(R.id.txt_spent_time);

        StatisticsResult result = getChild(groupPosition, childPosition);
        titleTextView.setText(result.getTaskTitle());
        spentTimeTextView.setText(mDateFormatModel.formatDuration(result.getSpentTime()));


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}