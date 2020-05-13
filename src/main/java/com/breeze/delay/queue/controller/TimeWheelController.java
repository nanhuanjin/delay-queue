package com.breeze.delay.queue.controller;

import com.breeze.delay.queue.entity.Order;
import com.breeze.delay.queue.exception.ApplicationException;
import com.breeze.delay.queue.result.BaseResponse;
import com.breeze.delay.queue.result.ResultCodeEnum;
import com.breeze.delay.queue.service.TimeWheelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * @author breeze
 * @date 2020/5/13
 */
@RestController
@RequestMapping("/admin/wheel")
@Api(tags = "时间轮实现延时队列")
public class TimeWheelController {

    @Autowired
    private TimeWheelService timeWheelService;

    @PostMapping("/save/order")
    @ApiOperation("保存订单信息")
    public BaseResponse saveOrder(
            @ApiParam(name = "myOrder", value = "订单对象", required = true)
            @RequestBody Order order) {

        int count = this.timeWheelService.saveOrder(order);

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

        int count = this.timeWheelService.payOrder(orderName);

        if (count == 1) {
            return BaseResponse.success().message("订单支付成功！");
        } else {
            throw new ApplicationException(ResultCodeEnum.ORDER_PAY_ERROR);
        }
    }

    @GetMapping("/list/order")
    @ApiOperation("查询订单列表")
    public BaseResponse listOrder() {

        Map<String, Object> result = this.timeWheelService.listOrder();

        return BaseResponse.success().message("获取订单列表成功！").data(result);
    }
}
