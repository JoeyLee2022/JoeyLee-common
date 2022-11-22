package com.joeylee.common.config.runner;

import cn.hutool.extra.spring.SpringUtil;
import com.joeylee.common.annotation.EnableJoeyLeeConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 打印bean的日志
 *
 * @author joeylee
 */
@Component
@Order(1)
@Slf4j
@ConditionalOnProperty(prefix = "joeylee.print-all-beans", name = "enable", havingValue = "true")
@ConditionalOnBean(annotation = EnableJoeyLeeConfiguration.class)
public class JoeyLeeRunner implements CommandLineRunner {

    @Value("${joeylee.print-all-beans.enable:false}")
    private boolean queryAllBeans;

    @Override
    public void run(String... args) {
        //获取所有的bean
        if (queryAllBeans) {
            String[] allBeanNames = SpringUtil.getApplicationContext().getBeanDefinitionNames();
            log.info("get all spring-managed beans");
            for (String allBeanName : allBeanNames) {
                log.debug("allBeanName init");
            }
        }
    }

}


