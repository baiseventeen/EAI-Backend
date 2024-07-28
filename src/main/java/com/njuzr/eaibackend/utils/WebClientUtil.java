package com.njuzr.eaibackend.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;


/**
 * @author: Leonezhurui
 * @Date: 2024/2/25 - 16:52
 * @Package: EAI-Backend
 */

@Slf4j
@Component
public class WebClientUtil {

    private final WebClient webClient;

    public WebClientUtil(@Value("${proxy.enabled}") boolean proxyEnabled,
                         @Value("${proxy.host}") String proxyHost,
                         @Value("${proxy.port}") int proxyPort) {
        HttpClient httpClient = HttpClient.create();
        if (proxyEnabled) {
            httpClient = httpClient.proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP)
                    .host(proxyHost)
                    .port(proxyPort));
        }
        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }


//    public WebClientUtil() {
//        this.webClient = WebClient.builder().build();
//    }
//
//    public WebClientUtil(WebClient webClient) {
//        this.webClient = webClient;
//    }
//
//    public WebClientUtil(String url) {
//        this.webClient = WebClient.builder()
//                .baseUrl(url)
//                .build();
//    }

//    public static WebClientUtil createWebClientWithProxy(String url, String proxyHost, int proxyPort) {
//        HttpClient httpClient = HttpClient.create()
//                .proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP)
//                        .host(proxyHost)
//                        .port(proxyPort));
//
//        WebClient webClient1 =WebClient.builder()
//                .clientConnector(new ReactorClientHttpConnector(httpClient))
//                .baseUrl(url)
//                .build();
//        return new WebClientUtil(webClient1);
//    }

    // GET请求方法
    public <T> T get(String uri, Class<T> responseType) {
        try {
            return this.webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(responseType)
                    .block(); // 转换为阻塞调用
        } catch (WebClientResponseException e) {
            log.error("WebClient发送GET请求失败：" + e.getMessage());
            throw new RuntimeException("Failed to get response: " + e.getMessage(), e);
        }
    }

    // POST请求方法
    public <T, R> T  post(String uri, R request, Class<T> responseType) {
        try {
            return this.webClient.post()
                    .uri(uri)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(responseType)
                    .block(); // 转换为阻塞调用
        } catch (WebClientResponseException e) {
            log.error("WebClient发送POST请求失败：" + e.getMessage());
            throw new RuntimeException("Failed to post data: " + e.getMessage(), e);
        }
    }

    // PUT请求方法
    public <T, R> T put(String uri, R request, Class<T> responseType) {
        try {
            return this.webClient.put()
                    .uri(uri)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(responseType)
                    .block(); // 转换为阻塞调用
        } catch (WebClientResponseException e) {
            log.error("WebClient发送PUT请求失败：" + e.getMessage());
            throw new RuntimeException("Failed to put data: " + e.getMessage(), e);
        }
    }

    // DELETE请求方法
    public <T> T delete(String uri, Class<T> responseType) {
        try {
            return this.webClient.delete()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(responseType)
                    .block(); // 转换为阻塞调用
        } catch (WebClientResponseException e) {
            log.error("WebClient发送DELETE请求失败：" + e.getMessage());
            throw new RuntimeException("Failed to delete resource: " + e.getMessage(), e);
        }
    }


    public <T, R> T  postWithToken(String uri, R request, Class<T> responseType, String key) {
        try {
            return this.webClient.post()
                    .uri(uri)
                    .header("Authorization", "Bearer " + key)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(responseType)
                    .block(); // 转换为阻塞调用
        } catch (WebClientResponseException e) {
            log.error("WebClient发送POST请求失败：" + e.getMessage());
            throw new RuntimeException("Failed to post data: " + e.getMessage(), e);
        }
    }

}
