package com.joeylee.common.utils.http;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.Header;
import com.joeylee.common.domain.exception.JoeyLeeException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp 工具类
 *
 * @author joeylee
 */
@Data
@Slf4j
public class OkHttpUtil extends BaseHttpUtil {
    protected int writeTimeout;

    private OkHttpClient client = new OkHttpClient();
    private Request.Builder builder;
    private Request request = builder.build();


    public OkHttpUtil() {

        OkHttpClient client1 = client.newBuilder().writeTimeout(writeTimeout, TimeUnit.MILLISECONDS).readTimeout(readTimeout, TimeUnit.MILLISECONDS).connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS).build();
        this.client = client1;
        if (httpMethod.equals(HttpMethod.GET)) {
            builder = new Request.Builder();
        } else {
            final MediaType mediaType = MediaType.parse(Header.CONTENT_TYPE.getValue());
            builder = new Request.Builder().method(httpMethod.toString(), RequestBody.create(body.toString(), mediaType));
        }
        builder.url(getFinalUrl());
        if (ObjectUtil.isEmpty(headers)) {
            headers = new LinkedHashMap<>();
        }
        if (ObjectUtil.isNotEmpty(headers)) {
            headers.forEach((k, v) -> {
                builder.header(k, v);
            });
        }
    }


    private void logResponse(Response response) {
        ResponseBody body = response.body();
        if (response.code() != 200) {
            log.warn("error,code: {},msg: {}", response.code(), body);
        }
        if (response.code() == 404) {
            log.error("error,code: {},msg: {}", response.code(), body);
        }
        StringBuilder responseString = new StringBuilder();
        responseString.append(response);
        responseString.append("\n");
        responseString.append("response header: ");

        Headers responseHeaders = response.headers();
        responseString.append("\n");
        for (int i = 0; i < responseHeaders.size(); i++) {
            responseString.append(responseHeaders.name(i));
            responseString.append(": ");
            responseString.append(responseHeaders.value(i));
            responseString.append("\n");
        }
        if (ObjectUtil.isNotEmpty(body)) {
            String bodyString = null;
            try {
                bodyString = body.string();
            } catch (IOException e) {
                throw new JoeyLeeException(e);
            }
            responseString.append("response body: ");
            responseString.append("\n");
            responseString.append(bodyString);
        }
        log.info("response: {}", responseString);
    }

    private void logRequest(Request request) {
        StringBuilder requestString = new StringBuilder();
        requestString.append(request);
        requestString.append("\n");
        requestString.append("request header: ");

        Headers requestHeaders = request.headers();
        requestString.append("\n");
        for (int i = 0; i < requestHeaders.size(); i++) {
            requestString.append(requestHeaders.name(i));
            requestString.append(": ");
            requestString.append(requestHeaders.value(i));
            requestString.append("\n");
        }
        RequestBody body = request.body();
        if (ObjectUtil.isNotEmpty(body)) {
            requestString.append("request body: ");
            requestString.append("\n");
            requestString.append(body.toString());
            Buffer buffer = new Buffer();
            try {
                body.writeTo(buffer);
            } catch (IOException e) {
                throw new JoeyLeeException(e);
            }
            String requestBodyToString = buffer.readUtf8();
            requestString.append(requestBodyToString);
            requestString.append("\n");
        }
        log.info("request: {}", requestString);
    }

    @Override
    public String execute() {
        validateParam(url);
        logRequest(request);
        try (Response response = client.newCall(request).execute()) {
            logResponse(response);
            return response.body().string();
        } catch (IOException e) {
            log.error("Response failed: {}", e);
        }
        return null;
    }

}
