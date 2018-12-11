package com.cobee.mqttclientdemo.net;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * 定义系统mqttclient的规范方法，维护连接的方法在实现类中定义并且封装起来，不需要对外公开。
 *
 * @author 陈淦森
 */
public interface IMqttClient {

    /**
     * 发布消息，qos默认是1
     *
     * @param topic
     * @param data
     */
    void pub(String topic, byte[] data);

    /**
     * 发布消息
     *
     * @param topic
     * @param data
     */
    void pub(final int qos, final boolean retained, final String topic, final byte[] data);

    /**
     * 订阅消息，qos默认是1
     *
     * @param topic
     */
    void sub(String topic);

    /**
     * 订阅消息，自定义qos
     *
     * @param topic
     * @param qos
     */
    void sub(String topic, int qos);

    /**
     * 退订
     * @param topic
     */
    void unsub(String topic);

    /**
     * 订阅消息，并设置处理类
     *
     * @param topic
     * @param listener
     */
    void sub(String topic, IMqttMessageListener listener);

    /**
     * 订阅多个主题，并且自定义qos
     *
     * @param topic
     * @param qos
     */
    void sub(String[] topic, int[] qos);

    /**
     * 批量退定主题
     *
     * @param topics
     */
    void unsub(String[] topics);

}
