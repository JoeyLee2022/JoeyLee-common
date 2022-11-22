package com.joeylee.common.domain.entity;

import lombok.Data;
import lombok.ToString;

/**
 * 线程运行详情
 *
 * @author JoeyLee
 */
@Data
@ToString
public class ThreadCallResultDetail<T> {
    //运行线程
    private String threadName;
    //是否运行成功
    private boolean isSuccess;
    //运行异常
    private String exceptionMessage;
    //耗时(毫秒)
    private long costTime;
    //响应结果
    private T data;

}
