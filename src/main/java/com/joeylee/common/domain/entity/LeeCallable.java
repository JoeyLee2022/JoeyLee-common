package com.joeylee.common.domain.entity;

import java.util.concurrent.Callable;

public class LeeCallable implements Callable<ThreadCallResultDetail> {

    private Runnable runnable;

    public LeeCallable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public ThreadCallResultDetail call() {
        return null;
    }
}
