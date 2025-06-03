package com.firmys.terminus.annotations;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Terminus annotation is a custom meta-annotation that combines functionality from
 * Spring's {@link RestController} to register a class as a REST controller while also
 * introducing versioning support for APIs. Classes annotated with Terminus are considered
 * REST controllers, with additional metadata to specify API version(s).
 * <p>
 * The Terminus annotation is designed for use on types that require versioned endpoints.
 * It integrates seamlessly with Spring's request mapping functionality to handle version
 * information dynamically.
 * <p>
 * <pre>
 * Features:
 *   - `versions`: Defines the API versions supported by the annotated controller. Controllers
 *     can specify one or more versions.
 *   - `value`: An alias for `@RestController` that can be used to define a specific name for
 *     the controller bean.
 *
 * Default behavior:
 *   - Without specifying versions, the annotation functions similarly to `@RestController`.
 *   - If versions are defined, corresponding endpoint adjustments may be made at runtime
 *     (e.g., by appending a version prefix to endpoint paths).
 *
 * Usage:
 *   - The TerminusHandlerMapping processes this annotation to modify request mappings and
 *     register versioned paths based on the defined versions.
 *
 * Supported targets and retention policy:
 *   - Target: Can be applied to types and other annotations.
 *   - Retention: Available at runtime for dynamic processing.
 * </pre>
 */
@RestController
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface Terminus {

    String[] versions() default {};

    @AliasFor(annotation = RestController.class)
    String value() default "";
}

