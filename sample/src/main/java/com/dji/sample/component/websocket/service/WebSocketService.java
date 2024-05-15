package com.dji.sample.component.websocket.service;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@Component  // 交给IOC容器
@ServerEndpoint("/webSocket")   // 这里的路径跟上面Js创建的WebSocket路径一致
public class WebSocketService {
    // 定义属性
    private Session session;
    //创建一个set用来存储用户
    private static CopyOnWriteArraySet<WebSocketService> websockets = new CopyOnWriteArraySet<>();

    /**
     * 当有用户创建连接时候调用该方法
     */
    @OnOpen
    public void onOpen(Session session) {
        // 给当前的Session赋值
        this.session = session;
        // 将当前对象添加到CopyOnWriteArraySet 中
        websockets.add(this);
        // 可以获取该session，但是其实也是一个内存地址
        System.err.println("【建立连接】 用户为：" + this.session);
        // 获取总数，这个不难理解，实际上这个集合的总数，就是WebSocket连接的总数
        System.err.println("【建立连接】 总数为：" + websockets.size());
    }

    /**
     * 有用户连接断开时候触发该方法
     */
    @OnClose
    public void onClose() {
        websockets.remove(this); // 将当前的对象从集合中删除
        System.err.println("【连接断开】 用户为：" + this.session);
        System.err.println("【连接断开】 总数为：" + websockets.size());
    }

    /**
     * 这个方法是客户端给服务端发送消息触发该方法
     * @param message ： 消息内容
     */
    @OnMessage
    public void onMessage(String message) {
        System.err.println("【收到客户端发的消息】：" + message);
    }

    /**
     * 发送消息的方法，方便后期别的service调用
     *
     * @param message 消息内容
     */
    public void sendMessage(String message) {
        for (WebSocketService websocket : websockets) {   // 遍历该Set集合
            System.err.println("广播消息 【给用户】 ：" + websocket + "发送消息" + "【" + message + "】"); // 获取一个，在控制台打印一句话
            try {
                websocket.session.getBasicRemote().sendText(message); // 发送消息的方法
            } catch (IOException e) {
                e.getMessage();
            }
        }
    }
}

