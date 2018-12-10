package com.cobee.mqttclientdemo;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.util.UUID;

/**
 * Created by Administrator on 2018/12/8.
 */
public class MqttclientdemoApp {

    public static void main(String[] args) throws MqttException, InterruptedException {
        System.out.println("===============MqttclientdemoApp================");

        String tmpDir = System.getProperty("java.io.tmpdir");

        MqttAsyncClient mqttAsyncClient =  new MqttAsyncClient("tcp://iot.eclipse.org:1883", "java_client" + UUID.randomUUID().toString(),
                new MqttDefaultFilePersistence(tmpDir));


        // 1, 连接mqtt服务器
//        mqttAsyncClient.connect();
        mqttAsyncClient.connect(null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {

                System.out.println("已连接到Broker");

            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, final Throwable exception) {

                System.out.println("连接Broker失败:" + exception.getMessage());

            }
        });

        System.out.println("abc");


        Thread.sleep(10000);
        boolean flag = mqttAsyncClient.isConnected();
        System.out.println(flag);
        if (flag)
        {
            mqttAsyncClient.disconnect(null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    System.out.println("关闭已连接到Broker");
                    try {
                        mqttAsyncClient.close();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, final Throwable exception) {

                    System.out.println("关闭连接Broker失败:" + exception.getMessage());

                }
            });
//            for (int i = 0; i < 10; i++)
//            {
//                final int ii = i;
//                new Thread(() -> {
//                    try {
//                        mqttAsyncClient.publish("home/2ndfloor/304/temperature", ("Hello world" + ii).getBytes(), 1, true, null, new IMqttActionListener() {
//
//                            @Override
//                            public void onSuccess(IMqttToken asyncActionToken) {
//
//                                System.out.println("发送成功" + ii);
//
//                            }
//
//                            @Override
//                            public void onFailure(IMqttToken asyncActionToken, final Throwable exception) {
//
//                                System.out.println("发送失败:" + ii + exception.getMessage());
//
//                            }
//
//                        });
//                    } catch (MqttException e) {
//                        e.printStackTrace();
//                    }
//                }).start();
//            }
        }
        else
        {
            mqttAsyncClient.close();
        }

    }

}
