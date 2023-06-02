package com.kking.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kking.reggie.entity.Orders;


public interface OrderService extends IService<Orders> {

    /**
     * 用户提交订单
     * @param orders
     */
    public void submit(Orders orders);
}
