package org.example.aop;

import jakarta.inject.Scope;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.lang.annotation.Retention;

@Aspect
public class StopWatchAspect {
    @Around("execution(* *(..)) && !within(org.example.aop..*) && !within(org.example..*Test*)" +
            "&& !within(org.example.mapper..*) && within(org.example..*)")
    public Object logExecutionDuration(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            System.out.println(joinPoint + " -> " + (System.nanoTime() - startTime) / 1_000_000 + " ms");
        }
    }
}
