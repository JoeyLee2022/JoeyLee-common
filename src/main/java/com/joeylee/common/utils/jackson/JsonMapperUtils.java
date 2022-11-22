package com.joeylee.common.utils.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.joeylee.common.annotation.EnableJoeyLeeConfiguration;
import com.joeylee.common.domain.interfaces.PostConstructInterface;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * jackson 工具类
 *
 * @author JoeyLee
 */
@Slf4j
@Data
@ToString(callSuper = true)
@Configuration
@ConfigurationProperties(prefix = "joeylee.jackson.json")
@ConditionalOnProperty(prefix = "joeylee.jackson.json", name = "enable", havingValue = "true", matchIfMissing = true)
@ConditionalOnBean(annotation = EnableJoeyLeeConfiguration.class)
public class JsonMapperUtils extends BaseJacksonUtils implements PostConstructInterface {
    // 允许单引号的字段名称
    private boolean allowSingleQuotes = true;
    // 允许不带引号的字段名称
    private boolean allowUnquotedFieldNames = true;

    @Autowired(required = false)
    private XmlMapperUtils xmlMapperUtils;

    public static JsonMapperUtils getSample() {
        return new JsonMapperUtils();
    }

    @Override
    @PostConstruct
    public void postConstruct() {
        log.debug("getMapper");
        mapper = JsonMapper.builder().build();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, allowSingleQuotes);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, allowUnquotedFieldNames);
        super.postConstruct();
    }

    /**
     * json转xml
     *
     * @param str
     * @return
     */
    public String jsonToXml(String str) {
        if (xmlMapperUtils == null) {
            xmlMapperUtils = XmlMapperUtils.getSample();
        }
        return xmlMapperUtils.objToStr(strToJsonNode(str));
    }
}