package com.joeylee.common.aspectj;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.json.JSONUtil;
import com.joeylee.common.annotation.EnableJoeyLeeConfiguration;
import com.joeylee.common.aspectj.annotations.WebLog;
import com.joeylee.common.domain.constant.JoeyLeeConstant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;

/**
 * web日志切面类 表示有@WebLog注解的方法都会打印日志
 *
 * @author joeylee
 */
@ConditionalOnBean(annotation = EnableJoeyLeeConfiguration.class)
@Aspect
@Component
@Slf4j
@ConditionalOnProperty(prefix = "joeylee.web-log", name = "enable", havingValue = "true")
public class WebLogAspect {

    /**
     * 以自定义 @WebLog 注解为切点
     */
    @Pointcut("@annotation(com.joeylee.common.aspectj.annotations.WebLog)")
    public void webLog() {
    }

    /**
     * 环绕
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();
        doBefore(joinPoint);
        Object o = joinPoint.proceed();
        doAfter(joinPoint, startTime, o);
        return o;
    }


    /**
     * 在切点之前织入
     *
     * @param joinPoint
     */
    private void doBefore(JoinPoint joinPoint) {
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // 获取请求对象
        HttpServletRequest request = attributes.getRequest();
        // 获取 @WebLog 注解的描述信息
        String methodDescription = getAspectLogDescription(joinPoint);
        // 打印请求相关参数
        StringBuffer sb = new StringBuffer(JoeyLeeConstant.LINE_SEPARATOR);
        sb.append("========================================== Request Start==========================================");
        // url
        sb.append(JoeyLeeConstant.LINE_SEPARATOR);
        sb.append("URL  : ");
        sb.append(request.getRequestURL().toString());
        // 描述信息
        if (Validator.isNotEmpty(methodDescription)) {
            sb.append(JoeyLeeConstant.LINE_SEPARATOR);
            sb.append("说明  : ");
            sb.append(methodDescription);
        }
        // Http method
        sb.append(JoeyLeeConstant.LINE_SEPARATOR);
        sb.append("HTTP请求类型  : ");
        sb.append(request.getMethod());
        // controller 的全路径以及执行方法
        sb.append(JoeyLeeConstant.LINE_SEPARATOR);
        sb.append("方法  : ");
        sb.append(getMethod(joinPoint));
        // 请求的 IP
        sb.append(JoeyLeeConstant.LINE_SEPARATOR);
        sb.append("IP  : ");
        sb.append(request.getRemoteAddr());
        // 请求入参
         /*Enumeration<String> parameterNames = request.getParameterNames();
         while (parameterNames.hasMoreElements()) {
         String name = parameterNames.nextElement();
             sb.append("\n请求参数  : ");
             sb.append(name);
             sb.append(" = ");
             sb.append(request.getParameter(name));
         log.info("请求参数 : " + name + " = " + request.getParameter(name));
         }*/
        sb.append(JoeyLeeConstant.LINE_SEPARATOR);
        sb.append("请求参数  : ");
        sb.append(JSONUtil.toJsonStr(request.getParameterMap()));

        Enumeration<String> headerNames = request.getHeaderNames();
        sb.append(JoeyLeeConstant.LINE_SEPARATOR);
        sb.append("请求头  : ");
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            sb.append(name);
            sb.append(" = ");
            sb.append(request.getHeader(name));
        }
        sb.append(JoeyLeeConstant.LINE_SEPARATOR);
        sb.append("========================================== Request End  ==========================================");
        log.info("request : {}", sb);
    }

    /**
     * 获取方法
     *
     * @param joinPoint
     * @return
     */
    private String getMethod(JoinPoint joinPoint) {
        StringBuffer sb = new StringBuffer();
        sb.append(joinPoint.getSignature().getDeclaringTypeName());
        sb.append(".");
        sb.append(joinPoint.getSignature().getName());
        sb.append("()");
        return sb.toString();
    }

    /**
     * 在切点之后织入
     *
     * @param joinPoint
     * @param startTime
     * @param o
     */
    private void doAfter(ProceedingJoinPoint joinPoint, long startTime, Object o) {
        // 避免返回内容过长，进行截取
        String result = JSONUtil.toJsonStr(o);
        StringBuffer sb = new StringBuffer();
        sb.append(JoeyLeeConstant.LINE_SEPARATOR);
        sb.append(getMethod(joinPoint));
        sb.append(JoeyLeeConstant.LINE_SEPARATOR);
        if (result.length() > 500) {
            sb.append("RESPONSE内容(返回内容过长,已截取) :");
            sb.append(result, 0, 500);
            sb.append("......");
        } else {
            sb.append("RESPONSE内容 :");
            sb.append(result);
        }
        // 执行耗时
        sb.append(JoeyLeeConstant.LINE_SEPARATOR);
        sb.append("method cost : ");
        sb.append(System.currentTimeMillis() - startTime);
        sb.append("ms");
        log.info("response : {}", sb);
    }

    /**
     * 获取切面注解的描述
     *
     * @param joinPoint 切点
     * @return 描述信息
     * @throws Exception
     */
    private String getAspectLogDescription(JoinPoint joinPoint) {

        // 类名
        String targetName = joinPoint.getTarget().getClass().getName();
        // 方法名
        String methodName = joinPoint.getSignature().getName();
        // 参数
        Object[] arguments = joinPoint.getArgs();
        // 加载类
        Class targetClass = ClassUtil.loadClass(targetName);
        // 获取方法
        Method[] methods = targetClass.getMethods();
        StringBuilder description = new StringBuilder("");
        Class[] clazzs;
        for (Method method : methods) {
            // 如果找到该方法
            if (method.getName().equals(methodName)) {
                // 参数列表
                clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    // 获取注解的值
                    description.append(method.getAnnotation(WebLog.class).value());
                    break;
                }
            }
        }
        return description.toString();
    }
}
