package com.weighing.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * 过磅任务进度实体
 * 用于记录一次完整的“过磅任务”的汇总信息。
 * 一个任务对应一个订单中的一辆车的一次完整过磅流程（毛重+皮重）。
 */
@Entity
@Table(name="Task_record")
@Data
public class TaskRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private long id;  //id
    private String taskGroupId;//提单组好  任务唯一ID
    private String taskType;//提单类型
    private String orderNo;//提单号  关联的订单ID（订单主键）
    private String remark;//备注
    private String materialName;//物资名称
    private String sendName;//供应商
    private String receiverName;//客户
    private  String status;//提单状态  close complete  cancle
    private LocalDateTime createdTime;//创建时间
    private LocalDateTime completedTime;//提单完成时间
    private BigDecimal grossWeight;//毛量
    private BigDecimal tareWeight;//皮重值
    private LocalDateTime grossTime;
    private LocalDateTime tareTime;
    private BigDecimal netWeight;//净重量
    private String specification;
    private String load_site;//提货点
    private String line;//提货线
    private String firstScaleNo;//磅号
    private String secondScaleNo;
    private String licensePlate;    // 车牌号
    private String qualityStatus;        // 质检结果  PASS（合格） 或 FAIL（不合格）
    private  String firstOperator;
    private  String secondOperator;


}
