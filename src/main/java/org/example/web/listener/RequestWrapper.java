package org.example.web.listener;

import com.sun.net.httpserver.HttpExchange;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import org.example.dto.UserDto;
import org.example.entity.User;
import org.example.exception.ApplicationException;
import org.example.service.CrudService;
import org.example.service.UserService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.*;

import static org.example.preset.FinancialTrackerInit.UNAUTHORIZED;

public class RequestWrapper extends HttpServletRequestWrapper {
    private final HttpExchange ex;
    private final Map<String, String[]> postData;
    private final ServletInputStream is;
    private final Map<String, Object> attributes = new HashMap<>();
    private final CrudService<UserDto> userService;
    private Principal principal;

    public RequestWrapper(HttpServletRequest request, HttpExchange ex, Map<String, String[]> postData, ServletInputStream is) {
        super(request);
        this.ex = ex;
        this.postData = postData;
        userService = UserService.getInstance();
        Optional.ofNullable(getCookies()).ifPresent(value -> setAttribute("JSESSIONID",
            Arrays.stream(value)
                    .filter(cookie -> cookie.getName().equals("JSESSIONID"))
                    .map(Cookie::getValue).findAny().orElse(null))
        );
        this.is = is;
    }

    @Override
    public String getHeader(String name) {
        return ex.getRequestHeaders().getFirst(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return Optional.ofNullable(ex.getRequestHeaders().get(name)).map(headers ->
                new Vector<>(headers).elements()).orElse(null);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return new Vector<>(ex.getRequestHeaders().keySet()).elements();
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object o) {
        this.attributes.put(name, o);
    }

    @Override
    public ServletContext getServletContext() {
        return super.getServletContext();
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return new Vector<>(attributes.keySet()).elements();
    }

    @Override
    public String getMethod() {
        return ex.getRequestMethod();
    }

    @Override
    public ServletInputStream getInputStream() {
        return is;
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public String getPathInfo() {
        return ex.getRequestURI().getPath();
    }

    @Override
    public Principal getUserPrincipal() {
        return principal;
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws ServletException {
        return Optional.ofNullable(getHeader(HttpHeaders.AUTHORIZATION))
                .filter(x -> x.startsWith("Basic"))
                .map(x -> x.substring("Basic".length()).trim())
                .map(x -> new String(Base64.getDecoder().decode(x), StandardCharsets.UTF_8))
                .map(x -> x.split(":"))
                .map(x -> new UserDto(null, x[0], x[1]))
                .map(dto -> {
                    var user = userService.findAllByDto(dto).stream().findAny().orElse(null);
                    if (user == null) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return false;
                    }
                    principal = user::getEmail;
                    if (getAttribute("JSESSIONID") == null) {
                        Cookie cookie = new Cookie("JSESSIONID", user.getEmail());
                        cookie.setPath("/api/v1");
                        cookie.setMaxAge(2_592_000);
                        response.addCookie(cookie);
                    }
                    return true;
                }).orElseGet(() -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                });
    }

    @Override
    public String getParameter(String name) {
        String[] arr = postData.get(name);
        return arr != null ? (arr.length > 1 ? Arrays.toString(arr) : arr[0]) : null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return postData;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return new Vector<>(postData.keySet()).elements();
    }

    @Override
    public int getServerPort() {
        return HttpListener.getInstance().getServerPort();
    }

    @Override
    public Cookie[] getCookies() {
        return Optional.ofNullable(getHeaders("Cookie")).map(headers ->
                Collections.list(headers).stream()
                .map(header -> {
                    String[] cookie = header.split("=");
                    return new Cookie(cookie[0], cookie[1]);
                }).toArray(Cookie[]::new)
        ).orElse(null);
    }
}
