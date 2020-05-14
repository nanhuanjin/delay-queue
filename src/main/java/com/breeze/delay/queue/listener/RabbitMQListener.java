package com.breeze.delay.queue.listener;

import com.breeze.delay.queue.service.RabbitMQService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author breeze
 * @date 2020/5/14
 */
@Component
@Slf4j
public class RabbitMQListener {

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private RabbitMQService rabbitMQService;

    /**
     * 监听死信队列 - 如果有消息，自动关单并且解锁库存
     * @param orderCode
     */
    @RabbitListener(queues = {"ORDER-DEAD-QUEUE"})
    public void closeOrderListener(String orderCode) {

        //如果逻辑删除订单影响行数为1，解锁库存
        if (this.rabbitMQService.closeOrder(orderCode) == 1) {
            log.info(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            log.info("{} - 订单超时，解锁订单库存", orderCode);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ORDER-SUCCESS-QUEUE"),
            exchange = @Exchange(value = "ORDER-EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"order.pay"}
    ))
    public void payOrderSuccess(String orderCode) {
        log.info("{} - 支付成功，删减库存", orderCode);
    }

}
