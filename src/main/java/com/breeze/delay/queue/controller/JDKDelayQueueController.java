package com.breeze.delay.queue.controller;

import com.breeze.delay.queue.entity.MyOrder;
import com.breeze.delay.queue.exception.ApplicationException;
import com.breeze.delay.queue.result.BaseResponse;
import com.breeze.delay.queue.result.ResultCodeEnum;
import com.breeze.delay.queue.service.JDKDelayQueueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author breeze
 * @date 2020/5/11
 *
 * 延时队列实现方式一：JDK原生API
 *      1.JDK 中提供了一组实现延迟队列的API，位于Java.util.concurrent包下DelayQueue。
 *
 *      2.DelayQueue是一个BlockingQueue（无界阻塞）队列，它本质就是封装了一个PriorityQueue（优先队列），
 *        PriorityQueue内部使用完全二叉堆（不知道的自行了解哈）来实现队列元素排序，我们在向DelayQueue
 *        队列中添加元素时，会给元素一个Delay（延迟时间）作为排序条件，队列中最小的元素会优先放在队首。
 *        队列中的元素只有到了Delay时间才允许从队列中取出。队列中可以放基本数据类型或自定义实体类，在存
 *        放基本数据类型时，优先队列中元素默认升序排列，自定义实体类就需要我们根据类属性值比较计算了。
 *
 *      3.DelayQueue的put方法是线程安全的，因为put方法内部使用了ReentrantLock锁进行线程同步。
 *        DelayQueue还提供了两种出队的方法 poll() 和 take() ， poll() 为非阻塞获取，没有到期的
 *        元素直接返回null；take() 阻塞方式获取，没有到期的元素线程将会等待。
 */
@RestController
@RequestMapping("/admin/jdk")
@Api(tags = "JDK的DelayQueue实现延迟队列")
public class JDKDelayQueueController {

    @Autowired
    private JDKDelayQueueService delayQueueService;

    @PostMapping("/save/order")
    @ApiOperation("保存订单信息")
    public BaseResponse saveOrder(
            @ApiParam(name = "myOrder", value = "订单对象", required = true)
            @RequestBody MyOrder myOrder) {

        int count = this.delayQueueService.saveOrder(myOrder);

        if (count == 1) {
            return BaseResponse.success().message("保存订单信息成功！");
        } else {
            throw new ApplicationException(ResultCodeEnum.SAVE_ORDER_ERROR);
        }
    }

    @PutMapping("/pay/order")
    @ApiOperation("订单支付")
    public BaseResponse payOrder(
            @ApiParam(name = "orderName", value = "订单名称", required = true)
            @RequestParam("orderName") String orderName) {

        int count = this.delayQueueService.payOrder(orderName);

        if (count == 1) {
            return BaseResponse.success().message("订单支付成功！");
        } else {
            throw new ApplicationException(ResultCodeEnum.ORDER_PAY_ERROR);
        }
    }

    @GetMapping("/list/order")
    @ApiOperation("查询订单列表")
    public BaseResponse listOrder() {

        Map<String, Object> result = this.delayQueueService.listOrder();

        return BaseResponse.success().message("获取订单列表成功！").data(result);
    }

}
