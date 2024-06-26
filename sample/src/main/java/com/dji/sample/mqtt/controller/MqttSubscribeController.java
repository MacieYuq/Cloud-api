package com.dji.sample.mqtt.controller;

import com.alibaba.fastjson2.JSONObject;

import com.dji.sample.component.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.Arrays;

@RestController
@Slf4j
@RequestMapping("${url.manage.prefix}${url.manage.version}/mqtt")
public class MqttSubscribeController {

    @Resource
    private MqttInfo mqttInfo;

    @Autowired
    private WebSocketService webSocketService;

    @GetMapping("/sub")
    public void osdSub() throws MqttException {
        MqttClient sampleClient = new MqttClient("tcp://" + mqttInfo.getHost() + ":" + mqttInfo.getPort(), mqttInfo.getClientId(), new MemoryPersistence());// 创建客户端
        MqttConnectOptions connOpts = new MqttConnectOptions(); // 创建链接参数
        // 在重新启动和重新连接时记住状态
        connOpts.setCleanSession(false);
        // 设置连接的用户名
        connOpts.setUserName(mqttInfo.getUsername());
        // 设置连接的密码
        connOpts.setPassword(mqttInfo.getPassword().toCharArray());
        // 超时时间 单位为秒
        connOpts.setConnectionTimeout(10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
        connOpts.setKeepAliveInterval(20);

        // 设置回调函数
        sampleClient.setCallback(new MqttCallback() {
            // 客户端掉线走这条
            public void connectionLost(Throwable cause) {
                log.info("connectionLost");
                cause.printStackTrace();
            }

            // 收到消息推送走这条
            public void messageArrived(String topic, MqttMessage message) throws SQLException, MqttException {
                String content = new String(message.getPayload());
                log.info("topic:"+topic);
                log.info("Qos:"+message.getQos());
                log.info("收到的消息:"+content);
                // 将消息推送到socket
                webSocketService.sendMessage(content);
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
                log.info("deliveryComplete---------"+ token.isComplete());
            }
        });
        sampleClient.connect(connOpts); // 建立连接
        String[] topics = new String[2];
        topics[0] = "thing/product/4TADKCC0010022/osd";
        topics[1] = "thing/product/1581F6Q8D23BU00AC9W2/state";
        log.info(Arrays.toString(topics));
        //订阅消息
        // 第一个参数是订阅的主题，第二个参数是QOS，消息的质量
        sampleClient.subscribe(Arrays.toString(topics), 1);
    }
}
