package com.joeylee.common.domain.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 原始数据结构返回
 *
 * @author joeylee
 */
@Data
public class Original<T> implements Serializable {

    T data;

    public Original(T data) {
        this.data = data;
    }

    public static <T> Original<T> of(T data) {
        return new Original<>(data);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
