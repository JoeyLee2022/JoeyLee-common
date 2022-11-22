package com.joeylee.common.config.mq.ibmmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

/**
 * MQ 抽象配置
 *
 * @author joeylee
 */
public abstract class MqConfig {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected String host;
    protected Integer port;
    protected String username;
    protected String password;
    protected String queueManager;
    protected Integer ccsid = 1208;
    protected String requestQueue;
    protected String replyQueue;
    protected String encoding = "273";
    protected String channel;
    protected String charSet;
    protected int receiveTimeOut;

    public abstract JmsListenerContainerFactory<?> jmsListenerContainerFactory();

    @Override
    public String toString() {
        String sb =
                "MqConfig{"
                        + "host='"
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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQueueManager() {
        return queueManager;
    }

    public void setQueueManager(String queueManager) {
        this.queueManager = queueManager;
    }

    public Integer getCcsid() {
        return ccsid;
    }

    public void setCcsid(Integer ccsid) {
        this.ccsid = ccsid;
    }

    public String getRequestQueue() {
        return requestQueue;
    }

    public void setRequestQueue(String requestQueue) {
        this.requestQueue = requestQueue;
    }

    public String getReplyQueue() {
        return replyQueue;
    }

    public void setReplyQueue(String replyQueue) {
        this.replyQueue = replyQueue;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public int getReceiveTimeOut() {
        return receiveTimeOut;
    }

    public void setReceiveTimeOut(int receiveTimeOut) {
        this.receiveTimeOut = receiveTimeOut;
    }
}
