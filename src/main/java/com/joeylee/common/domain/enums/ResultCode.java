package com.joeylee.common.domain.enums;

import com.joeylee.common.domain.interfaces.IResultCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author joeylee
 **/
@AllArgsConstructor
@NoArgsConstructor
public enum ResultCode implements IResultCode, Serializable {

    SUCCESS("0", "success"),
    ERROR("-1", "系统出错"),

    PARAM_EMPTY("10001", "参数为空"),
    PARAM_NULL("10002", "参数为null"),

    MQ_ERROR("20001", "MQ异常"),
    MQ_SEND_ERROR("20002", "MQ发送异常"),
    MQ_RECEIVE_ERROR("20003", "MQ接收异常"),
    MQ_CLOSE_ERROR("20004", "MQ关闭异常"),
    MQ_QUEUE_MANAGER_ERROR("20005", "MQ创建队列管理器异常"),
    MQ_RECEIVE_NULL("20006", "MQ接收消息为空"),


    FILE_NOT_FOUND("30001", "文件不存在，请检查响应文件是否存在"),


    REQ_TYPE_ERROR("40001", "请求type错误"),
    SERVICE_NOT_FUND("40002", "请求服务不存在"),
    CHAIN_ERROR("40003", "调用链发生异常"),
    INTERFACE_NOT_FUND("40004", "接口名不存在"),
    JSON_EXCEPTION("40005", "json格式错误"),


    CLIENT_NOT_SUPPORT("50001", "暂不支持的客户端类型数据"),
    DEFAULT_ENV_NOT_EXIST("50002", "默认环境配置不存在"),
    HTTP_NOT_CONFIGURED("50003", "HTTP未配置"),
    MQ_NOT_CONFIGURED("50004", "MQ未配置"),
    MQ_REQ_NOT_CONFIGURED("50005", "MQ请求队列未配置"),
    MQ_RES_NOT_CONFIGURED("50006", "MQ响应队列未配置"),
    ENV_NOT_EXIST("50003", "环境配置不存在"),


    REQ_TYPE_DEST_NOT_MATCH_CONFIGURATION_TYPE("60001", "请求类型: {} 与配置类型: {} 不匹配，请检查配置"),


    FILE_IS_EMPTY("60001", "文件内容为空"),
    ;

    private String code;
    private String desc;

    public static ResultCode getValue(String code) {
        for (ResultCode value : values())
            if (value.getCode().equals(code)) {
                return value;
            }
        return ERROR; // 默认系统执行错误
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "{" + "\"code\":\"" + code + '\"' + ", \"desc\":\"" + desc + '\"' + '}';
    }
}
