package com.joeylee.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件 工具类
 *
 * @author: joeylee
 */
@Slf4j
public class FileUtils {


  /**
   * todo 判断文件是否是图片类型
   *
   * @param file 文件路径集合
   * @return
   */
  public static boolean isPicture(File file) {
    return false;
  }

  /**
   * 获取文件行数
   *
   * @param filePathList 文件路径集合
   * @return
   */
  public static int getLinesCount(File... filePathList) {
    List<File> fileList = getAllFiles(filePathList);
    return getTotalLines(fileList);
  }

  /**
   * 获取文件行数
   *
   * @param filePathList 文件路径集合
   * @return
   */
  public static int getLinesCount(String... filePathList) {
    List<File> fileList = getAllFilesByPath(filePathList);
    return getTotalLines(fileList);
  }

  /**
   * 获取文件行数
   *
   * @param fileList 文件路径集合
   * @return
   */
  private static int getTotalLines(List<File> fileList) {
    int totalLines = 0;
    for (File file : fileList) {
      int linesCount = FileUtil.getTotalLines(file);
      log.debug("file : {} , linesCount : {}", file, linesCount);
      totalLines += linesCount;
    }
    return totalLines;
  }

  /**
   * 移动所有文件到指定目录
   *
   * @param newFilePath  要移动的新目录
   * @param filePathList 文件路径集合
   * @return
   */
  public static List<File> moveAllFile(String newFilePath, String... filePathList) {
    if (ObjectUtil.isEmpty(filePathList)) {
      log.error("filePathList : {} was empty", filePathList);
      return null;
    }
    List<File> fileList = new ArrayList<>();
    for (String filePath : filePathList) {
      if (!FileUtil.exist(filePath)) {
        log.error("file : {} not exist", filePath);
        continue;
      }
      if (FileUtil.isDirectory(filePath)) {
        fileList.addAll(FileUtil.loopFiles(filePath, file -> FileUtil.isFile(file)));
      } else {
        File file = FileUtil.file(filePath);
        fileList.add(file);
      }
    }
    FileUtil.mkParentDirs(newFilePath);
    for (File file : fileList) {
      FileUtil.move(file, FileUtil.file(newFilePath + FileUtil.FILE_SEPARATOR + file.getName()),
          true);
    }
    return fileList;
  }

  /**
   * 根据 文件路径集合 移动所有文件到指定目录
   *
   * @param newFilePath  要移动的新目录
   * @param fileType     文件类型
   * @param filePathList 文件路径集合
   * @return
   */
  public static List<File> moveTypeFile(String newFilePath, String fileType,
      String... filePathList) {
    if (ObjectUtil.isEmpty(filePathList)) {
      log.error("filePathList : {} was empty", filePathList);
      return null;
    }
    List<File> fileList = new ArrayList<>();
    for (String filePath : filePathList) {
      if (!FileUtil.exist(filePath)) {
        log.error("file : {} not exist", filePath);
        continue;
      }
      if (FileUtil.isDirectory(filePath)) {
        fileList.addAll(FileUtil.loopFiles(filePath,
            file -> FileUtil.getType(file).equalsIgnoreCase(fileType)));
      } else {
        File file = FileUtil.file(filePath);
        if (FileUtil.getType(file).equalsIgnoreCase(fileType)) {
          fileList.add(file);
        }
      }
    }
    if (ObjectUtil.isEmpty(fileList)) {
      log.error("fileType {} in here paths was not found", fileType);
      return null;
    }
    FileUtil.mkParentDirs(newFilePath);
    for (File file : fileList) {
      FileUtil.move(file, FileUtil.file(newFilePath + FileUtil.FILE_SEPARATOR + file.getName()),
          true);
    }
    return fileList;
  }

  /**
   * 根据 文件路径集合 集合获取所有文件
   *
   * @param filePathList 文件路径集合
   * @return
   */
  public static List<File> getAllFilesByPath(String... filePathList) {
    if (ObjectUtil.isEmpty(filePathList)) {
      return null;
    }
    List<File> fileList = new ArrayList<>();
    for (String filePath : filePathList) {
      if (!FileUtil.exist(filePath)) {
        log.error("file : {} not exist", filePath);
        continue;
      }
      if (FileUtil.isDirectory(filePath)) {
        fileList.addAll(FileUtil.loopFiles(filePath));
      } else {
        fileList.add(FileUtil.file(filePath));
      }
    }
    return fileList;
  }

