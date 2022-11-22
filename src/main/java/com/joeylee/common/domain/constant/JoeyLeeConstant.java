package com.joeylee.common.domain.constant;

/**
 * @author joeylee
 */
public interface JoeyLeeConstant {
    String MOCK_ENV_LOCAL = "LOCAL";
    String MOCK_MQ = "mockMq";
    String CHARSET = "UTF-8";
    String CONTENT_TYPE = "application/json; charset=utf-8";

    String MOCK_HTTP_V1 = "/http/v1";
    String MOCK_HTTP_V2 = "/http/v2";
    String MOCK_HTTP_METHOD = "/res";

    String HORIZONTAL_DELIMITER = "-";
    String REQ = "req";
    String RES = "req";

    String CORRELATION_ID_PREFIX = "JMSCorrelationID='ID:%s'";
    String MESSAGE_ID_PREFIX = "JMSMessageID='ID:%s'";
    //线程池bean名称
    String THREAD_POOL_BEAN_NAME = "LeeExecutor";

    //换行符
    String LINE_SEPARATOR = System.lineSeparator();

}
