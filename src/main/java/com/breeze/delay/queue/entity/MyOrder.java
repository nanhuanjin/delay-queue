package com.breeze.delay.queue.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author breeze
 * @date 2020/5/11
 *
 * 订单类
 */
@Data
@ApiModel("订单类")
@NoArgsConstructor
@ToString
public class MyOrder implements Delayed {

    @ApiModelProperty("延迟时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private long time;

    @ApiModelProperty("订单名")
    private String orderName;

    @ApiModelProperty("是否支付")
    private boolean isPay = false;

    public MyOrder(String orderName, long time, TimeUnit unit) {
        this.orderName = orderName;
        this.time = System.currentTimeMillis() + (time > 0 ? unit.toMillis(time) : 0);
    }

    public void setOrder(long time, TimeUnit unit) {
        this.time = System.currentTimeMillis() + (time > 0 ? unit.toMillis(time) : 0);
    }

    public long getDelay(TimeUnit unit) {
        return time - System.currentTimeMillis();
    }

    public int compareTo(Delayed o) {
        MyOrder MyOrder = (MyOrder) o;
        long diff = this.time - MyOrder.time;
        if (diff <= 0) {
            return -1;
        } else {
            return 1;
        }
    }
}
