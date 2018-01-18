package com.nandy.taskmanager.mvp.view;

/**
 * Created by razomer on 18.01.18.
 */

public interface TaskDetailsView {

    void setTitle(String title);

    void setDescription(String description);

    void setTime(String time);

    void setStatus(String status);

    void setLocation(String location);

    void loadImage(String image);
}
