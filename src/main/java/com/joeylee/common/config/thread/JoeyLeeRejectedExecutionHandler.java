package com.joeylee.common.config.thread;

import com.joeylee.common.domain.exception.JoeyLeeException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义 线程池拒绝策略
 *
 * @author joeylee
 */
@Slf4j
public class JoeyLeeRejectedExecutionHandler implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (!executor.isShutdown()) {
            throw new JoeyLeeException("Unable to add task");
        }
    }

}
