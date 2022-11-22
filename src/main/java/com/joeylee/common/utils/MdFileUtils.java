package com.joeylee.common.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * md文件 工具类
 *
 * @author joeylee
 */
@Slf4j
public class MdFileUtils {
    private static final String POUND_KEY = "#";
    private static final String PATTERN = "#.*";
    private static final String TYPE_PATTERN = "```[\\s\\S```]*?```";
    private static final String PREFIX_PATTERN = "```.*";
    private static final String CODE_PREFIX = "```";

    /**
     * 格式化代码块
     *
     * @param sourceFile
     * @param exportFile
     * @param type
     * @param keepOriginalType 是否保留原始类型
     */
    public static void formatCodeStyle(File sourceFile, File exportFile, String type, boolean keepOriginalType) {
        String readUtf8String = FileUtil.readUtf8String(sourceFile);
        // 编译正则表达式
        Pattern patten = Pattern.compile(TYPE_PATTERN);
        // 指定要匹配的字符串
        Matcher matcher = patten.matcher(readUtf8String);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            //匹配到结果字符串
            String group = matcher.group(0);
            //转义特殊字符
            group = Matcher.quoteReplacement(group);
            group = convertGroup(group, type, keepOriginalType);
            String prefix = group.substring(0, 3);
            String suffix = group.substring(3);
            matcher.appendReplacement(sb, prefix + suffix);
        }
        matcher.appendTail(sb);
        FileUtil.writeString(sb.toString(), exportFile, Charset.defaultCharset());
    }

    /**
     * 转换 代码块内容
     *
     * @param requestStr
     * @param keepOriginalType 是否保留原始类型
     * @return
     */
    private static String convertGroup(String requestStr, String type, boolean keepOriginalType) {
        // 编译正则表达式
        Pattern patten = Pattern.compile(PREFIX_PATTERN);
        // 指定要匹配的字符串
        Matcher matcher = patten.matcher(requestStr);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String group = matcher.group();
            if (!keepOriginalType) {
                group = group.substring(0, 3);
            }
            if (group.trim().equals(CODE_PREFIX)) {
                group += type;
            }
            matcher.appendReplacement(sb, group);
            break;
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


    /**
     * 提升目录
     *
     * @param sourceFile 源文件
     * @param exportFile 导出文件
     */
    public static void increaseHeadingLevel(File sourceFile, File exportFile) {
        controlHeadingLevel(sourceFile, exportFile, true);
    }

    /**
     * 降级目录
     *
     * @param sourceFile 源文件
     * @param exportFile 导出文件
     */
    public static void decreaseHeadingLevel(File sourceFile, File exportFile) {
        controlHeadingLevel(sourceFile, exportFile, false);
    }

    /**
     * 提升或降级目录
     *
     * @param sourceFile 源文件
     * @param exportFile 导出文件
     * @param isIncrease true升级，false降级
     */
    private static void controlHeadingLevel(File sourceFile, File exportFile, boolean isIncrease) {
        String readString = FileUtil.readString(sourceFile, Charset.defaultCharset());
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(readString);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            //匹配到结果字符串
            String group = matcher.group();
            if (isIncrease) {
                //升级
                if (StrUtil.count(group, POUND_KEY) == 1) {
                    //如果为1级目录，则不操作
                    log.warn("{} is top level", group);
                    continue;
                }
                matcher.appendReplacement(sb, group.substring(1));
            } else {
                //降级
                matcher.appendReplacement(sb, POUND_KEY + group);
            }
        }
        matcher.appendTail(sb);
        FileUtil.writeString(sb.toString(), exportFile, Charset.defaultCharset());
    }

}

