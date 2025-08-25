package com.sportygroup.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * RocketMQ Configuration
 * Enables RocketMQ when not in mock mode
 */
@Configuration
@ConditionalOnProperty(name = "app.mock.rocketmq", havingValue = "false")
@Import(RocketMQAutoConfiguration.class)
@Slf4j
public class RocketMQConfig {
    
    public RocketMQConfig() {
        log.info("RocketMQ configuration enabled - using real RocketMQ implementation");
    }
}
