package com.nandy.taskmanager.adapter;

import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.image.ImageLoader;
import com.nandy.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TasksAdapter extends BaseSwipeAdapter {

    public interface OnItemOptionSelectedListener {

        void onDeleteOptionSelected(Task task, int position);

        void onEditOptionSelected(Task task, int position);

        void onToggleStatus(Task task, int position);

    }

    private final ArrayList<Task> mTasks = new ArrayList<>();
    private OnItemOptionSelectedListener mOnItemOptionSelectedListener;

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
            holder.setRepeatPeriod(task.getRepeatPeriod().getTextResId());

            switch (task.getStatus()) {

                case ACTIVE:
                    holder.setControlButtonText(R.string.finish);

                case NEW:
                    holder.setControlButtonText(R.string.start);
            }

            holder.setOnDeleteButtonClickListener(view -> {
                holder.closeSwipeLayout(false);
                mOnItemOptionSelectedListener.onDeleteOptionSelected(task, position);
            });
            holder.setOnEditButtonClickListener(view ->
            {
                holder.closeSwipeLayout(true);
                mOnItemOptionSelectedListener.onEditOptionSelected(task, position);
            });
            holder.setOnControlButtonClickListener(view ->
            {
                holder.closeSwipeLayout(true);
                mOnItemOptionSelectedListener.onToggleStatus(task, position);
            });
        }

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


    public void setItems(Collection<Task> tasks) {
        mTasks.clear();
        addAll(tasks);
    }

    public void set(Task task, int position) {
        mTasks.set(position, task);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mTasks.remove(position);
        notifyDataSetChanged();
    }

    public ArrayList<Task> getItems() {
        return mTasks;
    }


    static class ViewHolder {

        @BindView(R.id.task_image)
        ImageView mImageTask;
        @BindView(R.id.txt_status)
        TextView mTextViewStatus;
        @BindView(R.id.txt_title)
        TextView mTextViewTitle;
        @BindView(R.id.txt_description)
        TextView mTextViewComment;
        @BindView(R.id.txt_period)
        TextView mTextViewPeriod;
        @BindView(R.id.btn_delete)
        Button mButtonDelete;
        @BindView(R.id.btn_edit)
        Button mButtonEdit;
        @BindView(R.id.btn_control)
        Button mButtonToggleStatus;
        @BindView(R.id.swipe)
        SwipeLayout mSwipeLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        void setTitle(String title) {
            mTextViewTitle.setText(title);
        }

        void setComment(String comment) {
            mTextViewComment.setText(comment);
        }

        void setStatus(String status) {
            mTextViewStatus.setText(status);
        }

        void setControlButtonText(@StringRes int textResId) {
            mButtonToggleStatus.setText(textResId);
        }

        void loadImage(String image, boolean drawMapPin) {

            if (drawMapPin) {
                ImageLoader.load(mImageTask.getContext(), image, R.mipmap.ic_map_marker)
                        .into(mImageTask);
            } else {
                ImageLoader.load(mImageTask.getContext(), image)
                        .into(mImageTask);
            }
        }

        void loadImage(int resId, boolean drawMapPin) {

            if (drawMapPin) {
                ImageLoader.load(mImageTask.getContext(), resId, R.mipmap.ic_map_marker)
                        .into(mImageTask);
            } else {
                ImageLoader.load(mImageTask.getContext(), resId)
                        .into(mImageTask);
            }
        }

        void setPeriodical(boolean periodical) {
            mTextViewPeriod.setVisibility(periodical ? View.VISIBLE : View.GONE);
        }

        void setRepeatPeriod(@StringRes int textResId) {
            mTextViewPeriod.setText(textResId);
        }

        void setOnDeleteButtonClickListener(View.OnClickListener onClickListener) {
            mButtonDelete.setOnClickListener(onClickListener);
        }

        void setOnEditButtonClickListener(View.OnClickListener onClickListener) {
            mButtonEdit.setOnClickListener(onClickListener);
        }

        void setOnControlButtonClickListener(View.OnClickListener onClickListener) {
            mButtonToggleStatus.setOnClickListener(onClickListener);
        }

        void closeSwipeLayout(boolean smooth) {
            mSwipeLayout.close(smooth);
        }

    }
}
