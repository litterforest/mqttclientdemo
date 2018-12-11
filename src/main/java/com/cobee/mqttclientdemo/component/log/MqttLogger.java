package com.cobee.mqttclientdemo.component.log;

public interface MqttLogger {

    void debug(String msg);

    void info(String msg);

    void warn(String msg);

    void warn(String msg, Throwable throwable);

    void error(String msg);

    void error(String msg, Throwable throwable);

}
