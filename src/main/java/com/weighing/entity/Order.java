package com.weighing.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name="Order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderId;
    private String orderNo;
    private String taskType;// recrive ship
    private String materialCode;
    private String materialName;
    private BigDecimal targetWeight;
    private String senderName;
    private String receiverName;
    private String status; //active closed  cancelled
    private LocalDateTime createDate;
    private String createUser;
}

