package com.firmys.terminus.interceptors;

import com.firmys.terminus.TerminusConstants;
import com.firmys.terminus.annotations.ApiVersion;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

@Component
@ConditionalOnClass(org.springframework.web.servlet.DispatcherServlet.class)
public class TerminusMvcInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        return true;
    }

    private void handleAnnotations(
            @NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String terminusVersion = request.getHeader(TerminusConstants.TERMINUS_VERSION_HEADER);
        if (handler instanceof HandlerMethod handlerMethod) {
            // Get method and class
            Method method = handlerMethod.getMethod();
            Class<?> controllerClass = method.getDeclaringClass();

            // Get class annotations
            Annotation[] classAnnotations = controllerClass.getAnnotations();
            System.out.println("Class Annotations:");
            for (Annotation annotation : classAnnotations) {
                System.out.println(" - " + annotation.annotationType().getSimpleName());
                if (annotation instanceof RestController restController) {
                    System.out.println("   RestController value: " + restController.value());
                }
            }

            // Get method annotations
            Annotation[] methodAnnotations = method.getAnnotations();
            System.out.println("Method Annotations:");
            for (Annotation annotation : methodAnnotations) {
                if (annotation instanceof ApiVersion apiVersion) {
                    if (Arrays.stream(apiVersion.allowed())
                            .map(str -> str.toLowerCase(Locale.getDefault()))
                            .anyMatch(allowed -> Objects.equals(allowed, terminusVersion))) {

                    }
                }
                System.out.println(" - " + annotation.annotationType().getSimpleName());
            }
        }
    }
}
