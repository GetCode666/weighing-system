package com.weighing.repository;

import com.weighing.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问接口
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * 查询状态为指定值的订单列表
     * @param status 订单状态（ACTIVE, CLOSED, CANCELLED）
     * @return 订单列表
     */
    List<Order> findByStatus(String status);

    Optional<Order> findByOrderNo(String orderNo);
}
