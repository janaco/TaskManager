package com.nandy.taskmanager.mvp.view;

import android.content.Intent;
import android.support.annotation.StringRes;

/**
 * Created by yana on 16.01.18.
 */

public interface CreateTaskView {

    void setTitleError(@StringRes int textResId);

    void setCommentError(@StringRes int textResId);

    void setResult(int resultCode, Intent intent);
}
