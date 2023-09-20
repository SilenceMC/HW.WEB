package ru.netology;

import org.apache.http.HttpRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        httpClient.execute(HttpRequest request)

        var server = new Server(9999, 64);


        // добавление хендлеров (обработчиков)
        server.addHandler("GET", "/messages",
                (request, responseStream) -> {
                    server.resourceNotFound(responseStream);
                });


        server.addHandler("POST", "/messages",
                (request, responseStream) -> {
                    server.resourceNotFound(responseStream);
                });

        server.start();


    }
}


