package com.nandy.taskmanager;

import io.reactivex.disposables.Disposable;

/**
 * Created by yana on 25.01.18.
 */

public class SubscriptionUtils {

    public static void dispose(Disposable disposable){
        if (disposable != null && !disposable.isDisposed()){
            disposable.dispose();
        }
    }
}
