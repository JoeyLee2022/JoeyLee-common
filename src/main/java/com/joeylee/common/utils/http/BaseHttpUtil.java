package com.joeylee.common.utils.http;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.joeylee.common.domain.exception.JoeyLeeException;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.Map;

/**
 * http 工具类
 *
 * @author joeylee
 */
@Slf4j
@Data
@ToString(callSuper = true)
public abstract class BaseHttpUtil {
    //连接时间
    protected int connectionTimeout;
    //读取时间
    protected int readTimeout;
    //公共前缀，如果不为空，则请求url=baseUrl+url
    protected String baseUrl;
    protected String url;

    //媒体类型
    protected MediaType mediaType;

    //Http请求方法
    protected HttpMethod httpMethod;
    //路径参数,优先级高于uriVariables
    protected Map<String, ?> uriVariablesMap;
    //路径参数，数组形式
    protected Object[] uriVariables;

    //请求体数据
    protected Object body;

    //请求体表单数据
    protected Map<String, Object> formData;

    //请求参数
    protected Map<String, Object> urlParams;

    //请求头
    protected Map<String, String> headers;


    public BaseHttpUtil() {
    }

    public BaseHttpUtil(int connectionTimeout, int readTimeout) {
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
    }

    /**
     * 拼接 URL 中间内容
     *
     * @param host
     * @param port
     * @return
     */
    public static String getMiddle(String host, Integer port) {
        return ObjectUtil.isEmpty(port) || port < 0 ? host : host + ":" + port;
    }

    /**
     * 获取最终拼接url
     *
     * @return
     */
    public String getFinalUrl() {
        String getUrl = "";
        if (StrUtil.isNotBlank(getBaseUrl())) {
            getUrl += baseUrl;
        }

        if (StrUtil.isNotBlank(this.url)) {
            getUrl += this.url;
        }

        if (ObjectUtil.isNotEmpty(this.urlParams)) {
            if (getUrl.contains("?")) {
                getUrl += "?";
            }
            for (Map.Entry<String, Object> entry : urlParams.entrySet()) {
                getUrl += entry.getKey() + "=" + entry.getValue() + "&";
            }
            getUrl.substring(0, getUrl.length() - 1);
        }
        return getUrl;
    }

    /**
     * 获取响应结果
     *
     * @return
     */
    public abstract String execute();

    /**
     * 验证URL
     *
     * @param url
     */
    public void validateParam(String url) {
        if (StrUtil.isBlankIfStr(url)) {
            log.error("url is blank");
            throw new JoeyLeeException("url is blank");
        }
    }

    public void logResponse(Object response) {
        log.info("response: {}", response);
    }

    public void logRequest(Object request) {
        log.info("request: {}", request);
    }
}
