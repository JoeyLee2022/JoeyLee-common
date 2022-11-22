package com.joeylee.common.aspectj.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志打印
 *
 * @author joeylee
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})// 表示运行在方法上
// Documented注解表明这个注释是由 javadoc记录的，在默认情况下也有类似的记录工具。 如果一个类型声明被注释了文档化，它的注释成为公共API的一部分。
@Documented
public @interface WebLog {

    /**
     * 日志描述信息
     */
    String value() default "";

}