  /**
   * 根据 文件路径集合 集合获取所有文件
   *
   * @param filePathList 文件路径集合
   * @return
   */
  public static List<File> getAllFilesByPath(List<String> filePathList) {
    if (ObjectUtil.isEmpty(filePathList)) {
      return null;
    }
    List<File> fileList = new ArrayList<>();
    for (String filePath : filePathList) {
      if (!FileUtil.exist(filePath)) {
        log.error("file : {} not exist", filePath);
        continue;
      }
      if (FileUtil.isDirectory(filePath)) {
        fileList.addAll(FileUtil.loopFiles(filePath));
      } else {
        fileList.add(FileUtil.file(filePath));
      }
    }
    return fileList;
  }

  /**
   * 根据 文件路径集合 集合获取所有文件
   *
   * @param fileList 文件路径集合
   * @return
   */
  private static List<File> getAllFiles(File... fileList) {
    if (ObjectUtil.isEmpty(fileList)) {
      return null;
    }
    List<File> returnFileList = new ArrayList<>();
    for (File file : fileList) {
      if (!FileUtil.exist(file)) {
        log.error("file : {} not exist", file);
        continue;
      }
      if (FileUtil.isDirectory(file)) {
        returnFileList.addAll(FileUtil.loopFiles(file));
      } else {
        returnFileList.add(file);
      }
    }
    return returnFileList;
  }

  /**
   * 根据 文件路径集合 集合获取所有文件
   *
   * @param fileList 文件路径集合
   * @return
   */
  public static List<File> getAllFiles(List<File> fileList) {
    if (ObjectUtil.isEmpty(fileList)) {
      return null;
    }
    List<File> returnFileList = new ArrayList<>();
    for (File file : fileList) {
      if (!FileUtil.exist(file)) {
        log.error("file : {} not exist", file);
        continue;
      }
      if (FileUtil.isDirectory(file)) {
        returnFileList.addAll(FileUtil.loopFiles(file));
      } else {
        returnFileList.add(file);
      }
    }
    return returnFileList;
  }

  /**
   * <h1>获取指定文件夹下所有文件，含文件夹</h1>
   *
   * @param dirFilePath 文件夹路径
   * @return
   */
  public static List<File> getAllFiles(String dirFilePath) {
    if (StrUtil.isBlank(dirFilePath)) {
      return null;
    }
    return getAllFiles(new File(dirFilePath));
  }

  /**
   * <h1>获取指定文件夹下所有文件，不含文件夹</h1>
   *
   * @param dirFile 文件夹
   * @return
   */
  private static List<File> getAllFiles(File dirFile) {
    // 如果文件夹不存在或着不是文件夹，则返回 null
    if (Objects.isNull(dirFile) || !dirFile.exists() || dirFile.isFile()) {
      return null;
    }

    File[] childrenFiles = dirFile.listFiles();
    if (Objects.isNull(childrenFiles) || childrenFiles.length == 0) {
      return null;
    }

    List<File> files = new ArrayList<>();
    // 如果时文件，直接添加到结果集合
    for (File childFile : childrenFiles) {
      if (childFile.isFile()) {
        files.add(childFile);
      } else {
        // 如果是文件夹。则先将其添加到结果集合，再将其内部文件添加进结果集合。
        files.add(childFile);
        List<File> cFiles = getAllFiles(childFile);
        if (Objects.isNull(cFiles) || cFiles.isEmpty()) {
          continue;
        }
        files.addAll(cFiles);
      }
    }

    return files;
  }

  public static String getMaxLastModifiedTimeString(String path) {
    return DateUtil.formatDateTime(getMaxLastModifiedTime(new File(path)));
  }

  public static String getMaxLastModifiedTimeString(File file) {
    return DateUtil.formatDateTime(getMaxLastModifiedTime(file));
  }

  public static Date getMaxLastModifiedTime(String path) {
    return getMaxLastModifiedTime(new File(path));
  }

  /**
   * 递归获取文件夹最后获取时间，取最新的
   */
  private static Date getMaxLastModifiedTime(File file) {
    List<File> loopFiles = getAllFiles(file);
    List<Date> dateList = loopFiles.stream().map(f -> FileUtil.lastModifiedTime(f)).sorted()
        .collect(Collectors.toList());
    Date max = CollectionUtil.max(dateList);
    return max;
  }

}