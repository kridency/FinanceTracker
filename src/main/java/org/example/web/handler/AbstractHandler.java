package org.example.web.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jakarta.inject.Inject;
import org.example.web.listener.RequestStream;
import org.example.web.listener.RequestWrapper;
import org.example.web.listener.ResponseWrapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractHandler implements HttpHandler {
    @Inject
    protected HttpServlet servlet;
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        byte[] inBytes = getBytes(httpExchange.getRequestBody());
        httpExchange.getRequestBody().close();
        final ByteArrayInputStream newInput = new ByteArrayInputStream(inBytes);
        final RequestStream is = new RequestStream(newInput.readAllBytes());

        Map<String, String[]> parsePostData = new HashMap<>();

        try {
            parsePostData.putAll(splitQuery(httpExchange.getRequestURI().getQuery()));
            parsePostData.putAll(splitQuery(new String(inBytes, StandardCharsets.ISO_8859_1)));
        } catch (IllegalArgumentException e) {
            newInput.reset();
        }

        RequestWrapper req = new RequestWrapper(createUnimplementAdapter(HttpServletRequest.class), httpExchange, parsePostData, is);
        ResponseWrapper resp = new ResponseWrapper(createUnimplementAdapter(HttpServletResponse.class), httpExchange);

        try {
            req.authenticate(resp);
            servlet.service(req, resp);
            resp.complete();
        } catch (ServletException e) {
            throw new IOException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected static <T> T createUnimplementAdapter(Class<T> httpServletApi) {
        class UnimplementedHandler implements InvocationHandler {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                throw new UnsupportedOperationException("Not implemented: " + method + ", args=" + Arrays.toString(args));
            }
        }

        return (T) Proxy.newProxyInstance(UnimplementedHandler.class.getClassLoader(),
                new Class<?>[] { httpServletApi },
                new UnimplementedHandler());
    }

    protected byte[] getBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            int r = in.read(buffer);
            if (r == -1) break;
            out.write(buffer, 0, r);
        }
        return out.toByteArray();
    }

    public HttpServlet getServlet() {
        return servlet;
    }

    public static Map<String, String[]> splitQuery(String query) {
        final Map<String, List<String>> query_pairs = new LinkedHashMap<>();
        return Optional.ofNullable(query).map(value -> value.split("&")).map(pairs -> {
            for (String pair : pairs) {
                final int idx = pair.indexOf("=");
                final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8) : pair;
                if (!query_pairs.containsKey(key)) {
                    query_pairs.put(key, new LinkedList<>());
                }
                final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1),
                        StandardCharsets.UTF_8) : null;
                query_pairs.get(key).add(value);
            }
            return query_pairs.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toArray(new String[0])));
        }).orElse(Collections.emptyMap());
    }
}
