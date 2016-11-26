package com.example.mahendran.moviecritic;

import android.util.Log;

/**
 * Created by Mahendran on 22-11-2016.
 */

public class MyThreadUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        String TAG="IllegalStateException";
        Log.e(TAG, "Received exception '" + ex.getMessage() + "' from thread " + thread.getName(), ex);
    }
}
