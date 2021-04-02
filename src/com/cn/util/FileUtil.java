package com.cn.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.cn.config.Logger;
import com.cn.config.LoggerManager;

public class FileUtil {
    private static final Logger logger = LoggerManager.getLogger(FileUtil.class);

    /**
     * 获取某路径下的所有文件（包括该路径下的文件夹下的文件）
     * 
     * @param path 路径
     * @return
     */
    public static List<String> getAllFiles(String path) {
        List<String> files = new ArrayList<>();
        return getAllFiles(path, files);
    }

    /**
     * 获取某路径下的所有文件
     * 
     * @param path 当先路径
     * @param files 已有的文件
     * @return
     */
    public static List<String> getAllFiles(String path, List<String> files) {
        File file = new File(path);
        File[] fileArry = file.listFiles();
        for (File f : fileArry) {
            if (f.isFile()) {
                files.add(path + File.separator + f.getName());
            } else if (f.isDirectory()) {
                getAllFiles(f.getPath(), files);
            }
        }
        return files;
    }


    /**
     * 获取文件内容
     * 
     * @param file 文件路径
     * @return 文件内容
     */
    public static String getFileContent(String file) {
        StringBuilder content = new StringBuilder();
        File fileObject = new File(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(fileObject))) {
            while (reader.readLine() != null) {
                content.append(reader.readLine());
            }
        } catch (Exception e) {
            logger.severe("Parse file error!");
            logger.severe(e.getMessage());
            e.printStackTrace();
        }
        return content.toString();
    }

    public static void main(String[] args) {
        String path = "D:\\Users\\Documents\\eclipse\\workplaces\\workplace03\\util\\src\\com\\cn";
        List<String> files = getAllFiles(path);
        for (String file : files) {
            logger.info(file);
        }
        // String file = path + "\\config\\LoggerManager.java";
        // String content = getFileContent(file);
        // logger.info(content);
    }
}
