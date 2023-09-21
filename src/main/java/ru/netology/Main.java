package ru.netology;

import org.apache.http.HttpRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {


        var server = new Server(9998, 64);

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


