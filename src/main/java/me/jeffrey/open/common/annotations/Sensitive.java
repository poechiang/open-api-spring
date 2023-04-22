package me.jeffrey.open.common.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.jeffrey.open.common.SensitiveType;
import me.jeffrey.open.utils.serialize.SensitiveSerialize;

@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveSerialize.class)
public @interface Sensitive {
    SensitiveType type() default SensitiveType.CUSTOM;
    String pattern() default "";
    char symbol() default '*';
    boolean keepLength() default true;
}
