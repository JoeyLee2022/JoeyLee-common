package com.joeylee.common.domain.interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

/**
 * 初始化打印接口
 *
 * @author JoeyLee
 */
public interface PostConstructInterface {
    Logger log = LoggerFactory.getLogger(PostConstructInterface.class);


    @PostConstruct
    default void postConstruct() {
        log.info("{} : {}", this.getClass().getSimpleName(), this);
    }

}
