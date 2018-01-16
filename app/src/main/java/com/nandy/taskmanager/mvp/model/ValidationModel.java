package com.nandy.taskmanager.mvp.model;

/**
 * Created by yana on 16.01.18.
 */

public class ValidationModel {

    public boolean isEmpty(String text){

        return text == null || text.isEmpty();
    }
}
