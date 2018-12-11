package com.cobee.mqttclientdemo.exception;

/**
 * mqttclient初始化和连接异常
 * @author 陈淦森
 */
public class MqttClientInitException extends RuntimeException {

    public MqttClientInitException() {
        super();
    }

    public MqttClientInitException(String message) {
        super(message);
    }

    public MqttClientInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public MqttClientInitException(Throwable cause) {
        super(cause);
    }

    protected MqttClientInitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
