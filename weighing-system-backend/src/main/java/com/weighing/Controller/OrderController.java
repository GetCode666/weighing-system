package com.weighing.Controller;

import com.weighing.entity.Order;
import com.weighing.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 * 提供订单的创建和查询接口
 */

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    /**
     * 获取所有状态为 ACTIVE 的订单（用于过磅选择）
     * @return 订单列表
     */
    @GetMapping("/order/active")
    public List<Order> getActiveOrders() {
        return orderRepository.findByStatus("ACTIVE");
    }
    /**
     * 创建新订单（收货或发货）
     * @param order 订单实体（前端传递 JSON）
     * @return 保存后的订单（含自动生成的ID）
     */
    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Order order) {
        Map<String, Object> response = new HashMap<>();

        // 校验必填字段
        if (order.getOrderNo() == null || order.getOrderNo().isBlank()) {
            response.put("success", false);
            response.put("message", "提单号不能为空");
            return ResponseEntity.badRequest().body(response);
        }
        if (order.getTaskType() == null || order.getTaskType().isBlank()) {
            response.put("success", false);
            response.put("message", "任务类型不能为空（RECEIVE 或 SHIP）");
            return ResponseEntity.badRequest().body(response);
        }
        if (orderRepository.findByOrderNo(order.getOrderNo()).isPresent()) {
            response.put("success", false);
            response.put("message", "提单号已存在: " + order.getOrderNo());
            return ResponseEntity.badRequest().body(response);
        }

        order.setStatus("ACTIVE");
        order.setCreateDate(LocalDateTime.now());
        Order saved = orderRepository.save(order);
        response.put("success", true);
        response.put("message", "创建成功");
        response.put("data", saved);
        return ResponseEntity.ok(response);
    }
}
