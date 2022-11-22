package com.joeylee.common.domain.abstracts;

import com.joeylee.common.domain.entity.MqConfig;

/**
 * ibm mq 服务端 抽象接口
 *
 * @author joeylee
 */
public abstract class AbstractMqService {

    public abstract void send(Object msg, byte[] correlationId, String correlationIdStr, byte[] messageId, String messageIdStr,
                              MqConfig config);


    public abstract Object receive(byte[] correlationId, String correlationIdStr, byte[] messageId, String messageIdStr, MqConfig config);

    /**
     * 监听获取队列的消息
     */
    public abstract void get(MqConfig config);


    /**
     * 发送并接收
     */
    public Object sendAndReceive(Object msg, MqConfig config) {
        send(msg, config);
        return receive(config);
    }

    public Object sendAndReceiveByMessageId(Object msg, byte[] messageId, MqConfig config) {
        sendByMessageId(msg, messageId, config);
        return receiveByMessageId(messageId, config);
    }

    public Object sendAndReceiveByMessageId(Object msg, String messageId, MqConfig config) {
        sendByMessageId(msg, messageId, config);
        return receiveByMessageId(messageId, config);
    }

    public Object sendAndReceiveByCorrelationId(Object msg, byte[] correlationId, MqConfig config) {
        sendByCorrelationId(msg, correlationId, config);
        return receiveByCorrelationId(correlationId, config);
    }

    public Object sendAndReceiveByCorrelationId(Object msg, String correlationId, MqConfig config) {
        sendByCorrelationId(msg, correlationId, config);
        return receiveByCorrelationId(correlationId, config);
    }


    /**
     * 根据 队列 发送消息
     *
     * @param msg    消息
     * @param config 　MQ配置
     * @return
     */
    public void send(Object msg, MqConfig config) {
        send(msg, null, null, null, null, config);

    }

    /**
     * 根据 队列 接收消息
     *
     * @param config 　MQ配置
     * @return
     */
    public Object receive(MqConfig config) {
        return receive(null, null, null, null, config);
    }

    /**
     * 根据 messageId 发送消息
     *
     * @return
     */
    public void sendByMessageId(Object msg, byte[] messageId, MqConfig config) {
        send(msg, null, null, messageId, null, config);
    }

    public void sendByMessageId(Object msg, String messageId, MqConfig config) {
        send(msg, null, null, null, messageId, config);
    }

    /**
     * 根据 correlationId 发送消息
     *
     * @return
     */
    public void sendByCorrelationId(Object msg, byte[] correlationId, MqConfig config) {
        send(msg, correlationId, null, null, null, config);
    }

    public void sendByCorrelationId(Object msg, String correlationId, MqConfig config) {
        send(msg, null, correlationId, null, null, config);
    }

    /**
     * 根据 messageId 接收消息
     *
     * @return
     */
    public Object receiveByMessageId(byte[] messageId, MqConfig config) {
        return receive(null, null, messageId, null, config);
    }

    public Object receiveByMessageId(String messageId, MqConfig config) {
        return receive(null, null, null, messageId, config);
    }

    /**
     * 根据 correlationId 接收消息
     *
     * @return
     */
    public Object receiveByCorrelationId(byte[] correlationId, MqConfig config) {
        return receive(correlationId, null, null, null, config);
    }

    public Object receiveByCorrelationId(String correlationId, MqConfig config) {
        return receive(null, correlationId, null, null, config);
    }


}
