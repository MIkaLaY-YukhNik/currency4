package com.example.currency4.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.example.currency4.CurrencyController.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Executing method: {} with arguments: {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @AfterThrowing(pointcut = "execution(* com.example.currency4.CurrencyController.*(..))", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        logger.error("Error in method {}: {}", joinPoint.getSignature().getName(), error.getMessage(), error);
    }
}