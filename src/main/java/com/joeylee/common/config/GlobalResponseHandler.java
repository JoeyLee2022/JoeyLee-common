package com.joeylee.common.config;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONUtil;
import com.joeylee.common.annotation.EnableJoeyLeeConfiguration;
import com.joeylee.common.domain.entity.Original;
import com.joeylee.common.domain.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * 全局响应结果处理
 *
 * @author joeylee
 */
@RestControllerAdvice
@Slf4j
@ConditionalOnBean(annotation = EnableJoeyLeeConfiguration.class)
@ConditionalOnProperty(prefix = "joeylee.response-handler", name = "enable", havingValue = "true")
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    /**
     * 返回 true 则下面 beforeBodyWrite方法被调用, 否则就不调用下述方法
     *
     * @param returnType
     * @param converterType
     * @return
     */
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        // 遇到feign接口之类的请求, 不应该再次包装,应该直接返回
        // 上述问题的解决方案: 可以在feign拦截器中,给feign请求头中添加一个标识字段, 表示是feign请求
        // 在此处拦截到feign标识字段, 则直接放行 返回body.

        log.debug("beforeBodyWrite: {}", body);
        log.debug("MediaType: {}", selectedContentType.getType());
        if (body instanceof Result) {
            return body;
        } else if (body instanceof Original) {
            Object data = ((Original<?>) body).getData();
            if (data instanceof String) {
                response.getHeaders().set(Header.CONTENT_TYPE.getValue(), ContentType.TEXT_PLAIN.getValue());
                try (OutputStream outputStream = response.getBody()) {
                    outputStream.write(data.toString().getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            return data;
        } else if (body instanceof String) {
            //返回json格式
            response.getHeaders().set(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
            return JSONUtil.toJsonStr(Result.success(body));
        } else if (body == null) {
            return Result.success();
        } else {
            return Result.success(body);
        }
    }
}