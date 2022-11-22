package com.joeylee.common.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * mq 连接信息
 *
 * @author joeylee
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class MqConfig {

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
    //接收消息等待时间，单位毫秒
    protected int receiveTimeOut;
    //过期时间，单位毫秒
    protected int expirationTime;

}