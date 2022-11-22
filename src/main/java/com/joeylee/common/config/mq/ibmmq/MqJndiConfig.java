package com.joeylee.common.config.mq.ibmmq;

import cn.hutool.extra.spring.SpringUtil;
import com.joeylee.common.domain.exception.JoeyLeeException;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * JNDI MQ 连接对象
 *
 * @author joeylee
 */
public class MqJndiConfig extends MqConfig {

    private String jndiName;

    private String factoryJndiName;

    /**
     * 配置连接工厂
     *
     * @return
     */
    public ConnectionFactory connectionFactory() {
        InitialContext initialContext = SpringUtil.getBean(InitialContext.class);
        ConnectionFactory connectionFactory;
        try {
            // 通过JNDI获取连接信息
            connectionFactory = (ConnectionFactory) initialContext.lookup(factoryJndiName);
            log.debug("connectionFactory : {}", connectionFactory.getClass());
        } catch (NamingException e) {
            throw new JoeyLeeException(e);
        }
        return connectionFactory;
    }

    @Override
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactoryConfigurer configurer =
                SpringUtil.getBean(DefaultJmsListenerContainerFactoryConfigurer.class);
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory());
        factory.setPubSubDomain(false);
        return factory;
    }

    /**
     * 配置连接队列
     *
     * @return
     */
    public Destination destination() {
        InitialContext initialContext = SpringUtil.getBean(InitialContext.class);
        Destination destination;
        try {
            destination = (Destination) initialContext.lookup(jndiName);
            log.debug("destination : {}", destination.getClass());
        } catch (NamingException e) {
            throw new JoeyLeeException(e);
        }
        return destination;
    }

    @Override
    public String toString() {
        String sb =
                "MqJndiConfig{"
                        + "jndiName='"
                        + jndiName
                        + '\''
                        + ", factoryJndiName='"
                        + factoryJndiName
                        + '\''
                        + ", host='"
                        + host
                        + '\''
                        + ", port="
                        + port
                        + ", username='"
                        + username
                        + '\''
                        + ", password='"
                        + password
                        + '\''
                        + ", queueManager='"
                        + queueManager
                        + '\''
                        + ", ccsid="
                        + ccsid
                        + ", requestQueue='"
                        + requestQueue
                        + '\''
                        + ", replyQueue='"
                        + replyQueue
                        + '\''
                        + ", encoding='"
                        + encoding
                        + '\''
                        + ", channel='"
                        + channel
                        + '\''
                        + ", charSet='"
                        + charSet
                        + '\''
                        + ", receiveTimeOut="
                        + receiveTimeOut
                        + '}';
        return sb;
    }

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public String getFactoryJndiName() {
        return factoryJndiName;
    }

    public void setFactoryJndiName(String factoryJndiName) {
        this.factoryJndiName = factoryJndiName;
    }
}
