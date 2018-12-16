package com.cobee.mqttclientdemo.net;

import com.cobee.mqttclientdemo.component.log.MqttLogger;
import com.cobee.mqttclientdemo.component.log.MqttLoggerFactory;
import com.cobee.mqttclientdemo.config.MqttConfig;
import com.cobee.mqttclientdemo.exception.MqttClientInitException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 封装的异步 mqttclient，单例模式。内部维护连接状态
 *
 * @author 陈淦森
 */
public class SimpleAsyncMqttClient implements IMqttClient {

    private static final int ERROR_CODE_DISCONNECTED = 32104;

    private static MqttLogger mqttLogger = MqttLoggerFactory.getLogger();

    private static SimpleAsyncMqttClient instance;

    static
    {
        instance = new SimpleAsyncMqttClient(MqttConfig.brokerUrl, null, false, MqttConfig.username, MqttConfig.password);
    }

    private MqttAsyncClient client;
    private String brokerUrl;
    private boolean cleanSession;
    private String password;
    private String userName;
    private String clientId;
    private MqttConnectOptions mqttConnectOptions;
    private DefaultMqttCallback defaultMqttCallback = new DefaultMqttCallback();
    // 锁对象
    private Object waiter = new Object();
    // 是否执行下一个命令
    private boolean donext = false;
    private Throwable ex = null;

    private int state = BEGIN;

    private static final int BEGIN = 0;
    private static final int CONNECTED = 1;
    private static final int PUBLISHED = 2;
    private static final int SUBSCRIBED = 3;
    private static final int DISCONNECTED = 4;
    private static final int FINISH = 5;
    private static final int ERROR = 6;
    private static final int DISCONNECT = 7;

    private SimpleAsyncMqttClient(){}

    private SimpleAsyncMqttClient(String brokerUrl, String clientId, boolean cleanSession, String userName, String password)
    {
        this.brokerUrl = brokerUrl;
        this.cleanSession = cleanSession;
        this.password = password;
        this.userName = userName;
        if (StringUtils.isNotBlank(clientId))
        {
            this.clientId = clientId;
        }
        else
        {
            this.clientId = "android" + UUID.randomUUID().toString();
        }

        String tmpDir = System.getProperty("java.io.tmpdir");
        // 持久化到磁盘上面
        MqttClientPersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
        // 数据放到内存中
        // MqttClientPersistence dataStore = new MemoryPersistence();

        try {
            mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(cleanSession);
            // 设置自动重连
            mqttConnectOptions.setAutomaticReconnect(true);
            if(StringUtils.isNotBlank(password)) {
                mqttConnectOptions.setPassword(this.password.toCharArray());
            }
            if(StringUtils.isNotBlank(userName)) {
                mqttConnectOptions.setUserName(this.userName);
            }
            client = new MqttAsyncClient(this.brokerUrl,this.clientId, new MemoryPersistence());
            client.setCallback(defaultMqttCallback);
            connect();
        }
        catch (MqttException e)
        {
            mqttLogger.error("Unable to set up client: "+e.toString());
            throw new MqttClientInitException(e);
        }
    }

    public static SimpleAsyncMqttClient getInstance()
    {
        return instance;
    }

    @Override
    public void pub(String topic, byte[] data) {
        waiteForConnected();
        try {
            client.publish(topic, data, 1, true);
        } catch (MqttException e) {
            int code = e.getReasonCode();
            setDisconnected(code);
            mqttLogger.error("code:" + code, e);
        }
    }

    @Override
    public void pub(int qos, boolean retained, String topic, byte[] data) {

        waiteForConnected();
        try {
            client.publish(topic, data, qos, retained);
        } catch (MqttException e) {
            int code = e.getReasonCode();
            setDisconnected(code);
            mqttLogger.error("code:" + code, e);
        }

    }

    @Override
    public void sub(String topic) {

        waiteForConnected();
        try {
            client.subscribe(topic, 1);
        } catch (MqttException e) {
            int code = e.getReasonCode();
            setDisconnected(code);
            mqttLogger.error("code:" + code, e);
        }

    }

    @Override
    public void sub(String topic, int qos) {

        waiteForConnected();
        try {
            client.subscribe(topic, qos);
        } catch (MqttException e) {
            int code = e.getReasonCode();
            setDisconnected(code);
            mqttLogger.error("code:" + code, e);
        }

    }

    @Override
    public void unsub(String topic) {

        waiteForConnected();
        try {
            client.unsubscribe(topic);
        } catch (MqttException e) {
            int code = e.getReasonCode();
            setDisconnected(code);
            mqttLogger.error("code:" + code, e);
        }

    }

