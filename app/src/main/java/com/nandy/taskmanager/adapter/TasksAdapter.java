package com.nandy.taskmanager.adapter;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nandy.taskmanager.ImageLoader;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.model.Task;

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
            viewHolder.setStatus(task.getStatus().name());

            if (task.hasImage()) {
                viewHolder.loadImage(task.getImage(), task.hasLocation());
            } else {
                viewHolder.loadImage(R.mipmap.ic_task, task.hasLocation());
            }
            viewHolder.setPeriodical(task.isPeriodical());
            viewHolder.setRepeatPeriod(task.getPeriod().getTextResId());
        }


        return convertView;
    }


    static class ViewHolder {

        @BindView(R.id.task_image)
        ImageView imageTask;
        @BindView(R.id.txt_status)
        TextView textViewStatus;
        @BindView(R.id.txt_title)
        TextView textViewTitle;
        @BindView(R.id.txt_description)
        TextView textViewComment;
        @BindView(R.id.txt_period)
        TextView textViewPeriod;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        void setTitle(String title) {
            textViewTitle.setText(title);
        }

        void setComment(String comment) {
            textViewComment.setText(comment);
        }

        void setStatus(String status) {
            textViewStatus.setText(status);
        }

        void loadImage(String image, boolean drawMapPin) {

            if (drawMapPin) {
                ImageLoader.load(imageTask.getContext(), image, R.mipmap.ic_map_marker)
                        .into(imageTask);
            } else {
                ImageLoader.load(imageTask.getContext(), image)
                        .into(imageTask);
            }
        }

        void loadImage(int resId, boolean drawMapPin) {

            if (drawMapPin) {
                ImageLoader.load(imageTask.getContext(), resId, R.mipmap.ic_map_marker)
                        .into(imageTask);
            } else {
                ImageLoader.load(imageTask.getContext(), resId)
                        .into(imageTask);
            }
        }

        void setPeriodical(boolean periodical) {
            textViewPeriod.setVisibility(periodical ? View.VISIBLE : View.GONE);
        }

        void setRepeatPeriod(@StringRes int textResId){
            textViewPeriod.setText(textResId);
        }
    }
}
