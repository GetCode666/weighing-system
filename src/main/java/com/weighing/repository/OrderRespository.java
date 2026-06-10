package com.weighing.repository;

import com.weighing.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问接口
 */
public interface OrderRespository extends JpaRepository<Order, Long> {
    /**
     * 查询状态为指定值的订单列表
     * 例如：查找所有 ACTIVE 的订单用于过磅选择
     * @param status 订单状态（ACTIVE, CLOSED, CANCELLED）
     * @return 订单列表
     */
    List<Order> findByStatus(String status);

    Optional<Order> findByOrderNo(String orderNo);
}
