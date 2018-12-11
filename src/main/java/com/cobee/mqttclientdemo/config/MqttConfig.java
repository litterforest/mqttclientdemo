package com.cobee.mqttclientdemo.config;

/**
 * mqtt连接配置
 *
 * @author 陈淦森
 */
public class MqttConfig {

    /**
     * 验证类型
     * 1 证书验证
     * 2 账号密码验证
     */
    public static int authType = 2;

    /**
     * 用户名
     */
    public static String username = "admin";

    /**
     * 密码
     */
    public static String password = "public";

    /**
     * URI数组
     */
    public static String[] uris = {"tcp://120.77.246.45:1883"};

    /**
     * mqtt服务器连接地址
     */
    public static String brokerUrl = "tcp://120.77.246.45:1883";

    /**
     * 设备Id
     */
    public final static String deviceId = "441900025";

    /**
     * 下位机，上位机版本
     */
    public final static String version = "1.0.0,1.0.0";

    /**
     * 省
     */
    public final static String province = "440000000000";

    /**
     * 市
     */
    public final static String city = "441900000000";

    /**
     * 区
     */
    public final static String area ="441900004000";

}
