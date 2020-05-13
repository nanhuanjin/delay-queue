package com.breeze.delay.queue.listener;

import com.breeze.delay.queue.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * @author breeze
 * @date 2020/5/13
 *
 * Redis key过期的监听器
 */
@Component
@Slf4j
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Autowired
    private OrderMapper orderMapper;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        log.info("监听到key：{} 已过期", expiredKey);

        //逻辑删除数据库中的订单信息
        this.orderMapper.updateOrderToDeletedByOrderName(expiredKey);
    }
}
