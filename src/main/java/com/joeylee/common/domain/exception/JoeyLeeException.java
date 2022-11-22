package com.joeylee.common.domain.exception;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.joeylee.common.domain.interfaces.IResultCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义业务异常
 *
 * @author joeylee
 */
@Getter
@Slf4j
public class JoeyLeeException extends RuntimeException {

    public IResultCode resultCode;

    public JoeyLeeException(IResultCode errorCode) {
        super(errorCode.getDesc());
        this.resultCode = errorCode;
    }

    public JoeyLeeException(String message) {
        super(message);
    }

    public JoeyLeeException(String message, Throwable cause) {
        super(message, cause);
    }

    public JoeyLeeException(Throwable cause) {
        super(cause);
    }

    public static void validate(boolean flag, IResultCode errorCode, Object... params) {
        if (flag) {
            if (ObjectUtil.isEmpty(params)) {
                throw new JoeyLeeException(errorCode);
            } else {
                throw new JoeyLeeException(StrFormatter.format(errorCode.getDesc(), params));
            }
        }
    }

    public static void validate(boolean flag, IResultCode errorCode) {
        if (flag) {
            throw new JoeyLeeException(errorCode);
        }
    }

    public static void validateNull(Object param, IResultCode errorCode) {
        if (ObjectUtil.isNull(param)) {
            throw new JoeyLeeException(errorCode);
        }
    }

    public static void validateEmpty(Object param, IResultCode errorCode) {
        if (ObjectUtil.isEmpty(param)) {
            throw new JoeyLeeException(errorCode);
        }
    }

    public static void validateBlank(Object param, IResultCode errorCode) {
        if (StrUtil.isBlankIfStr(param)) {
            throw new JoeyLeeException(errorCode);
        }
    }

    public static void validateNull(Object param, String paramName) {
        if (ObjectUtil.isNull(param)) {
            throw new JoeyLeeException(StrFormatter.format("参数 {} 为null", paramName));
        }
    }

    public static void validateEmpty(Object param, String paramName) {
        if (ObjectUtil.isEmpty(param)) {
            throw new JoeyLeeException(StrFormatter.format("参数 {} 为空", paramName));
        }
    }

    public static void validateBlank(Object param, String paramName) {
        if (StrUtil.isBlankIfStr(param)) {
            throw new JoeyLeeException(StrFormatter.format("参数 {} 为空", paramName));
        }
    }
}
