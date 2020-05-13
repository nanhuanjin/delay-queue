package com.breeze.delay.queue;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author breeze
 * @date 2020/5/11
 */
@SpringBootApplication
@MapperScan(basePackages = "com.breeze.delay.queue.mapper")
public class DelayQueueApp {
    public static void main(String[] args) {
        SpringApplication.run(DelayQueueApp.class, args);
    }
}
