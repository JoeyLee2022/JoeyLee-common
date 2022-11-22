package com.joeylee.common.utils.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.joeylee.common.annotation.EnableJoeyLeeConfiguration;
import com.joeylee.common.domain.interfaces.PostConstructInterface;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;


/**
 * xml 工具类
 *
 * @author joeylee
 */
@Slf4j
@Data
@ToString(callSuper = true)
@Configuration
@ConfigurationProperties(prefix = "joeylee.jackson.xml")
@ConditionalOnProperty(prefix = "joeylee.jackson.xml", name = "enable", havingValue = "true", matchIfMissing = true)
@ConditionalOnBean(annotation = EnableJoeyLeeConfiguration.class)
public class XmlMapperUtils extends BaseJacksonUtils implements PostConstructInterface {
    //设置转换模式 直接输出原始的字段名
    private boolean useStdBeanNaming = true;
    /**
     * 确定注释自省是否
     * 用于配置；如果启用，则配置
     * 将使用{@link AnnotationIntrospector}：如果禁用，
     * 不考虑注释。
     * 默认情况下启用功能。
     */
    private boolean useAnnotations = true;
    /**
     * 一种功能，用于定义Creator属性（通过构造函数或静态工厂方法传递的属性）是否应在未指定显式顺序的其他属性之前排序，以防此类属性使用字母顺序。请注意，在这两种情况下，显式顺序（无论是按名称还是按索引）都优先于此设置。
     * 注意：不适用于java.util。映射序列化（因为条目不被视为Bean/POJO属性。
     * 默认情况下启用功能。
     */
    private boolean sortCreatorPropertiesFirst = true;
    //序列化是否绕根元素，true，则以类名为根元素
    private boolean wrapRootValue = true;
    /**
     * 指定用于包装List和Map属性的 XML 元素
     * 如集合 ListUtil.of("Chinese", "English", "Cantonese")
     * 为true时，序列化成  <list>
     * <Language>Chinese</Language>
     * <Language>English</Language>
     * <Language>Cantonese</Language>
     * </list>
     * 为false时，序列化成
     * <Language>Chinese</Language>
     * <Language>English</Language>
     * <Language>Cantonese</Language>
     */
    private boolean defaultUseWrapper = false;

    public static XmlMapperUtils getSample() {
        return new XmlMapperUtils();
    }

    @Override
    @PostConstruct
    public void postConstruct() {
        log.debug("getMapper");
        mapper = XmlMapper.builder().configure(MapperFeature.USE_STD_BEAN_NAMING, useStdBeanNaming).configure(MapperFeature.USE_ANNOTATIONS, useAnnotations)
                //按属性字母排序
                .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, false).configure(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST, sortCreatorPropertiesFirst).defaultUseWrapper(defaultUseWrapper)
                //要启用JAXB注释处理，请将JaxbAnnotationModule添加到XmlMapper。这允许Jackson使用JAXB注释来提供映射元数据。
                // .addModule(new JaxbAnnotationModule())
                .build();

        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, wrapRootValue);
        super.postConstruct();
    }

    /**
     * 转json 并格式化
     *
     * @param str
     * @return
     */
    public String toPrettyJsonStr(String str) {
        try {
            return getMapper().readTree(str).toPrettyString();
        } catch (JsonProcessingException e) {
            log.error("to pretty json string error ", e);
            throw new RuntimeException("to pretty json string error ", e);
        }
    }

    /**
     * 转json
     *
     * @param str
     * @return
     */
    public String toJsonStr(String str) {
        try {
            return getMapper().readTree(str).toString();
        } catch (JsonProcessingException e) {
            log.error("to json string error ", e);
            throw new RuntimeException("to json string error ", e);
        }
    }


}