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
    private Long id;
    private String orderNo;
    private String orderType;  //ship recevie
    private String materialCode;
    private String materialName;
    private BigDecimal targetNetWeight;
    private BigDecimal firstWeight;
    private BigDecimal secondWeight;
    private LocalDateTime firstWeightDate;
    private LocalDateTime secondWeightDate;
    private BigDecimal netWeight;
    private double packageWeight;
    private double extraWeight;//扣杂
    private String first_scaleID;
    private String second_scaleID;
    private String sender;
    private String receiver;
    private LocalDateTime createDate;
    private Long createBy;
    private String remark;
    private String status;// complete cancle  closed
    private boolean printer;
}
