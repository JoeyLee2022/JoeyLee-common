package com.joeylee.common.utils;

import cn.hutool.core.map.MapUtil;
import com.joeylee.common.domain.entity.ThreadCallResult;
import com.joeylee.common.domain.entity.ThreadCallResultDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;

/**
 * 线程 工具类
 *
 * @author JoeyLee
 */
@Slf4j
public class ThreadUtils {


    /**
     * 获取当前线程名称
     */
    public static String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

    /**
     * 并发调用并获取结果
     *
     * @param size     并发数量
     * @param supplier 线程逻辑
     */
    public static ThreadCallResult concurrencyCall(int size, Supplier supplier) {
        return concurrencyCall(size, supplier);
    }

    /**
     * 并发调用并获取结果
     *
     * @param size     并发数量
     * @param executor 线程池
     * @param supplier 线程逻辑
     */
    public static ThreadCallResult concurrencyCall(int size, Executor executor, Supplier supplier) {
        long start = System.currentTimeMillis();
        List<CompletableFuture<ThreadCallResultDetail>> completableFutureList = new ArrayList<>(size);
        List<ThreadCallResultDetail<?>> threadCallResultDetailList = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            CompletableFuture<ThreadCallResultDetail> future;
            if (executor != null) {
                future = CompletableFuture.supplyAsync(supplier, executor);
            } else {
                future = CompletableFuture.supplyAsync(supplier);
            }
            completableFutureList.add(future);
        }
        for (CompletableFuture<ThreadCallResultDetail> future : completableFutureList) {
            threadCallResultDetailList.add(future.join());
        }
        long end = System.currentTimeMillis();
        ThreadCallResult threadCallResult = new ThreadCallResult();
        threadCallResult.setDetails(threadCallResultDetailList);
        threadCallResult.setTotal(size);
        threadCallResult.setCostTime(end - start);
        int success = (int) threadCallResultDetailList.stream().filter(ThreadCallResultDetail::isSuccess).count();
        threadCallResult.setSuccess(success);
        threadCallResult.setFailed(size - success);
        return threadCallResult;
    }

    /**
     * 获取线程池详情
     *
     * @param threadPoolTaskExecutor
     * @return
     */
    public static Map getInfo(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        Map map = new HashMap();
        map.put("getActiveCount", threadPoolTaskExecutor.getActiveCount());
        map.put("getMaxPoolSize", threadPoolTaskExecutor.getMaxPoolSize());
        map.put("getCorePoolSize", threadPoolTaskExecutor.getCorePoolSize());
        map.put("getKeepAliveSeconds", threadPoolTaskExecutor.getKeepAliveSeconds());
        map.put("getThreadPriority", threadPoolTaskExecutor.getThreadPriority());
        map.put("getPoolSize", threadPoolTaskExecutor.getPoolSize());
        map.put("getThreadNamePrefix", threadPoolTaskExecutor.getThreadNamePrefix());

        ThreadPoolExecutor threadPoolExecutor = threadPoolTaskExecutor.getThreadPoolExecutor();
        map.put("getRejectedExecutionHandler", threadPoolExecutor.getRejectedExecutionHandler().getClass());

        BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
        map.put("getQueue", queue.getClass());
        map.put("getQueue size", queue.size());
        map.put("getQueue remainingCapacity", queue.remainingCapacity());
        return MapUtil.sort(map);
    }


}