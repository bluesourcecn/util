package com.cn.config;

import java.util.Date;
import java.util.function.Function;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.ansi.AnsiColor;
import cn.hutool.core.lang.ansi.AnsiEncoder;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.level.Level;
import cn.hutool.setting.dialect.Props;

public class SimpleFormatter extends Formatter {
	/**
	 * 控制台打印类名的颜色代码
	 */
	private static final AnsiColor COLOR_CLASSNAME = AnsiColor.CYAN;

	/**
	 * 控制台打印时间的颜色代码
	 */
	private static final AnsiColor COLOR_TIME = AnsiColor.WHITE;

	/**
	 * 是否打印行号
	 */
	private static boolean LINENUM = false;
	
	/**
	 * 是否显示方法名
	 */
	private static boolean METHODNAME = false;
	
	/**
	 * 是否启用ansi
	 */
	private static boolean ANSIENABLE = false;

	/**
	 * 控制台打印行数的颜色代码
	 */
	private static final AnsiColor COLOR_LINENUM = AnsiColor.BRIGHT_YELLOW;

	/**
	 * 控制台打印正常信息的颜色代码
	 */
	private static final AnsiColor COLOR_NONE = AnsiColor.DEFAULT;

	private static Function<Level, AnsiColor> colorFactory = (level -> {
		switch (level) {
		case DEBUG:
		case INFO:
			return AnsiColor.GREEN;
		case WARN:
			return AnsiColor.YELLOW;
		case ERROR:
			return AnsiColor.RED;
		case TRACE:
			return AnsiColor.MAGENTA;
		default:
			return COLOR_NONE;
		}
	});

	static {
		Props props = new Props("logging.properties");
		LINENUM = props.getBool("com.cn.toolbox.config.log.ConsoleHandler.formatter.linenum", false);
		METHODNAME = props.getBool("com.cn.toolbox.config.log.ConsoleHandler.formatter.method", false);
		ANSIENABLE = props.getBool("com.cn.toolbox.config.log.ConsoleHandler.ansi.enable", false);
	}

	@Override
	public String format(LogRecord record) {
		StringBuilder logBuilder = new StringBuilder();
		// 时间
		logBuilder.append(ansiHandler(COLOR_TIME, DatePattern.NORM_DATETIME_MS_FORMAT.format(new Date())));
		// 分割
		logBuilder.append(" ");
		// 日志级别
		logBuilder.append(transLevel(record.getLevel()));
		// 分割
		logBuilder.append(" ");
		// 包名以及类名
		logBuilder.append(ansiHandler(COLOR_CLASSNAME, record.getSourceClassName()));
		if (METHODNAME) {
			// 分割
			logBuilder.append(" ");
			// 方法名
			logBuilder.append(ansiHandler(COLOR_CLASSNAME, record.getSourceMethodName()));
		}
		if (LINENUM) {
			// 分割
			logBuilder.append("-");
			// 行数
			logBuilder.append(getLinenum(record.getSourceClassName()));
		}
		// 分割
		logBuilder.append(" : ");
		// 日志内容
		logBuilder.append(ansiHandler(COLOR_NONE, record.getMessage()));
		// 换行
		logBuilder.append(FileUtil.getLineSeparator());
		return logBuilder.toString();
	}

	/**
	 * 转换日志为Hutool-log日志门面中的级别
	 * 
	 * @param level {@link java.util.logging.Leve}
	 * @return Hutool-日志级别
	 */
	private String transLevel(Object level) {
		switch (level.toString()) {
		case "INFO":
			return ANSIENABLE ? AnsiEncoder.encode(colorFactory.apply(Level.INFO), Level.INFO) : Level.INFO.name();
		case "FINE":
			return ANSIENABLE ? AnsiEncoder.encode(colorFactory.apply(Level.DEBUG), Level.DEBUG) : Level.DEBUG.name();
		case "WARNING":
			return ANSIENABLE ? AnsiEncoder.encode(colorFactory.apply(Level.WARN), Level.WARN) : Level.WARN.name();
		case "SEVERE":
			return ANSIENABLE ? AnsiEncoder.encode(colorFactory.apply(Level.ERROR), Level.ERROR) : Level.ERROR.name();
		case "FINEST":
			return ANSIENABLE ? AnsiEncoder.encode(colorFactory.apply(Level.TRACE), Level.TRACE) : Level.TRACE.name();
		default:
			throw new RuntimeException(StrUtil.format("unknown level {}", level));
		}
	}

	/**
	 * 获取行号
	 * 
	 * @param className 需要获取行号的类名
	 * @return 行号
	 */
	private String getLinenum(String className) {
		StackTraceElement[] stackTrace = ThreadUtil.getStackTrace();
		for (StackTraceElement stackTraceElement : stackTrace) {
			if (StrUtil.equals(className, stackTraceElement.getClassName())) {
				return ansiHandler(COLOR_LINENUM, StrUtil.format("[{}]", stackTraceElement.getLineNumber()));
			}
		}
		return ansiHandler(COLOR_LINENUM, "[unknown]");
	}
	
	/**
	 * ansi处理
	 * 
	 * @param color {@link AnsiColor}
	 * @param log  日志
	 * @return ansi处理后的日志
	 */
	private String ansiHandler(AnsiColor color, String log) {
		if (ANSIENABLE) {
			return AnsiEncoder.encode(color, log);
		}
		return log;
	}
}
