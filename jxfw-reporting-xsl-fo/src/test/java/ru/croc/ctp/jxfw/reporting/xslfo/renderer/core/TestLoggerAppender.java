package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.List;


public class TestLoggerAppender extends AppenderBase<ILoggingEvent> {

    public static List<ILoggingEvent> events = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent eventObject) {
        events.add(eventObject);
    }
}
