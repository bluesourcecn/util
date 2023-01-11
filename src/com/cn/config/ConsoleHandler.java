package com.cn.config;

import java.util.logging.LogRecord;

public class ConsoleHandler extends java.util.logging.ConsoleHandler {
	public ConsoleHandler() {
		super();
		setOutputStream(System.out);
	}
	@Override
	public void publish(LogRecord record) {
		super.publish(record);
	}
	@Override
    public void close() {
        flush();
    }
	
}
