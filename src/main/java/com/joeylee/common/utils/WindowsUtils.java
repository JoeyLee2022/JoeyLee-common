package com.joeylee.common.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.RuntimeUtil;
import java.io.File;
import lombok.extern.slf4j.Slf4j;

/**
 * windows系统 工具类
 *
 * @author: joeylee
 */
@Slf4j
public class WindowsUtils {

  /**
   * 打开文件
   *
   * @param filePtah
   */
  public static void openFile(String filePtah) {
    if (!FileUtil.exist(filePtah)) {
      String error = StrFormatter.format("file not exist : {}", filePtah);
      log.error(error);
      throw new RuntimeException(error);
    }
    String cmd = StrFormatter.format("cmd.exe /c start \"\" \"{}\"", filePtah);
    // 打开文件
    RuntimeUtil.exec(cmd);
  }

  /**
   * 打开文件
   *
   * @param file
   */
  public static void openFile(File file) {
    openFile(file.getPath());
  }
}