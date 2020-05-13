package com.breeze.delay.queue.controller;

import com.breeze.delay.queue.entity.Order;
import com.breeze.delay.queue.result.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * @author breeze
 * @date 2020/5/13
 */
@RestController
@RequestMapping("/admin/rabbit")
@Api(tags = "RabbitMQ实现延时队列")
public class RabbitMQController {

    @PostMapping("/save/order")
    @ApiOperation("保存订单信息")
    public BaseResponse saveOrder(
            @ApiParam(name = "myOrder", value = "订单对象", required = true)
            @RequestBody Order order) {

        return null;
    }

    @PutMapping("/pay/order")
    @ApiOperation("订单支付")
    public BaseResponse payOrder(
            @ApiParam(name = "orderName", value = "订单名称", required = true)
            @RequestParam("orderName") String orderName) {

        return null;
    }

    @GetMapping("/list/order")
    @ApiOperation("查询订单列表")
    public BaseResponse listOrder() {

        return null;
    }
}
