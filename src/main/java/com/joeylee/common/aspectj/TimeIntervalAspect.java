package com.joeylee.common.aspectj;

import com.joeylee.common.annotation.EnableJoeyLeeConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 运行时间统计 切面类
 *
 * @author joeylee
 **/
@ConditionalOnBean(annotation = EnableJoeyLeeConfiguration.class)
@Aspect
@Component
@Slf4j
@ConditionalOnProperty(prefix = "joeylee.time-interval", name = "enable", havingValue = "true")
public class TimeIntervalAspect {

    @Pointcut("@annotation(com.joeylee.common.aspectj.annotations.TimeInterval)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        // 环绕通知 ProceedingJoinPoint 执行proceed方法的作用是让目标方法执行
        Object o = joinPoint.proceed();
        //类名
        String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        long endTime = System.currentTimeMillis();
        log.info("method :{}.{} cost : {} ms", declaringTypeName, methodName, endTime - startTime);
        return o;
    }
}
