package com.genius.totop.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolUtils {

    private static final int THREAD_NUM = 5;

    private ExecutorService mExecutorService = null;

    private static ThreadPoolUtils mPoolHelper;

    private ThreadPoolUtils() {
        mExecutorService = Executors.newFixedThreadPool(THREAD_NUM);
    }

    public void execute(Runnable command) {
        mExecutorService.execute(command);
    }

    public void submit(Runnable runnable) {
        mExecutorService.submit(runnable);
    }

    public ExecutorService getExecutorService() {
        return mExecutorService;
    }

    public static ThreadPoolUtils getInstance() {
        if (mPoolHelper == null) {
            mPoolHelper = new ThreadPoolUtils();
        }
        return mPoolHelper;
    }
}
