package com.breeze.delay.queue.config;

import com.breeze.delay.queue.entity.MyOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.DelayQueue;

/**
 * @author breeze
 * @date 2020/5/11
 *
 * 将延时队列注入到spring容器
 */
@Configuration
public class DelayQueueConfig {

    @Bean
    public DelayQueue<MyOrder> getDelayQueue() {
        return new DelayQueue<>();
    }
}
