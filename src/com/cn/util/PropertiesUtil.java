package com.cn.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Logger;

import com.cn.config.LoggerManager;

public class PropertiesUtil {
	private static final Logger logger = LoggerManager.getLogger(PropertiesUtil.class);

	private static Properties props;

	/**
	 * -初始化配置文件
	 * 
	 * @param file 需要载入的文件
	 */
	public static void init(String file) {
		props = new Properties();
		try {
			if (file == null) {
				logger.info("配置文件不能为空");
			}
			logger.info("载入配置文件:" + file);
			props.load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		} catch (IOException e) {
			logger.severe("配置文件读取异常。。。");
			e.printStackTrace();
		}
	}

	/**
	 * -通过键值获取属性值
	 * 
	 * @param key key
	 * @return value
	 */
	public static String getProperty(String key) {
		String value = props.getProperty(key.trim());
		if (value == null || "".equals(value)) {
			return null;
		}
		return value.trim();
	}

	/**
	 * -通过键值获取属性值
	 * 
	 * @param key          key
	 * @param defaultValue 默认值
	 * @return value
	 */
	public static String getProperty(String key, String defaultValue) {
		String value = props.getProperty(key.trim());
		if (value == null || "".equals(value)) {
			value = defaultValue;
		}
		return value.trim();
	}

}
