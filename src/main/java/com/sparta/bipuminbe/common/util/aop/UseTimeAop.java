package com.sparta.bipuminbe.common.util.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UseTimeAop {

    @Around("execution(public * com.sparta.bipuminbe.category.controller.*.*(..)) ||" +
            "execution(public * com.sparta.bipuminbe.common.sse.controller.*.*(..)) ||" +
            "execution(public * com.sparta.bipuminbe.department.controller.*.*(..)) ||" +
            "execution(public * com.sparta.bipuminbe.dashboard.controller.*.*(..)) ||" +
            "execution(public * com.sparta.bipuminbe.requests.controller.*.*(..)) ||" +
            "execution(public * com.sparta.bipuminbe.supply.controller.*.*(..)) ||" +
            "execution(public * com.sparta.bipuminbe.user.controller.*.*(..)) ||" +
            "execution(public * com.sparta.bipuminbe.partners.controller.*.*(..))")
    public synchronized Object execute(ProceedingJoinPoint joinPoint) throws Throwable {

        // 측정 시작 시간
        long startTime = System.currentTimeMillis();

        try {
            // 핵심기능 수행
            Object output = joinPoint.proceed();
            return output;
        } finally {
            // 측정 종료 시간
            long endTime = System.currentTimeMillis();
            // 수행시간 = 종료 시간 - 시작 시간
            long runTime = endTime - startTime;

            Class clazz = joinPoint.getTarget().getClass();
            log.info("[API Use Time] API URI: " + getRequestUrl(joinPoint, clazz) + ", run Time: " + runTime + " ms");
        }
    }

    private String getRequestUrl(JoinPoint joinPoint, Class clazz) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
        String baseUrl = requestMapping.value()[0];

        String url = Stream.of( GetMapping.class, PutMapping.class, PostMapping.class,
                        PatchMapping.class, DeleteMapping.class, RequestMapping.class)
                .filter(mappingClass -> method.isAnnotationPresent(mappingClass))
                .map(mappingClass -> getUrl(method, mappingClass, baseUrl))
                .findFirst().orElse(null);
        return url;
    }

    /* httpMETHOD + requestURI 를 반환 */
    private String getUrl(Method method, Class<? extends Annotation> annotationClass, String baseUrl){
        Annotation annotation = method.getAnnotation(annotationClass);
        String[] value;
        String httpMethod = null;
        try {
            value = (String[])annotationClass.getMethod("value").invoke(annotation);
            httpMethod = (annotationClass.getSimpleName().replace("Mapping", "")).toUpperCase();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            return null;
        }
        return String.format("%s %s%s", httpMethod, baseUrl, value.length > 0 ? value[0] : "") ;
    }
}