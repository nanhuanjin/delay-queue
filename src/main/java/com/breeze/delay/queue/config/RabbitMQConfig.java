package com.breeze.delay.queue.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author breeze
 * @date 2020/5/14
 *
 * RabbitMQ配置类 - 延时队列 + 死信队列
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 交换机
     * @return
     */
    @Bean
    public Exchange exchange() {
        return new TopicExchange("ORDER-EXCHANGE", true, false);
    }

    /**
     * TTL延时队列
     * @return
     */
    @Bean
    public Queue ttlQueue() {
        Map<String, Object> arguments = new HashMap<>(3);
        arguments.put("x-dead-letter-exchange", "ORDER-EXCHANGE");
        arguments.put("x-dead-letter-routing-key", "order.dead");
        arguments.put("x-message-ttl", 15000);
        return new Queue("ORDER-TTL-QUEUE", true, false, false, arguments);
    }

    /**
     * 延时队列绑定到交换机
     * @return
     */
    @Bean
    public Binding ttlBinding() {
        return new Binding("ORDER-TTL-QUEUE", Binding.DestinationType.QUEUE,
                "ORDER-EXCHANGE", "order.close", null);
    }

    /**
     * 死信队列
     * @return
     */
    @Bean
    public Queue deadQueue() {
        return new Queue("ORDER-DEAD-QUEUE", true, false, false, null);
    }

    /**
     * 死信队列绑定到交换机
     * @return
     */
    @Bean
    public Binding deadBinding() {
        return new Binding("ORDER-DEAD-QUEUE", Binding.DestinationType.QUEUE,
                "ORDER-EXCHANGE", "order.dead", null);
    }

    /**
     * 支付成功队列绑定到交换机
     * @return
     */
    @Bean
    public Binding successBinding() {
        return new Binding("ORDER-SUCCESS-QUEUE", Binding.DestinationType.QUEUE,
                "ORDER-EXCHANGE", "order.pay", null);
    }
}
