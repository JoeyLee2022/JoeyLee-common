package com.joeylee.common.aspectj;

import com.joeylee.common.annotation.EnableJoeyLeeConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 同步执行 切面类
 *
 * @author joeylee
 **/
@ConditionalOnBean(annotation = EnableJoeyLeeConfiguration.class)
@Aspect
@Component
@Slf4j
public class SyncAspect {


    @Around("@annotation(com.joeylee.common.aspectj.annotations.Sync)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("{} around", this.getClass());
        ReentrantLock reentrantLock = new ReentrantLock();
        reentrantLock.lock();
        Object o = joinPoint.proceed();
        reentrantLock.unlock();
        return o;
    }
}
