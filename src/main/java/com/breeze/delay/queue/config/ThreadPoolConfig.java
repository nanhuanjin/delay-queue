package com.breeze.delay.queue.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author breeze
 * @date 2020/5/11
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor getThreadPoolExecutor() {
        return new ThreadPoolExecutor(
                5,
                10,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
