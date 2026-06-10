package com.weighing.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "Weight_record")
public class WeightRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long weightId;
    private String orderNo;//提单号
    private String orderType;  //ship recevie
    private String materialName;//物资名称
    private BigDecimal targetNetWeight;
    private BigDecimal firstWeight;
    private BigDecimal secondWeight;
    private LocalDateTime firstWeightDate;
    private String firstOperatorName;
    private LocalDateTime secondWeightDate;
    private String SecondOperatorName;
    private BigDecimal netWeight;
    private double packageWeight;
    private double extraWeight;//扣杂
    private String first_scaleID;
    private String second_scaleID;
    private LocalDateTime createDate;
    private String status;// complete cancle  closed
    private String licensePlate;
}
