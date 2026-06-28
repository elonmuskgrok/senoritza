package com.siva.utility;

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

    @Before("execution(* com.siva.service.*.*(..))")
    public void logBeforeService(JoinPoint joinPoint) {
        logger.info("Entering method: {} in class: {}", joinPoint.getSignature().getName(), joinPoint.getTarget().getClass().getSimpleName());
    }

    @AfterThrowing(pointcut = "execution(* com.siva.service.*.*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        logger.error("Exception in method: {} with cause: {}", joinPoint.getSignature().getName(), ex.getMessage());
    }
}
