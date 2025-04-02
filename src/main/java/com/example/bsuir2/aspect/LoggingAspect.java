package com.example.bsuir2.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Точка среза для всех методов в пакетах контроллеров и сервисов
    @Pointcut("within(com.example.bsuir2.controller..*) || within(com.example.bsuir2.service..*)")
    public void applicationPackagePointcut() {
        // Метод пуст, служит лишь для указания точки среза
    }

    // Логирование входа в метод
    @Before("applicationPackagePointcut()")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Entering {} with arguments: {}",
                joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    // Логирование выброса исключений
    @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        logger.error("Exception in {}: {}",
                joinPoint.getSignature().toShortString(), e.getMessage());
    }
}
