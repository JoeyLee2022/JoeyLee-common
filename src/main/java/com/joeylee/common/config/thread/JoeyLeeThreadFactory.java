package com.joeylee.common.config.thread;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;


/**
 * 自定义线程池工厂类
 *
 * @author joeylee
 */
@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoeyLeeThreadFactory implements ThreadFactory {

    private int counter;
    private String name;

    public JoeyLeeThreadFactory(String name) {
        counter = 0;
        this.name = name;
    }

    /**
     * 只需重写该方法
     *
     * @param run a runnable to be executed by new thread instance
     * @returnA
     */
    @Override
    public Thread newThread(Runnable run) {
        Thread t = new Thread(run, name + "-Thread-" + counter);
        counter++;
        //log.debug("ThreadFactory Created thread {} with name {} on {}\n", t.getId(), t.getName(), LocalDateTime.now());
        return t;
    }

}
