package com.firmys.terminus.annotations;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TerminusMapping is a custom annotation used to define request mapping metadata
 * for versioned API endpoints. It extends Spring's {@link RequestMapping} functionality
 * and supports dynamic placeholder resolution for its path definitions.
 * <p>
 * This annotation is primarily designed for use in conjunction with {@link Terminus},
 * enabling endpoints to be associated with specific API versions.
 * <p>
 * Supported properties:
 * <p>
 * <pre>
 * - `value`: Defines the primary path(s) for the endpoint. Can be used as an alias for `path`.
 * - `path`: Specifies the endpoint path(s). If `value` is not provided, this property can be used
 * to define the path(s).
 * - `method`: An array of HTTP methods (e.g., GET, POST, etc.) that this endpoint supports.
 * - `params`: Defines specific request parameters that must be present for this mapping to match.
 * - `headers`: Allows specifying required HTTP headers for the mapping.
 * - `consumes`: Specifies acceptable media types for request bodies. Useful for Content-Type negotiation.
 * - `produces`: Specifies media types for response bodies. Useful for Accept header negotiation.
 *
 * Target and retention policy:
 * - Target: Method-level annotation. Can only be applied to individual handler methods.
 * - Retention: Available at runtime for dynamic request mapping processing.
 *
 * Integration:
 * - Alongside {@link Terminus}, this annotation's properties are dynamically resolved and processed
 * with placeholders like `${terminus.placeholder}` being replaced at runtime. This allows for
 * flexible endpoint definitions based on configuration or runtime contexts.
 * </pre>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping("${terminus.placeholder}")
public @interface TerminusMapping {

    String[] value() default {};

    String[] path() default {};

    RequestMethod[] method() default {};

    String[] params() default {};

    String[] headers() default {};

    String[] consumes() default {};

    String[] produces() default {};
}
