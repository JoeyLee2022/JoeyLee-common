package com.joeylee.common.config;

import cn.hutool.core.exceptions.ValidateException;
import com.joeylee.common.annotation.EnableJoeyLeeConfiguration;
import com.joeylee.common.domain.entity.Result;
import com.joeylee.common.domain.exception.JoeyLeeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理
 *
 * @author joeylee
 */

@RestControllerAdvice
@Slf4j
@ConditionalOnProperty(prefix = "joeylee.exception-handler", name = "enable", havingValue = "true")
@ConditionalOnBean(annotation = EnableJoeyLeeConfiguration.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public <T> Result<T> handlerNoFoundException(NoHandlerFoundException e) {
        log.error("404 not found: {}", e.getMessage(), e);
        return Result.failed(HttpStatus.NOT_FOUND.toString(), e.getMessage());
    }


    @ExceptionHandler(JoeyLeeException.class)
    public <T> Result<T> handleMockException(JoeyLeeException e) {
        log.error("业务异常，异常原因：{}", e.getMessage(), e);
        if (e.getResultCode() != null) {
            return Result.failed(e.getResultCode());
        }
        return Result.failed(e.getMessage());
    }


    @ExceptionHandler(ValidateException.class)
    public <T> Result<T> validateException(ValidateException e) {
        log.error("验证异常，异常原因：{}", e.getMessage(), e);
        return Result.failed(e.getMessage());
    }


    @ExceptionHandler(BindException.class)
    public Result<Map<String, String>> methodArgumentNotValidException(BindException e) {
        log.error("参数校验异常，异常原因：{}", e.getMessage(), e);
        Map<String, String> errors = new HashMap<>();
        e.getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String defaultMessage = error.getDefaultMessage();
            errors.put(field, defaultMessage);
        });
        return Result.failed("参数校验异常", errors);
    }


    @ExceptionHandler(value = Exception.class)
    public <T> Result<T> handleException(Exception e) {
        log.error("异常，异常原因：{}", e.getMessage(), e);
        return Result.failed(e.getMessage());
    }

}
