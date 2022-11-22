package com.joeylee.common.utils.http;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * RestTemplate 工具类
 *
 * @author joeylee
 */
@Slf4j
@Data
@ToString(callSuper = true)
public class RestTemplateUtil extends BaseHttpUtil {
    protected RestTemplate restTemplate;


    @PostConstruct
    public void postConstruct() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        // 超时
        factory.setConnectionRequestTimeout(connectionTimeout);
        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(readTimeout);

        SSLConnectionSocketFactory sslConnectionSocketFactory;
        try {
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(createIgnoreVerifySSL(),
                    // 指定TLS版本
                    null,
                    // 指定算法
                    null,
                    // 取消域名验证
                    (string, ssls) -> true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
        factory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(factory);
        // 解决中文乱码问题
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        this.restTemplate = restTemplate;
    }

    /**
     * 跳过证书效验的sslcontext
     *
     * @return
     * @throws Exception
     */
    private SSLContext createIgnoreVerifySSL() throws Exception {
        SSLContext sc = SSLContext.getInstance("TLS");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate, String paramString) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate, String paramString) {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }


    @Override
    public String execute() {
        validateParam(url);
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        if (ObjectUtil.isNotEmpty(headers)) {
            headers.forEach((k, v) -> header.add(k, v));
        }
        //header.put("Connection", Collections.singletonList("close"));
        RequestEntity request;
        if (ObjectUtil.isNotEmpty(body)) {
            request = new RequestEntity<>(body.toString(), header, httpMethod, URI.create(getFinalUrl()));
        } else {
            request = new RequestEntity<>(httpMethod, URI.create(getFinalUrl()));
        }
        logRequest(request);
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        logResponse(response);

        return response.getBody();
    }


    private void logResponse(ResponseEntity response) {
        Object body = response.getBody();
        if (response.getStatusCodeValue() != 200) {
            log.warn("error,code: {},msg: {}", response.getStatusCodeValue(), body);
        }
        if (response.getStatusCodeValue() == 404) {
            log.error("error,code: {},msg: {}", response.getStatusCodeValue(), body);
        }
        StringBuilder responseString = new StringBuilder();
        responseString.append("response: ");
        responseString.append("\n");
        responseString.append("response header: ");

        HttpHeaders headers = response.getHeaders();
        responseString.append("\n");
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            responseString.append(entry.getKey());
            responseString.append(": ");
            responseString.append(entry.getValue());
            responseString.append("\n");
        }
        responseString.append("response body: ");
        responseString.append("\n");
        responseString.append(body);
        log.info("response: {}", responseString);
    }

    private void logRequest(RequestEntity request) {
        StringBuilder requestString = new StringBuilder();
        requestString.append("request: ");
        requestString.append(request);
        requestString.append("\n");
        requestString.append("request header: ");

        HttpHeaders headers = request.getHeaders();
        requestString.append("\n");
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            requestString.append(entry.getKey());
            requestString.append(": ");
            requestString.append(entry.getValue());
            requestString.append("\n");
        }

        Object requestBody = request.getBody();
        if (ObjectUtil.isNotEmpty(requestBody)) {
            String body = requestBody.toString();
            requestString.append("request body: ");
            requestString.append("\n");
            requestString.append(body);
        }
        log.info("request: \n{}", requestString);
    }
}
