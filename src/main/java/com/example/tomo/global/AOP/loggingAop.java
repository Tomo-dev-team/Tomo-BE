package com.example.tomo.global.AOP;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class loggingAop {

    @Pointcut("execution (* com.example.tomo..*Service..*(..))")
    public void ServicePointcut() {}

    @Around("ServicePointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.info("[START] {}.{}", className, methodName);

        try {
            Object result = joinPoint.proceed(); // 실제 메소드 실행
            return result;

        } catch (Exception e) {
            log.error("[EXCEPTION] {}.{} | type={} | message={}",
                    className,
                    methodName,
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );
            throw e; // ❗ 반드시 다시 던져야 함

        } finally {
            long endTime = System.currentTimeMillis();
            log.info("[END] {}.{} | time={}ms",
                    className,
                    methodName,
                    (endTime - startTime)
            );
        }
    }

}

