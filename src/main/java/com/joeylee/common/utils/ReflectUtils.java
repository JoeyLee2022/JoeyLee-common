package com.joeylee.common.utils;

import cn.hutool.core.util.StrUtil;
import java.io.File;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import lombok.extern.slf4j.Slf4j;

/**
 * 反射 工具类
 *
 * @author: joeylee
 */
@Slf4j
public class ReflectUtils {
  /*
   *//**
   * 根据包名获取包下面所有的类名
   *
   * @param pack
   * @return
   *//*
    private static Set<Class<?>> getClasses(String pack) {

        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    System.out.println("jar类型的扫描");
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        findClassesInPackageByJar(packageName, entries, packageDirName, recursive, classes);
                    } catch (IOException e) {
                        // log.error("在扫描用户定义视图时从jar包获取文件出错");
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    *//**
   * 以文件的形式来获取包下的所有Class
   *
   * @param packageName
   * @param packagePath
   * @param recursive
   * @param classes
   *//*
    private static void findClassesInPackageByFile(String packageName, String packagePath, boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        File[] dirfiles = dir.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));
        // 循环所有文件
        // 如果是目录 则继续扫描
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    // classes.add(Class.forName(packageName + '.' +
                    // className));
                    // 经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                    e.printStackTrace();
                }
            }
        }
    }

    *//**
   * 以jar的形式来获取包下的所有Class
   *
   * @param packageName
   * @param entries
   * @param packageDirName
   * @param recursive
   * @param classes
   *//*
    private static void findClassesInPackageByJar(String packageName, Enumeration<JarEntry> entries, String packageDirName,
                                                  boolean recursive, Set<Class<?>> classes) {
        // 同样的进行循环迭代
        while (entries.hasMoreElements()) {
            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            // 如果是以/开头的
            // 获取后面的字符串
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }
            // 如果前半部分和定义的包名相同
            if (name.startsWith(packageDirName)) {
                int idx = name.lastIndexOf('/');
                // 如果以"/"结尾 是一个包
                // 获取包名 把"/"替换成"."
                if (idx != -1) {
                    packageName = name.substring(0, idx).replace('/', '.');
                }
                // 如果可以迭代下去 并且是一个包
                // 如果是一个.class文件 而且不是目录
                if ((idx != -1) || recursive) {
                    if (name.endsWith(".class") && !entry.isDirectory()) {
                        // 去掉后面的".class" 获取真正的类名
                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                        try {
                            // 添加到classes
                            classes.add(Class.forName(packageName + '.' + className));
                        } catch (ClassNotFoundException e) {
                            // .error("添加用户自定义视图类错误 找不到此类的.class文件");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }*/

  /**
   * 获取类包下的所有子类
   *
   * @param type
   * @param <T>
   * @return
   */
  public static <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type) {
    Set<Class<?>> classes = getClasses(type.getPackage().getName());
    Set<Class<? extends T>> collect = new HashSet<>();
    for (Class<?> aClass : classes) {
      if (!type.equals(aClass) && type.isAssignableFrom(aClass) && !aClass.isInterface() &&
          !Modifier.isAbstract(aClass.getModifiers())) {
        collect.add((Class<? extends T>) aClass);
      }
    }

    return collect;
  }

  /**
   * 获取某个包下的所有类
   */
  private static Set<Class<?>> getClasses(String packageName) {
    Set<Class<?>> classSet = new HashSet<>();
    try {
      String sourcePath = packageName.replace(".", "/");
      Enumeration<URL> urls = Thread.currentThread().getContextClassLoader()
          .getResources(sourcePath);
      while (urls.hasMoreElements()) {
        URL url = urls.nextElement();
        if (url != null) {
          String protocol = url.getProtocol();
          if ("file".equals(protocol)) {
            String packagePath = url.getPath().replaceAll("%20", " ");
            addClass(classSet, packagePath, packageName);
          } else if ("jar".equals(protocol)) {
            JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
            if (jarURLConnection != null) {
              JarFile jarFile = jarURLConnection.getJarFile();
              if (jarFile != null) {
                Enumeration<JarEntry> jarEntries = jarFile.entries();
                while (jarEntries.hasMoreElements()) {
                  JarEntry jarEntry = jarEntries.nextElement();
                  String jarEntryName = jarEntry.getName();
                  if (jarEntryName.contains(sourcePath) && jarEntryName.endsWith(".class")) {
                    String className = jarEntryName.substring(0, jarEntryName.lastIndexOf("."))
                        .replaceAll("/", ".");
                    doAddClass(classSet, className);
                  }
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      log.error("获取子类失败", e);
    }

    return classSet;
  }

  private static void addClass(Set<Class<?>> classSet, String packagePath, String packageName) {
    File[] files = new File(packagePath).listFiles(
        file -> (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory());
    for (File file : files) {
      String fileName = file.getName();
      if (file.isFile()) {
        String className = fileName.substring(0, fileName.lastIndexOf("."));
        if (StrUtil.isNotEmpty(packageName)) {
          className = packageName + "." + className;
        }
        doAddClass(classSet, className);
      } else {
        String subPackagePath = fileName;
        if (StrUtil.isNotEmpty(packagePath)) {
          subPackagePath = packagePath + "/" + subPackagePath;
        }
        String subPackageName = fileName;
        if (StrUtil.isNotEmpty(packageName)) {
          subPackageName = packageName + "." + subPackageName;
        }
        addClass(classSet, subPackagePath, subPackageName);
      }
    }
  }

  /**
   * 加载类
   */
  private static Class<?> loadClass(String className, boolean isInitialized) {
    Class<?> cls;
    try {
      cls = Class.forName(className, isInitialized, Thread.currentThread().getContextClassLoader());
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    return cls;
  }

  /**
   * 加载类（默认将初始化类）
   */
  public static Class<?> loadClass(String className) {
    return loadClass(className, true);
  }

  private static void doAddClass(Set<Class<?>> classSet, String className) {
    Class<?> cls = loadClass(className, false);
    classSet.add(cls);
  }

}


