package com.cn.util;

import com.cn.config.Logger;
import com.cn.config.LoggerManager;

public class StringUtil {
    private static final Logger logger = LoggerManager.getLogger(StringUtil.class);

    /**
     * -判断是否为空字符串最优代码
     * 
     * @param str 字符串
     * @return true false
     */
    public static Boolean isEmpty(String str) {
        return (str == null || str.trim().length() == 0);
    }

    /**
     * -判断是否不为空字符串最优代码
     * 
     * @param str 字符串
     * @return true false
     */
    public static Boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * -将传入的对象转换成字符串
     * 
     * @param obj
     * @return 转换后字符串
     */
    public static String toString(Object obj) {
        return (obj == null ? "" : obj.toString());
    }

    public static void main(String[] args) {
        logger.info(toString(isEmpty("")));
    }
}