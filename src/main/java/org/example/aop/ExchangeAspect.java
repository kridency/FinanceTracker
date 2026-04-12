package org.example.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.entity.Invocation;
import org.example.entity.User;
import org.example.repository.InvocationRepository;
import org.example.service.UserService;

import java.security.Principal;
import java.util.Optional;

@Aspect
public class ExchangeAspect {
    private final UserService userService = new UserService();
    private final InvocationRepository invocationRepository = new InvocationRepository();

    @Before(value = "execution(public * jakarta.servlet.http.HttpServlet..do*(jakarta.servlet.http.HttpServletRequest, ..))" +
            "&& args(request, ..)", argNames = "request")
    public void httpExchangeCheckToHandle(HttpServletRequest request) {
        String endpoint = request.getPathInfo();
        String sessionId = Optional.ofNullable(request.getUserPrincipal()).map(Principal::getName).orElse(null);
        User user = userService.loadUserByUsername(sessionId);
        if (user != null) { invocationRepository.add(new Invocation(endpoint, user)); }
    }
}
