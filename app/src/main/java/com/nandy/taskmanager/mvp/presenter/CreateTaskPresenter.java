package com.nandy.taskmanager.mvp.presenter;

import android.app.Activity;
import android.content.Intent;

import com.nandy.taskmanager.R;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.model.CreateTaskModel;
import com.nandy.taskmanager.mvp.model.ValidationModel;
import com.nandy.taskmanager.mvp.view.CreateTaskView;

/**
 * Created by yana on 16.01.18.
 */

public class CreateTaskPresenter {

    private CreateTaskView mView;
    private CreateTaskModel mCreateTaskMode;
    private ValidationModel mValidationModel;

    public CreateTaskPresenter(CreateTaskView view) {
        mView = view;
    }

    public void createTask(String title, String comment) {

        if (mValidationModel.isEmpty(title)) {
            mView.setTitleError(R.string.empty_field);
            return;
        }

        if (mValidationModel.isEmpty(comment)) {
            mView.setCommentError(R.string.empty_field);
            return;
        }

        Task task = new Task(title, comment);
        mCreateTaskMode.save(task);

        Intent intent = new Intent();
        intent.putExtra("task", task);
        mView.setResult(Activity.RESULT_OK, intent);
    }


    public void setCreateTaskMode(CreateTaskModel mCreateTaskMode) {
        this.mCreateTaskMode = mCreateTaskMode;
    }

    public void setValidationModel(ValidationModel mValidationModel) {
        this.mValidationModel = mValidationModel;
    }
}
