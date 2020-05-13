package com.breeze.delay.queue.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author breeze
 * @date 2020/5/11
 *
 * 全局统一返回常量结果 - 枚举类
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public enum ResultCodeEnum {

    SUCCESS(20000, "成功"),
    UNKNOWN_REASON(40001, "未知错误"),

    SAVE_ORDER_ERROR(40002, "保存订单信息失败"),
    ORDER_PAY_ERROR(40003, "订单支付失败");

    private Integer code;

    private String message;
}
