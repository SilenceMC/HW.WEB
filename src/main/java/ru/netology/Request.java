package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class Request {
    private final String method;
    private final String path;

    private final List<NameValuePair> queryParams;

    public Request(String method, String path, List<NameValuePair> queryParams) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public List<NameValuePair> getQueryParam(String name) throws URISyntaxException {
        return getQueryParams()
                .stream()
                .filter(param -> param.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

}
