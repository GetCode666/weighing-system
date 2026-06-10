package com.weighing.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class WeightRequest {
    private String orderNo;           // 订单ID（必填）
    private String licensePlate;    // 车牌号（必填）
    private BigDecimal weight;      // 重量（吨）
    private String ScaleNo;//磅号
    private String materialName;//物资名称
    private double extraWeight;//扣咋
}
