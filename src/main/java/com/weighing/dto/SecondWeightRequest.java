package com.weighing.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 第二次过磅请求 DTO
 * 不需要 orderId，系统根据车牌自动匹配进行中的任务
 */

@Data
public class SecondWeightRequest {
    private String licensePlate;    // 车牌号
    private BigDecimal weight;      // 重量（吨）
}
