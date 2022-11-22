package com.joeylee.common.domain.entity;

import java.util.function.Supplier;

/**
 * 线程 逻辑 统计
 *
 * @author JoeyLee
 */
public class LeeSupplier implements Supplier<ThreadCallResultDetail> {

    private Supplier supplier;

    public LeeSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    @Override
    public ThreadCallResultDetail get() {
        long start = System.currentTimeMillis();
        ThreadCallResultDetail threadCallResultDetail = new ThreadCallResultDetail();
        try {
            threadCallResultDetail = new ThreadCallResultDetail();
            threadCallResultDetail.setThreadName(Thread.currentThread().getName());
            Object data = supplier.get();
            threadCallResultDetail.setSuccess(true);
            threadCallResultDetail.setData(data);
        } catch (Exception e) {
            threadCallResultDetail.setExceptionMessage(e.toString());
        }
        long end = System.currentTimeMillis();
        threadCallResultDetail.setCostTime(end - start);
        return threadCallResultDetail;
    }
}
