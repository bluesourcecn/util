package com.cn.config;

import java.util.logging.Level;

public class Logger extends java.util.logging.Logger {

    private Integer lineNum;


    public Integer getLineNum() {
        return this.lineNum == null ? 0 : this.lineNum;
    }

    protected Logger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

    @Override
    public void info(String msg) {
        this.lineNum = getCurrentLineNumber();
        log(Level.INFO, msg);
    }

    @Override
    public void warning(String msg) {
        this.lineNum = getCurrentLineNumber();
        log(Level.WARNING, msg);
    }

    @Override
    public void severe(String msg) {
        this.lineNum = getCurrentLineNumber();
        log(Level.SEVERE, msg);
    }


    private Integer getCurrentLineNumber() {
        return Thread.currentThread().getStackTrace()[3].getLineNumber();
    }

}