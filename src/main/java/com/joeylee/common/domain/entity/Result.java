package com.joeylee.common.domain.entity;

import com.joeylee.common.domain.enums.ResultCode;
import com.joeylee.common.domain.interfaces.IResultCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结构体
 *
 * @author joeylee
 **/
@Data
public class Result<T> implements Serializable {

    private String code;

    private T data;

    private String msg;

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getDesc());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> failed() {
        return result(ResultCode.ERROR.getCode(), ResultCode.ERROR.getDesc(), null);
    }

    public static <T> Result<T> failed(String msg) {
        return result(ResultCode.ERROR.getCode(), msg, null);
    }

    public static <T> Result<T> failed(String msg, T data) {
        return result(ResultCode.ERROR.getCode(), msg, data);
    }

    public static <T> Result<T> judge(boolean status) {
        if (status) {
            return success();
        } else {
            return failed();
        }
    }

    public static <T> Result<T> failed(IResultCode resultCode) {
        return result(resultCode.getCode(), resultCode.getDesc(), null);
    }

    public static <T> Result<T> failed(IResultCode resultCode, String msg) {
        return result(resultCode.getCode(), msg, null);
    }

    public static <T> Result<T> failed(String code, String msg) {
        return result(code, msg, null);
    }

    private static <T> Result<T> result(IResultCode resultCode, T data) {
        return result(resultCode.getCode(), resultCode.getDesc(), data);
    }

    private static <T> Result<T> result(String code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setData(data);
        result.setMsg(msg);
        return result;
    }

    public static boolean isSuccess(Result<?> result) {
        return result != null && ResultCode.SUCCESS.getCode().equals(result.getCode());
    }
}
