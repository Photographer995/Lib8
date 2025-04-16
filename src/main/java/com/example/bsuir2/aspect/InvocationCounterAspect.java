package com.example.bsuir2.aspect;

import com.example.bsuir2.service.InvocationCounter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class InvocationCounterAspect {
    private final InvocationCounter invocationCounter;

    public InvocationCounterAspect(InvocationCounter invocationCounter) {
        this.invocationCounter = invocationCounter;
    }

    // Исключаем сам InvocationCounter из перехвата
    @Before("execution(* com.example.bsuir2.service..*(..)) " +
            "&& !within(com.example.bsuir2.service.InvocationCounter)")
    public void beforeServiceMethod(JoinPoint joinPoint) {
        invocationCounter.increment();
    }
}