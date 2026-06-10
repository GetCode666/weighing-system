package com.weighing.Controller;

import com.weighing.entity.Order;
import com.weighing.repository.OrderRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单控制器
 * 提供订单的创建和查询接口
 */

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private OrderRespository orderRespository;

    /**
     * 获取所有状态为 ACTIVE 的订单（用于过磅选择）
     * @return 订单列表
     */
    @GetMapping("/order/active")
    public List<Order> getActiveOrders() {
        return orderRespository.findByStatus("ACTIVE");
    }
    /**
     * 创建新订单（收货或发货）
     * @param order 订单实体（前端传递 JSON）
     * @return 保存后的订单（含自动生成的ID）
     */
    @PostMapping("/orders")
    public Order createOrder(@RequestBody Order order){
        order.setStatus("ACTIVE");
        order.setCreateDate(LocalDateTime.now());
        // createdBy 可从当前登录用户获取，MVP 阶段暂不实现
        return orderRespository.save(order);
    }
}
