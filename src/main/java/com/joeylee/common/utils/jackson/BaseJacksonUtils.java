package com.joeylee.common.utils.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;


/**
 * jackson 工具类
 *
 * @author joeylee
 */
@Data
@Slf4j
@ToString(callSuper = true)
public abstract class BaseJacksonUtils {
    protected ObjectMapper mapper;
    //日期时间格式
    protected String defaultDateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    //日期格式
    protected String defaultDateFormat = "yyyy-MM-dd";
    //忽略字符串存在，对象不存在的属性,反序列化时，若实体类没有对应的属性，是否抛出JsonMappingException异常，false忽略掉
    protected boolean failOnUnknownProperties = false;
    //字段为null，自动忽略，不再序列化
    protected boolean includeNonNull = true;
    // 若为true遇到空对象则失败
    protected boolean failOnEmptyBeans = false;

    /**
     * 压缩
     *
     * @param str
     */
    public String compress(String str) {
        try {
            return getMapper().writeValueAsString(getMapper().readTree(str));
        } catch (JsonProcessingException e) {
            log.error("compress error ", e);
            throw new RuntimeException("compress error ", e);
        }
    }

    /**
     * 格式化
     *
     * @param str
     */
    public String pretty(String str) {
        try {
            return getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(getMapper().readTree(str));
        } catch (JsonProcessingException e) {
            log.error("pretty error ", e);
            throw new RuntimeException("pretty error ", e);
        }
    }

    /**
     * 对象转字符串 并格式化
     *
     * @param obj 需要转换的对象
     */
    public String objToPrettyStr(Object obj) {
        try {
            return getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("object to pretty string error ", e);
            throw new RuntimeException("object to pretty string error ", e);
        }
    }

    /**
     * 对象转字符串
     *
     * @param obj
     * @return
     */
    public String objToStr(Object obj) {
        try {
            return getMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("object to string error ", e);
            throw new RuntimeException("object to string error ", e);
        }
    }

    /**
     * 字符串转JsonNode对象
     *
     * @param str 需要转换的字符串
     * @param <T>
     * @return JsonNode
     */
    public JsonNode strToJsonNode(String str) {
        try {
            return getMapper().readTree(str);
        } catch (JsonProcessingException e) {
            log.error("string to JsonNode error ", e);
            throw new RuntimeException("string to JsonNode error ", e);
        }
    }

    /**
     * 字符串转对象
     *
     * @param str
     * @param c
     * @param <T>
     * @return
     */

    public <T> T strToObj(String str, Class<T> c) {
        try {
            return getMapper().readValue(str, c);
        } catch (IOException e) {
            log.error("string to object error ", e);
            throw new RuntimeException("string to object error ", e);
        }
    }

    /**
     * 字符串转对象
     *
     * @param str           需要转换的字符串
     * @param typeReference 自定义class对象
     * @param <T>
     * @return 自定义对象
     */
    public <T> T strToObj(String str, TypeReference<T> typeReference) {
        try {
            return getMapper().readValue(str, typeReference);
        } catch (IOException e) {
            log.error("string to object error ", e);
            throw new RuntimeException("string to object error ", e);
        }
    }

    public void postConstruct() {
        // 处理java.util.Date的格式，所有的日期都统一用yyyy-MM-dd HH:mm:ss格式
        mapper.setDateFormat(new SimpleDateFormat(getDefaultDateTimeFormat()));
        // 设置时区 getTimeZone("GMT+8:00")
        mapper.setTimeZone(TimeZone.getDefault());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProperties);
        if (includeNonNull) {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, failOnEmptyBeans);


        /**
         * 功能等效于的便利方法：mapper.registerModules（mapper.findModules（））；
         * 与findModules（）一样，没有对模块进行缓存，因此需要注意创建和共享单个映射器实例；或者缓存自省的模块集。
         * 使用JDK ServiceLoader工具以及模块提供的SPI查找可用方法的方法。
         * 请注意，该方法不做任何缓存，所以应该认为调用可能代价高昂。
         */
        //mapper.findAndRegisterModules();
        //处理 LocalDateTime 格式
        JavaTimeModule timeModule = new JavaTimeModule();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(getDefaultDateTimeFormat(), Locale.CHINA);
        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));

        //处理 LocalDate 格式
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(getDefaultDateFormat(), Locale.CHINA);
        timeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        mapper.registerModule(timeModule);
    }


}