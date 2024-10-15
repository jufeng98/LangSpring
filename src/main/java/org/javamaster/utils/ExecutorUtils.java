package org.javamaster.utils;

import org.javamaster.service.RunTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorUtils {
    public static ExecutorService executorService = Executors.newCachedThreadPool();

    public static Future<?> addTask(RunTask runnable) {
        return executorService.submit(runnable);
    }
}
