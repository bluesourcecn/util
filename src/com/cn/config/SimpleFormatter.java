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

	private static boolean LINENUM = false;
	
	private static boolean METHODNAME = false;

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
		LINENUM = props.getBool("com.cn.config.ConsoleHandler.formatter.linenum", false);
		METHODNAME = props.getBool("com.cn.config.ConsoleHandler.formatter.method", false);
	}

	@Override
	public String format(LogRecord record) {
		StringBuilder logBuilder = new StringBuilder();
		// 时间
		logBuilder.append(AnsiEncoder.encode(COLOR_TIME, DatePattern.NORM_DATETIME_MS_FORMAT.format(new Date())));
		// 分割
		logBuilder.append(" ");
		// 日志级别
		logBuilder.append(transLevel(record.getLevel()));
		// 分割
		logBuilder.append(" ");
		// 包名以及类名
		logBuilder.append(AnsiEncoder.encode(COLOR_CLASSNAME, record.getSourceClassName()));
		if (METHODNAME) {
			// 分割
			logBuilder.append(" ");
			// 方法名
			logBuilder.append(AnsiEncoder.encode(COLOR_CLASSNAME, record.getSourceMethodName()));
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
		logBuilder.append(AnsiEncoder.encode(COLOR_NONE, record.getMessage()));
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
			return AnsiEncoder.encode(colorFactory.apply(Level.INFO), Level.INFO);
		case "FINE":
			return AnsiEncoder.encode(colorFactory.apply(Level.DEBUG), Level.DEBUG);
		case "WARNING":
			return AnsiEncoder.encode(colorFactory.apply(Level.WARN), Level.WARN);
		case "SEVERE":
			return AnsiEncoder.encode(colorFactory.apply(Level.ERROR), Level.ERROR);
		case "FINEST":
			return AnsiEncoder.encode(colorFactory.apply(Level.TRACE), Level.TRACE);
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
				return AnsiEncoder.encode(COLOR_LINENUM, StrUtil.format("[{}]", stackTraceElement.getLineNumber()));
			}
		}
		return AnsiEncoder.encode(COLOR_LINENUM, "[unknown]");
	}
}
