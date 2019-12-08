package com.cn.util;

import com.cn.config.Logger;
import com.cn.config.LoggerManager;
import java.util.UUID;

public class UuidUtil {
    private static final Logger logger = LoggerManager.getLogger(UuidUtil.class);

    /**
     * -获取UUID
     * 
     * @return 获取到的UUID
     */
    public static String getUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * -获取不含"-"的UUID
     * 
     * @return 获取到的UUID
     */
    public static String getCharNumUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * -获取固定位数的UUID
     * 
     * @param i 获取UUID的位数
     * @return
     */
    public static String getCharNumUuid(int i) {
        return getCharNumUuid().substring(0, i);
    }

    public static void main(String[] args) {
        logger.info("UUID:" + getUuid());
        logger.info("UUID:" + getCharNumUuid());
        logger.info("UUID:" + getCharNumUuid(8));
    }

}
