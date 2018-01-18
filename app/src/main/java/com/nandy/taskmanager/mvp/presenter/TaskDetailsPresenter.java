package com.nandy.taskmanager.mvp.presenter;

import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.BasePresenter;
import com.nandy.taskmanager.mvp.model.DateFormatModel;
import com.nandy.taskmanager.mvp.model.TaskDetailsModel;
import com.nandy.taskmanager.mvp.view.TaskDetailsView;

import java.util.Locale;

/**
 * Created by razomer on 18.01.18.
 */

public class TaskDetailsPresenter extends BasePresenter{

    private TaskDetailsView mView;
    private TaskDetailsModel mDetailsModel;
    private DateFormatModel mDateFormatModel;

    public TaskDetailsPresenter(TaskDetailsView view){
        mView = view;
    }

    public void setDetailsModel(TaskDetailsModel mDetailsModel) {
        this.mDetailsModel = mDetailsModel;
    }

    public void setDateFormatModel(DateFormatModel mDateFormatModel) {
        this.mDateFormatModel = mDateFormatModel;
    }

    @Override
    public void start() {
        displayData(mDetailsModel.getTask());
    }

    @Override
    public void stop() {

    }

    private void displayData(Task task){

        mView.setStatus(task.getStatus().name());
        mView.setTitle(task.getTitle());
        mView.setDescription(task.getDescription());
        mView.setTime(String.format(Locale.getDefault(), "%s - %s",
                mDateFormatModel.format(task.getStartDate()),
                mDateFormatModel.format(task.getEndDate())));
        if (task.hasLocation()){
            mView.setLocation(task.getLocation().toString());
        }
    }
}
