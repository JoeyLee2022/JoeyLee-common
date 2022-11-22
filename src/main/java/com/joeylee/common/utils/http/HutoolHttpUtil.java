package com.joeylee.common.utils.http;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;

/**
 * hutool 工具类
 *
 * @author joeylee
 */
public class HutoolHttpUtil extends BaseHttpUtil {

    private HttpRequest request;


    public HutoolHttpUtil() {
        HttpRequest httpRequest = HttpRequest.of("");
        httpRequest.setReadTimeout(readTimeout);
        httpRequest.setConnectionTimeout(connectionTimeout);
        this.request = httpRequest;
        request.setUrl(getFinalUrl());
        if (ObjectUtil.isNotEmpty(body)) {
            request.body(body.toString());
        }
        if (ObjectUtil.isNotEmpty(headers)) {
            request.headerMap(headers, true);
        }
        request.method(Method.valueOf(httpMethod.toString()));

        if (ObjectUtil.isNotEmpty(formData)) {
            request.form(formData);
        }
    }

    @Override
    public String execute() {
        validateParam(url);
        logRequest(request);
        HttpResponse response = request.execute();
        logResponse(response);
        return response.body();
    }

}
