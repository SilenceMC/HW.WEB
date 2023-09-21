package ru.netology;

import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final List<String> validPaths = List.of("/index.html",
            "/spring.svg",
            "/spring.png",
            "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private final int port;
    private final int threads;
    private final int REQUEST_LINE_LENGTH = 3;

    private Map<String, Map<String, Handler>> handlers = new HashMap<>();

    public Server(int port, int threads) {
        this.port = port;
        this.threads = threads;
    }


    public void start() throws IOException {
        try (final var serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер стартовал");
            final var executorService = Executors.newFixedThreadPool(threads);
            while (!serverSocket.isClosed()) {
                final var socket = serverSocket.accept();
                executorService.submit(() -> {
                    try {
                        connectionProcessing(socket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    public void connectionProcessing(Socket socket) throws IOException {
        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new BufferedOutputStream(socket.getOutputStream())) {

            final var requestLine = in.readLine();
            final var requestBody = in.read('\r\n\r\n');
            final var parts = requestLine.split(" ");

            if (parts.length != REQUEST_LINE_LENGTH) {
                // just close socket
                return;
            }

            final var method = parts[0];
            final var path = parts[1];

            var request = new Request(method, path);

            if (validPaths.contains(request.getPath())) {
                requestCanHandle(request, out);
            }

            if (!handlers.containsKey(request.getMethod())) {
                resourceNotFound(out);
            } else {
                var handlerMap = handlers.get(request.getMethod());
                if (!handlerMap.containsKey(request.getPath())) {
                    resourceNotFound(out);
                } else {
                    handlerMap.get(path).handle(request, out);
                }
            }
        }
    }

    public void requestCanHandle(Request request, BufferedOutputStream out) throws IOException {
        final var filePath = Path.of(".", "public", request.getPath());
        final var mimeType = Files.probeContentType(filePath);

        // special case for classic
        if (request.getPath().equals("/classic.html")) {
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.write(content);
            out.flush();
            return;
        }

        final var length = Files.size(filePath);
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(filePath, out);
        out.flush();
    }

    public void resourceNotFound(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    public synchronized void addHandler(String method, String path, Handler handler) {
        if (!handlers.containsKey(method)) {
            handlers.put(method, new HashMap<>());
        }
        if (!handlers.get(method).containsKey(path)) {
            handlers.get(method).put(path, handler);
        }
    }
}
