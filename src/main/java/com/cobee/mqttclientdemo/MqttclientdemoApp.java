package com.cobee.mqttclientdemo;

import com.cobee.mqttclientdemo.config.MqttConfig;
import com.cobee.mqttclientdemo.net.SimpleAsyncMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

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
        // simpleAsyncMqttClient.pub("school/12ndfloor/2016565656/temperature", "hello world".getBytes());
        // 通配监听事情不能同时处理，只能排队处理。
        simpleAsyncMqttClient.sub("cobee/home/3rdfloor/2016565656/#", new IMqttMessageListener() {

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                System.out.println("=========================messageArrived start============================");
                System.out.println("mqttMessage:" + mqttMessage.toString());
                // Thread.sleep(5000);
                if (1 == 1)
                {
                    // 如果在此方法抛出异常，会马上触发connectionLost方法
                    throw new RuntimeException("hello exception");
                }
                System.out.println("=========================messageArrived end============================");
            }

        });
    }

}
