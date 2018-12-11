package com.cobee.mqttclientdemo.component.log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志工厂
 *
 * @author 陈淦森
 */
public abstract class MqttLoggerFactory {

    private static final String DEBUG = "debug";
    private static final String INFO = "info";
    private static final String WARN = "warn";
    private static final String ERROR = "error";

    private static MqttLogger mqttLogger;

    static
    {
        mqttLogger = new MqttLogger(){

            public void debug(String msg) {
                println(DEBUG, msg);
            }

            public void info(String msg) {
                println(INFO, msg);
            }

            public void warn(String msg) {
                println(WARN, msg);
            }

            public void warn(String msg, Throwable throwable) {
                println(WARN, msg);
                throwable.printStackTrace();
            }

            public void error(String msg) {
                println(ERROR, msg);
            }

            public void error(String msg, Throwable throwable) {
                println(ERROR, msg);
                throwable.printStackTrace();
            }
        };
    }

    private MqttLoggerFactory(){}

    public static void setLogger(MqttLogger mqttLogger) {
        MqttLoggerFactory.mqttLogger = mqttLogger;
    }

    public static MqttLogger getLogger() {
        return MqttLoggerFactory.mqttLogger;
    }

    private static SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private static void println(String level, String msg)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(fm.format(new Date())).append(" ").append(level).append(" ");
        try {
            StackTraceElement st = Thread.currentThread().getStackTrace()[4];
            sb.append("[").append(st.getClassName()).append("]").append(" - ").append(st.getMethodName()).append("(").append(st.getFileName()).append(":").append(st.getLineNumber()).append(")");
        } catch (Exception e) {
        }
        sb.append(": ").append(msg);
        System.out.println(sb.toString());
    }

}
