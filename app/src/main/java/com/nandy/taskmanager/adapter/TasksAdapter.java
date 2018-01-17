package com.nandy.taskmanager.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nandy.taskmanager.R;
import com.nandy.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TasksAdapter extends ArrayAdapter<Task> {


    public TasksAdapter(Context context, List<Task> itemList) {
        super(context, R.layout.item_task, itemList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Task task = getItem(position);
        if (task != null) {
            viewHolder.setTitle(task.getTitle());
            viewHolder.setComment(task.getDescription());
        }


        return convertView;
    }


    static class ViewHolder {

        @BindView(R.id.txt_title)
        TextView textViewTitle;
        @BindView(R.id.txt_comment)
        TextView textViewComment;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        void setTitle(String title) {
            textViewTitle.setText(title);
        }

        void setComment(String comment) {
            textViewComment.setText(comment);
        }
    }
}
