package com.joeylee.common.config.mq.ibmmq;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.jms.ExceptionListener;

/**
 * JMS MQ 连接对象
 *
 * @author joeylee
 */
public class MqJmsConfig extends MqConfig {

    /**
     * 配置连接工厂: CCSID要与连接到的队列管理器一致，Windows下默认为1381， Linux下默认为1208。1208表示UTF-8字符集，建议把队列管理器的CCSID改为1208
     *
     * @return
     */
    public MQQueueConnectionFactory mqQueueConnectionFactory() {
        MQQueueConnectionFactory factory = new MQQueueConnectionFactory();
        try {
            factory.setHostName(host);
            factory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
            factory.setCCSID(ccsid);
            factory.setChannel(channel);
            factory.setPort(port);
            factory.setQueueManager(queueManager);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return factory;
    }

    /**
     * 配置连接认证: 如不需要账户密码链接可以跳过此步，直接将mqQueueConnectionFactory注入下一步的缓存连接工厂。
     *
     * @param mqQueueConnectionFactory
     * @return
     */
    public UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter(
            MQQueueConnectionFactory mqQueueConnectionFactory) {
        UserCredentialsConnectionFactoryAdapter adapter = new UserCredentialsConnectionFactoryAdapter();
        adapter.setUsername(getUsername());
        adapter.setPassword(getPassword());
        adapter.setTargetConnectionFactory(mqQueueConnectionFactory);
        return adapter;
    }

    /**
     * 配置缓存连接工厂: 不配置该类则每次与MQ交互都需要重新创建连接，大幅降低速度。
     */
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        MQQueueConnectionFactory mqQueueConnectionFactory = mqQueueConnectionFactory();
        if (StrUtil.isBlankIfStr(getUsername()) || StrUtil.isBlankIfStr(getPassword())) {
            // 如不需要账户密码链接可以跳过此步，直接将mqQueueConnectionFactory注入下一步的缓存连接工厂。
            cachingConnectionFactory.setTargetConnectionFactory(mqQueueConnectionFactory);
        } else {
            // 配置连接认证
            cachingConnectionFactory.setTargetConnectionFactory(
                    userCredentialsConnectionFactoryAdapter(mqQueueConnectionFactory));
        }
        // 指定 JMS 会话缓存的所需大小（每个 JMS 会话类型）。
        cachingConnectionFactory.setSessionCacheSize(500);
        // 异常时重连
        cachingConnectionFactory.setReconnectOnException(true);
        ExceptionListener exceptionListener =
                e -> {
                    log.error(
                            "HostName:{},Port:{},QueueManager:{},error:{}",
                            mqQueueConnectionFactory.getHostName(),
                            mqQueueConnectionFactory.getPort(),
                            mqQueueConnectionFactory.getQueueManager(),
                            e.getMessage());
                    if (e.getLinkedException() != null) {
                        log.error(e.getLinkedException().getMessage());
                    }
                };
        cachingConnectionFactory.setExceptionListener(exceptionListener);
        return cachingConnectionFactory;
    }

    /**
     * 配置JMS模板: JmsOperations为JmsTemplate的实现接口。
     * 重要：不设置setReceiveTimeout时，当队列为空，从队列中取出消息的方法将会一直挂起直到队列内有消息
     *
     * @return
     */
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory());
        jmsTemplate.setReceiveTimeout(receiveTimeOut);
        return jmsTemplate;
    }

    /**
     * 配置事务管理器: 不使用事务可以跳过该步骤。 如需使用事务，可添加注解@EnableTransactionManagement到程序入口类中，事务的具体用法可参考Spring
     * Trasaction。
     *
     * @param cachingConnectionFactory
     * @return
     */
    @Bean
    public PlatformTransactionManager jmsTransactionManager(
            CachingConnectionFactory cachingConnectionFactory) {
        JmsTransactionManager jmsTransactionManager = new JmsTransactionManager();
        jmsTransactionManager.setConnectionFactory(cachingConnectionFactory);
        return jmsTransactionManager;
    }

    @Override
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactoryConfigurer configurer =
                SpringUtil.getBean(DefaultJmsListenerContainerFactoryConfigurer.class);
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, mqQueueConnectionFactory());
        factory.setPubSubDomain(false);
        return factory;
    }
}