    /**
     * 默认qos是1
     *
     * @param topic
     * @param listener
     */
    @Override
    public void sub(String topic, IMqttMessageListener listener) {

        waiteForConnected();
        try {
            client.subscribe(topic, 1, listener);
        } catch (MqttException e) {
            int code = e.getReasonCode();
            setDisconnected(code);
            mqttLogger.error("code:" + code, e);
        }

    }

    @Override
    public void sub(String[] topic, int[] qos) {

        waiteForConnected();
        try {
            client.subscribe(topic, qos);
        } catch (MqttException e) {
            int code = e.getReasonCode();
            setDisconnected(code);
            mqttLogger.error("code:" + code, e);
        }

    }

    @Override
    public void unsub(String[] topics) {

        waiteForConnected();
        try {
            client.unsubscribe(topics);
        } catch (MqttException e) {
            int code = e.getReasonCode();
            setDisconnected(code);
            mqttLogger.error("code:" + code, e);
        }

    }

    public void disconnect() {
        waiteForConnected();
        try {
            client.disconnect(null, new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mqttLogger.info("================================== 成功断开连接 ==================================");
                    state = DISCONNECTED;
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    mqttLogger.info("================================== 失败断开连接 ==================================");
                }

            }).waitForCompletion();
        } catch (MqttException e) {
            mqttLogger.error("", e);
        }
    }

    /**
     * 等待连接建立
     */
    private void waiteForConnected()
    {
        while(state != CONNECTED)
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                mqttLogger.error("", e);
            }
        }
    }

    /**
     * 设置客户端状态
     *
     * @param code
     */
    private void setDisconnected(int code)
    {
        if (code == ERROR_CODE_DISCONNECTED)
        {
            state = DISCONNECTED;
        }
    }


    /**
     * 准备连接到mqtt服务器的选项
     */
    @Deprecated
    private void prepareConnection()
    {
        String tmpDir = System.getProperty("java.io.tmpdir");
        // 持久化到磁盘上面
        MqttClientPersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
        // 数据放到内存中
        // MqttClientPersistence dataStore = new MemoryPersistence();

        try {
            mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(cleanSession);

            if(StringUtils.isNotBlank(password)) {
                mqttConnectOptions.setPassword(this.password.toCharArray());
            }

            if(StringUtils.isNotBlank(userName)) {
                mqttConnectOptions.setUserName(this.userName);
            }

            client = new MqttAsyncClient(this.brokerUrl,this.clientId, new MemoryPersistence());

            client.setCallback(defaultMqttCallback);

        }
        catch (MqttException e)
        {
            mqttLogger.error("Unable to set up client: "+e.toString());
            System.exit(1);
        }
    }

    private Lock lock = new ReentrantLock();

    /**
     * 执行连接
     * 如果已连接上，则忽略
     *
     * @throws MqttException
     */
    public void connect() throws MqttException {

        if (client != null && !client.isConnected())
        {
            lock.lock();
            try {
                if (client != null && !client.isConnected())
                {
                    client.connect(mqttConnectOptions,"Connect sample context", new IMqttActionListener() {

                        public void onSuccess(IMqttToken asyncActionToken) {
                            System.out.println("连接broker成功");
                            state = CONNECTED;
                        }

                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            System.out.println("连接broker失败");
                        }

                    }).waitForCompletion();
                }
            } finally {
                lock.unlock();
            }
        }

    }

    /**
     * 默认的实现回调试
     *
     * @author 陈淦森
     */
    private class DefaultMqttCallback implements MqttCallback {

        /**
         * mqtt连接丢失之后，实现重连
         *
         * @param throwable
         */
        @Override
        public void connectionLost(Throwable throwable) {
            state = DISCONNECTED;
            if ((client != null && !client.isConnected()))
            {
                mqttLogger.info("================================== DefaultMqttCallback#connectionLost 开始设置重连状态 ==================================");
                try {
                    connect();
                } catch (MqttException e) {
                    mqttLogger.error("", e);
                }
                mqttLogger.info("================================== DefaultMqttCallback#connectionLost 结束设置重连状态 ==================================");
            }
            mqttLogger.info("==================================DefaultMqttCallback#connectionLost==================================");
        }

        @Override
        public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
            mqttLogger.info("==================================DefaultMqttCallback#messageArrived==================================");
            mqttLogger.info("==================================DefaultMqttCallback#messageArrived: topic:" + topic + "==================================");
            mqttLogger.info("==================================DefaultMqttCallback#messageArrived: mqttMessage:" + mqttMessage.toString() + "==================================");
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            mqttLogger.info("==================================DefaultMqttCallback#deliveryComplete==================================");
        }
    }

}
