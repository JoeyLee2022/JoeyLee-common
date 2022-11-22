package com.joeylee.common.domain.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 线程运行结果
 *
 * @author JoeyLee
 */
@Data
@ToString
public class ThreadCallResult {
    //总线程数
    private int total;
    //成功数
    private int success;
    //失败数
    private int failed;
    //耗时(毫秒)
    private long costTime;
    //详情
    private List<ThreadCallResultDetail<?>> details;
}
