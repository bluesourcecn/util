package com.cn.config;

import java.text.SimpleDateFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LoggerManager {
	private static final Logger logger = LoggerManager.getLogger(LoggerManager.class);

	public static Logger getLogger(Class<?> clazz) {
		Logger logger = new Logger(clazz.getName(), null);
		//控制台控制器
        ConsoleHandler consoleHandler =new ConsoleHandler(); 
        consoleHandler.setLevel(Level.ALL); 
        consoleHandler.setFormatter(new Formatter() {			
			@Override
			public String format(LogRecord record) {
				SimpleDateFormat sdf =new SimpleDateFormat("YYYY-MM-dd HH:mm:ss S");
				StringBuilder sBuilder = new StringBuilder();
				sBuilder.append(sdf.format(record.getMillis())).append(" ");
				sBuilder.append(record.getLevel()).append(":");
				sBuilder.append("[").append(clazz.getSimpleName()).append(":");
				sBuilder.append(logger.getLineNum()).append("] ").append("- ");
				sBuilder.append(record.getMessage()).append("\n");
				return sBuilder.toString();
			}
		});
        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false);
		return logger;		
	}
	
	public static void main(String[] args) {
		logger.info("LoggerManager测试。。。info");
		logger.warning("LoggerManager测试。。。warning");
		logger.severe("LoggerManager测试。。。severe");
	}
}
