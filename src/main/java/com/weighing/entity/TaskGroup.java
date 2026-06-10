package com.weighing.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * 提单组的进度实体
 * 用于记录一个提单组的汇总信息。
 * 一个任务对应一个订单中的一辆车的一次完整过磅流程（毛重+皮重）。
 */
@Entity
@Data
@Table(name="Task_group")
public class TaskGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long p_id;  //id
    private String taskGroupId;//提单组好
    private String taskType;//提单类型
    private String remark;//备注
    private String materialName;//物资名称
    private String sendName;//供应商
    private String receiverName;//客户
    private  String status;//提单状态
    private LocalDateTime createdTime;//创建时间
    private LocalDateTime completedTime;//提单完成时间
    private int  index;//第几次提货
    private BigDecimal totalWeight;//总重量
    private BigDecimal deliveryWeight;//已经发送的重量
    private BigDecimal remainingWeight;//剩余提单重量
    private String specification;
    private int massFlag;
    private String load_site;//提货点
    /**
     * 任务状态（状态机）：
     * PENDING       - 刚创建，尚未有过磅
     * GROSS_DONE    - 第一次过磅完成（收货时为毛重，发货时为皮重）
     * TARE_DONE     - 第二次过磅完成（收货时为皮重，发货时为毛重）
     * COMPLETED     - 两次过磅完成，净重已计算，任务结束
     * CANCELLED     - 任务被取消（例如超时未完成）
     */
}
