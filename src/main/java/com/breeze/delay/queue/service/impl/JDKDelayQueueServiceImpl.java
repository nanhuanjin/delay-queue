package com.breeze.delay.queue.service.impl;

import com.breeze.delay.queue.consts.AppConst;
import com.breeze.delay.queue.entity.MyOrder;
import com.breeze.delay.queue.service.JDKDelayQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author breeze
 * @date 2020/5/11
 */
@Service
@Slf4j
public class JDKDelayQueueServiceImpl implements JDKDelayQueueService {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private DelayQueue<MyOrder> DELAY_QUEUE;

    //模拟数据库
    private static final Map<String, Object> ORDER_MAP = new HashMap<>();

    public int saveOrder(MyOrder myOrder) {
        //设置延时时间为15s
        myOrder.setOrder(AppConst.ORDER_TIMEOUT, TimeUnit.SECONDS);
        //处理保存的逻辑(模拟数据库)
        String orderName = myOrder.getOrderName();
        ORDER_MAP.put(orderName, myOrder);

        //放入延时队列
        DELAY_QUEUE.put(myOrder);
        threadPoolExecutor.execute(this::orderDelayQueue);
        return 1;
    }

    @Override
    public int payOrder(String orderName) {
        //获取订单 设置支付成功
        MyOrder myOrder = (MyOrder) ORDER_MAP.get(orderName);
        myOrder.setPay(true);

        //保存
        ORDER_MAP.put(myOrder.getOrderName(), myOrder);

        return 1;
    }

    @Override
    public Map<String, Object> listOrder() {

        Map<String, Object> result = new HashMap<>(2);
        List<MyOrder> myOrderList = new ArrayList<>();

        for (Map.Entry<String, Object> entry : ORDER_MAP.entrySet()) {
            MyOrder myOrder = (MyOrder) entry.getValue();
            myOrderList.add(myOrder);
        }

        result.put("orderList", myOrderList);
        result.put("delayQueue", DELAY_QUEUE);

        return result;
    }

    /**
     * 订单延时删除
     */
    private void orderDelayQueue() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("订单延迟队列开始时间:" + simpleDateFormat.format(new Date()));

        //处理延时队列
        while (DELAY_QUEUE.size() != 0) {

            //取队列头部元素是否过期
            MyOrder task = DELAY_QUEUE.poll();
            //poll取出的数据如果时间未过期，返回null
            if (task != null) {
                //获取数据库中订单信息
                String name = task.getOrderName();
                MyOrder myOrder = (MyOrder) ORDER_MAP.get(name);
                boolean isPay = myOrder.isPay();

                //如果isPay是false情况，取消订单
                if (!isPay) {
                    log.info("订单:{}被取消, 取消时间:{}", task.getOrderName(), simpleDateFormat.format(new Date()));
                    ORDER_MAP.remove(name);
                    log.info("剩余未支付订单：{}", DELAY_QUEUE);
                    log.info("所有订单：{}", ORDER_MAP);
                }
            }
        }
    }
}
