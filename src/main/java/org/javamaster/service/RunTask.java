package org.javamaster.service;

import com.intellij.openapi.diagnostic.Logger;

/**
 * @author yudong
 */
@FunctionalInterface
public interface RunTask extends Runnable {
    Logger LOG = Logger.getInstance(RunTask.class);

    @Override
    default void run() {
        try {
            runTask();
        } catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }
    }

    void runTask() throws Exception;

}
