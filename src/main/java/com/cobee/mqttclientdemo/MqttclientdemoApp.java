package com.cobee.mqttclientdemo;

import com.cobee.mqttclientdemo.config.MqttConfig;
import com.cobee.mqttclientdemo.net.SimpleAsyncMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by Administrator on 2018/12/8.
 */
public class MqttclientdemoApp {

    public static void main(String[] args) throws MqttException, InterruptedException {

        MqttConfig.brokerUrl = "tcp://iot.eclipse.org:1883";
        MqttConfig.username = "";
        MqttConfig.password = "";
        SimpleAsyncMqttClient simpleAsyncMqttClient = SimpleAsyncMqttClient.getInstance();
        System.out.println("12345678");
        simpleAsyncMqttClient.pub("school/12ndfloor/2016565656/temperature", "hello world".getBytes());
    }

}
