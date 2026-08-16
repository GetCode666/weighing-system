package com.weighing.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
@Data
@Entity
@Table(name = "transaction_record")
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "transaction_no", nullable = false,unique = true,length = 32)
    private String transactionNo;// 交易流水号（唯一）
    @Column(name = "task_group_id", nullable = false,length = 20)
    private String taskGroupId;  //提单组号
    @Column(name = "order_no", nullable = false,length = 20)
    private String orderNo;  //提单号
    @Column(name = "material_name",nullable = false,length = 32)
    private String materialName;//物质名称
    @Column(name="license_plate",length = 50)
    private String licensePlate; //车牌号
    @Column(name = "transaction_type",nullable = false,length = 20)// 交易类型：RECHARGE(充值)/PURCHASE(购粮)/REFUND(退款)
    private String transactionType;
    @Column(name = "amount",nullable = false)
    private BigDecimal amount;//交易数量
    @Column(name = "gross_weight",nullable = false)
    private BigDecimal grossWeight;  //毛重
    @Column(name="tare_weight",nullable = false)
    private BigDecimal tareWeight;   //皮重
    @Column(name="net_weight",nullable = false)
    private BigDecimal netWeight;   //净重
   @Column(name = "supplier",length = 32)
    private String supplier;
   @Column(name = "customer",length = 32)
    private String customer;
   @Column(name = "status",nullable = false)
    private String status;
}
