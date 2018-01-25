package com.nandy.taskmanager;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by yana on 25.01.18.
 */

public abstract class SimpleTextChangedListener implements TextWatcher {

    public  abstract void onTextChanged(String text);

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        onTextChanged(editable.toString());
    }
}
