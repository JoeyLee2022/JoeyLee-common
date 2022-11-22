package com.joeylee.common.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.poi.word.Word07Writer;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.BreakClear;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.List;

/**
 * word 工具类
 *
 * @author joeylee
 * @date 2022年9月2日11:31:10
 */
@Slf4j
public class WordUtils {


    /**
     * 将行集合数据 写出word
     *
     * @param path       文件路径
     * @param stringList 文件内容
     * @param isOpen     写完后是否打开
     */
    public static void writeWord(String path, List<String> stringList, boolean isOpen) {
        Word07Writer writer = new Word07Writer();

        // XWPFDocument代表一个docx文档，其可以用来读docx文档，也可以用来写docx文档
        XWPFDocument docx = writer.getDoc();

        // XWPFParagraph代表文档、表格、标题等种的段落，由多个XWPFRun组成
        XWPFParagraph paragraph = docx.createParagraph();
        // XWPFRun代表具有同样风格的一段文本
        XWPFRun run = paragraph.createRun();
        for (String s : stringList) {
            int pages = docx.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
            log.debug("pages: {} ", pages);
            if (pages >= 70) {
                break;
            }
            setRun(paragraph, run, s, false, true);
        }

        writer.flush(FileUtil.file(path));
        // 关闭
        writer.close();
        log.info("输出文件: {} 完成", path);
        // 打开文件
        if (isOpen) {
            String cmd = StrFormatter.format("cmd.exe /c start \"\" \"{}\"", path);
            RuntimeUtil.exec(cmd);
        }
    }

    /**
     * 将文本内容 写出word
     *
     * @param path    文件路径
     * @param content 文件内容
     * @param isOpen  写完后是否打开
     */
    public static void writeWord(String path, String content, boolean isOpen) {
        Word07Writer writer = new Word07Writer();

        // XWPFDocument代表一个docx文档，其可以用来读docx文档，也可以用来写docx文档
        XWPFDocument doc = writer.getDoc();

        // XWPFParagraph代表文档、表格、标题等种的段落，由多个XWPFRun组成
        XWPFParagraph paragraph = doc.createParagraph();
        // XWPFRun代表具有同样风格的一段文本
        XWPFRun run = paragraph.createRun();
        setRun(paragraph, run, content, false, true);

        writer.flush(FileUtil.file(path));
        // 关闭
        writer.close();
        log.info("输出文件: {} 完成", path);
        // 打开文件
        if (isOpen) {
            String cmd = StrFormatter.format("cmd.exe /c start \"\" \"{}\"", path);
            RuntimeUtil.exec(cmd);
        }

    }

    /**
     * 设置样式
     *
     * @param paragraph
     * @param run
     * @param text      文本
     * @param bold      是否添加缩进
     */
    private static void setRun(XWPFParagraph paragraph, XWPFRun run, String text, boolean bold, boolean addTab) {
        if (addTab) {
            run.addTab();
        }
        run.setText(text);
        // 设置行间距
        run.setTextPosition(1);
        // 对齐方式
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        // 加粗
        run.setBold(bold);
        // 设置颜色--十六进制
        run.setColor("000000");
        // 字体
        run.setFontFamily("微软雅黑");
        run.setFontSize(10);// 字体大小run = paragraph.createRun();
        // 换行 BreakType
            /*COLUMN
            指定当文档在页面视图中显示时，当前中断应在当前页面上可用的下一列上重新开始。
            PAGE
            指定当文档在页面视图中显示时，当前中断应在文档的下一页上重新开始。
            TEXT_WRAPPING
            指定当文档在页面视图中显示时，当前中断应在文档的下一行重新开始。*/
        //run.addBreak(BreakType.TEXT_WRAPPING);
        // 换行 BreakClear
        /*ALL
        指定文本换行符应将文本推进到 WordprocessingML 文档中的下一行，该行跨越该行的整个宽度。
        LEFT
        指定文本换行中断的行为如下： 如果此行分为多个区域（页面中心的浮动对象在两侧都有文本换行：如果这是此行上文本流的最左侧区域，则前进文本到该行的下一个位置 否则，将其视为 all 类型的文本换行中断。
        NONE
        指定文本换行符应将文本推进到 WordprocessingML 文档中的下一行，无论其位置从左到右或是否存在与该行相交的任何浮动对象，
        RIGHT
        指定文本换行中断的行为如下： 如果该行被分成多个区域（页面中心的浮动对象在两侧都有文本换行：如果这是该行上文本流的最右侧区域，则前进文本到下一行的下一个位置 否则，将其视为 all 类型的文本换行中断。
        方法*/
        //软回车
        run.addBreak(BreakClear.NONE);
        //硬回车
        run.addCarriageReturn();
    }


}