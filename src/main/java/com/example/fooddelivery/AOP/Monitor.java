package com.example.fooddelivery.AOP;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

/**
 * Monitor
 *
 * @author letingsun
 * @since 5/27/23
 */
@EnableAspectJAutoProxy
@Component
@Aspect
public class Monitor {
    @Pointcut("execution(* com.example.fooddelivery.Controller.*.*(..))")
    private void pointCutMethod() {
    }
    @Around("pointCutMethod()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("-----------------------");
        System.out.println("class:" + pjp.getTarget().toString() + " methods: " + pjp.getSignature().getName());
        long start = System.currentTimeMillis();
        Object o = pjp.proceed();
        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println("execution lasted:"+time+" ms");

        return o;
    }

}
