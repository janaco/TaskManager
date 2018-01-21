package com.nandy.taskmanager.adapter;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.nandy.taskmanager.ImageLoader;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TasksAdapter extends BaseSwipeAdapter {

    public interface OnItemOptionSelectedListener {

        void onDeleteOptionSelected(Task task, int position);

        void onEditOptionSelected(Task task, int position);

        void onToggleStatus(Task task, int position);

    }

    private final ArrayList<Task> mTasks;
    private OnItemOptionSelectedListener mOnItemOptionSelectedListener;

    public TasksAdapter() {
        mTasks = new ArrayList<>();
    }

    public void setOnItemOptionSelectedListener(OnItemOptionSelectedListener mOnItemOptionSelectedListener) {
        this.mOnItemOptionSelectedListener = mOnItemOptionSelectedListener;
    }

    public void clearAll() {
        mTasks.clear();
        notifyDataSetChanged();
    }


    public void add(Task task) {
        mTasks.add(task);
        notifyDataSetChanged();
    }


    public void addAll(Collection<Task> tasks) {
        mTasks.addAll(tasks);
        notifyDataSetChanged();
    }


    public void refresh(Collection<Task> tasks) {
        mTasks.clear();
        addAll(tasks);
    }

    public ArrayList<Task> getItems() {
        return mTasks;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public int getCount() {
        return mTasks.size();
    }

    @Override
    public Task getItem(int position) {
        return mTasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mTasks.get(position).getId();
    }


    @Override
    public View generateView(int position, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
    }


    @Override
    public void fillValues(int position, View convertView) {
        ViewHolder holder = new ViewHolder(convertView);

        Task task = getItem(position);
        if (task != null) {
            holder.setTitle(task.getTitle());
            holder.setComment(task.getDescription());
            holder.setStatus(task.getStatus().name());

            if (task.hasImage()) {
                holder.loadImage(task.getImage(), task.hasLocation());
            } else {
                holder.loadImage(R.mipmap.ic_task, task.hasLocation());
            }
            holder.setPeriodical(task.isPeriodical());
            holder.setRepeatPeriod(task.getPeriod().getTextResId());

            switch (task.getStatus()) {

                case ACTIVE:
                    holder.setControlButtonText(R.string.finish);

                case NEW:
                    holder.setControlButtonText(R.string.start);
            }

            holder.setOnDeleteButtonClickListener(view -> mOnItemOptionSelectedListener.onDeleteOptionSelected(task, position));
            holder.setOnEditButtonClickListener(view -> mOnItemOptionSelectedListener.onEditOptionSelected(task, position));
            holder.setOnControlButtonClickListener(view -> mOnItemOptionSelectedListener.onToggleStatus(task, position));
        }

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
        @BindView(R.id.btn_delete)
        Button buttonDelete;
        @BindView(R.id.btn_edit)
        Button buttonEdit;
        @BindView(R.id.btn_control)
        Button buttonToggleStatus;


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

        void setControlButtonText(@StringRes int textResId) {
            buttonToggleStatus.setText(textResId);
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

        void setRepeatPeriod(@StringRes int textResId) {
            textViewPeriod.setText(textResId);
        }

        void setOnDeleteButtonClickListener(View.OnClickListener onClickListener) {
            buttonDelete.setOnClickListener(onClickListener);
        }

        void setOnEditButtonClickListener(View.OnClickListener onClickListener) {
            buttonEdit.setOnClickListener(onClickListener);
        }

        void setOnControlButtonClickListener(View.OnClickListener onClickListener) {
            buttonToggleStatus.setOnClickListener(onClickListener);
        }

    }
}
