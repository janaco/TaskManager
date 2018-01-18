package com.nandy.taskmanager.mvp.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nandy.taskmanager.activity.CreateTaskActivity;
import com.nandy.taskmanager.activity.TaskDetailsActivity;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.BasePresenter;
import com.nandy.taskmanager.mvp.model.DateFormatModel;
import com.nandy.taskmanager.mvp.model.TaskDetailsModel;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;
import com.nandy.taskmanager.mvp.view.TaskDetailsView;

import java.util.Locale;

/**
 * Created by razomer on 18.01.18.
 */

public class TaskDetailsPresenter extends BasePresenter{

    private TaskDetailsView mView;
    private TaskDetailsModel mDetailsModel;
    private DateFormatModel mDateFormatModel;
    private TaskRecordsModel mRecordsModel;

    public TaskDetailsPresenter(TaskDetailsView view){
        mView = view;
    }

    public void setDetailsModel(TaskDetailsModel mDetailsModel) {
        this.mDetailsModel = mDetailsModel;
    }

    public void setDateFormatModel(DateFormatModel mDateFormatModel) {
        this.mDateFormatModel = mDateFormatModel;
    }

    public void setRecordsModel(TaskRecordsModel mRecordsModel) {
        this.mRecordsModel = mRecordsModel;
    }

    @Override
    public void start() {
        displayData(mDetailsModel.getTask());
    }

    @Override
    public void stop() {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == TaskDetailsActivity.REQUEST_CODE_EDIT){
            Task task = data.getParcelableExtra("task");
            mDetailsModel.setTask(task);
            displayData(task);
        }
    }

    public Task getTask(){
        return mDetailsModel.getTask();
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

    public void toggleStatus(){
        mDetailsModel.toggleStatus();
        mRecordsModel.update(mDetailsModel.getTask());
        mView.setStatus(mDetailsModel.getTask().getStatus().name());
    }

    public void delete(){
        mRecordsModel.delete(mDetailsModel.getTask());
    }
}
