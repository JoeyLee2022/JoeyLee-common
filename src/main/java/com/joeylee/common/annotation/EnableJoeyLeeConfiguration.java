package com.joeylee.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 这个不是必须的，建议有这样一个注释，作为自动配置相关属性的入口。
 *
 * @author JoeyLee
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
/**
 * 当以后我们在定义一个作用于类的注解时候，如果希望该注解也作用于其子类，那么可以用@Inherited 来进行修饰。
 */
@Inherited
public @interface EnableJoeyLeeConfiguration {

}
