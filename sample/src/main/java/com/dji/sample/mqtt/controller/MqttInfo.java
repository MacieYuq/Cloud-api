package com.dji.sample.mqtt.controller;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Data
public class MqttInfo {
    @Value("${cloud-sdk.mqtt.inbound-topic}")
    private String topic;

    @Value("${mqtt.BASIC.host}")
    private String host;

    @Value("${mqtt.BASIC.port}")
    private String port;

    @Value("${mqtt.BASIC.username}")
    private String username;

    @Value("${mqtt.BASIC.password}")
    private String password;

    @Value("${mqtt.BASIC.client-id}")
    private String clientId;
}
