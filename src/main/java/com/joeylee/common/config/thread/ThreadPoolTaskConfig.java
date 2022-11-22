package com.joeylee.common.config.thread;


import cn.hutool.core.thread.RejectPolicy;
import com.joeylee.common.annotation.EnableJoeyLeeConfiguration;
import com.joeylee.common.domain.constant.JoeyLeeConstant;
import com.joeylee.common.domain.interfaces.PostConstructInterface;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 线程池 配置
 *
 * @author joeylee
 */
@Configuration
@EnableAsync
@ConfigurationProperties(prefix = "joeylee.thread-pool")
@ConditionalOnProperty(prefix = "joeylee.thread-pool", name = "enable", havingValue = "true")
@Slf4j
@Data
@ToString
@ConditionalOnBean(annotation = EnableJoeyLeeConfiguration.class)
public class ThreadPoolTaskConfig implements PostConstructInterface {

    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer queueCapacity;

    private boolean allowCoreThreadTimeOut;

    private Integer keepAliveSeconds;
    private String threadNamePrefix;
    private RejectPolicy rejectPolicy;

    private boolean waitForTasksToCompleteOnShutdown;

    private int awaitTerminationMillis;
    private boolean prestartAllCoreThreads;


    @Bean(JoeyLeeConstant.THREAD_POOL_BEAN_NAME)
    @Primary
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        /*//Java虚拟机可用的处理器数
        int processors = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(processors);*/

        // 核心线程数
        executor.setCorePoolSize(corePoolSize);
        // 最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        // 队列容量
        executor.setQueueCapacity(queueCapacity);
        // 默认为false,设置为true的话，keepAliveSeconds参数设置的有效时间对corePoolSize线程也有效
        executor.setAllowCoreThreadTimeOut(allowCoreThreadTimeOut);
        // 默认60s,线程活跃时间（秒）
        executor.setKeepAliveSeconds(keepAliveSeconds);
        // 默认线程名称前缀
        executor.setThreadNamePrefix(threadNamePrefix);
        // 拒绝策略
        executor.setRejectedExecutionHandler(rejectPolicy.getValue());
        //IOC容器关闭时是否阻塞等待剩余的任务执行完成，默认:false（必须设置setAwaitTerminationSeconds）若为true表示等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        //阻塞IOC容器关闭的时间，默认：10秒（必须设置setWaitForTasksToCompleteOnShutdown）
        executor.setAwaitTerminationMillis(awaitTerminationMillis);
        //是否启动所有核心线程
        executor.setPrestartAllCoreThreads(prestartAllCoreThreads);
        //executor.setTaskDecorator();
        // 线程工厂
        executor.setThreadFactory(JoeyLeeThreadFactory.builder().name(threadNamePrefix).build());
        // 初始化线程池
        executor.initialize();
        return executor;
    }

}

