package com.joeylee.common.utils.http;

import cn.hutool.core.util.ObjectUtil;
import com.joeylee.common.domain.exception.JoeyLeeException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * WebClient 测试
 *
 * @author joeylee
 */
@Slf4j
@Data
public class WebClientUtil extends BaseHttpUtil {
    protected String baseUrl;

    private WebClient webClient;
    private WebClient.RequestBodyUriSpec spec;


    public WebClientUtil() {

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .doOnConnected(conn -> conn.addHandlerLast(
                                new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                        .addHandlerLast(
                                new WriteTimeoutHandler(10, TimeUnit.MILLISECONDS)));

        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient.wiretap(true));

        this.webClient = WebClient.builder()
                //公共前缀
                .baseUrl(baseUrl).clientConnector(connector)
                //请求过滤器
                .filter(logRequest())
                //响应过滤器
                .filter(logResponse()).build();
        spec = webClient.method(httpMethod);

        if (ObjectUtil.isNotEmpty(uriVariablesMap)) {
            spec.uri(url, uriVariablesMap);
        } else if (ObjectUtil.isNotEmpty(uriVariables)) {
            spec.uri(url, uriVariables);
        } else {
            spec.uri(url);
        }

        if (ObjectUtil.isNotEmpty(headers)) {
            headers.forEach((k, v) -> spec.header(k, v));
        }
        if (ObjectUtil.isNotEmpty(body)) {
            spec.body(BodyInserters.fromValue(body));
        }

        if (ObjectUtil.isNotEmpty(formData)) {
            spec.bodyValue(formData);
        }
        spec.accept(mediaType);
    }


    @Override
    public String execute() {

        // 媒体类型
        Mono<String> stringMono = spec
                // 获取响应体
                .retrieve()
                //响应数据类型转换
                .bodyToMono(String.class)
                // 3秒超时
                .timeout(Duration.ofSeconds(3))
                // 重试3次
                .retry(1)
                // 异常映射转换
                .onErrorMap(ReadTimeoutException.class, ex -> new JoeyLeeException("ReadTimeout"))
                //进行异常处理
                .doOnError(JoeyLeeException.class, err -> log.error("error: " + err))
                //请求异常给出默认返回值
                .onErrorReturn("error! please try again later");
        // 订阅结果
        //stringMono.subscribe(responseData ->
        //        log.info(responseData), e ->
        //        log.error("error: " + e.getMessage()));

        //获取结果
        return stringMono.block();
    }

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("header: {}={}", name, value)));
            return next.exchange(clientRequest);
        };
    }


    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            log.info("response {}", response);
            return Mono.just(response);
        });
    }
}