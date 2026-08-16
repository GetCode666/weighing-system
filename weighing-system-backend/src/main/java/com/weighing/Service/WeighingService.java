package com.weighing.Service;
import com.weighing.entity.Order;
import com.weighing.entity.TaskRecord;
import com.weighing.entity.WeightRecord;
import com.weighing.repository.OrderRepository;
import com.weighing.repository.TaskRecordRepository;
import com.weighing.repository.WeightRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WeighingService  {
    @Autowired
    private TaskRecordRepository taskRecordRepository;
    @Autowired
    private WeightRecordRepository weightRecordRepository;
    @Autowired
    private OrderRepository orderRepository;
    /**
     * 第一次过磅
     * 根据订单任务类型，决定本次过磅是毛重（收货）还是皮重（发货）
     *
     * @param orderNo      订单No
     * @param licensePlate 车牌号
     * @param weight       过磅重量（吨）
     * @param operatorName  操作员
     * @return 更新后的任务进度对象
     */
    @Transactional

    public TaskRecord handleWeighing(String orderNo, String licensePlate, BigDecimal weight, String ScaleNo,String operatorName) {
        System.out.println("weightserver_orderNo"+orderNo);
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在: " + orderNo));

        // 2. 查询该订单下该车牌是否存在未完成的任务
        TaskRecord taskRecord = taskRecordRepository.findByOrderNoAndLicensePlate(orderNo, licensePlate)
                .filter(t -> !"COMPLETED".equals(t.getStatus()) && !"CANCELED".equals(t.getStatus()))
                .orElse(null);
        // 2. 如果没有未完成任务，则创建新任务并记录第一次过磅
        if (taskRecord == null) {
            return createNewTaskAndFirstWeigh(orderNo, licensePlate, weight, operatorName, ScaleNo);
        }
        // 3. 有未完成任务，根据任务类型和当前状态决定第二次过磅
        return secondWeight(orderNo, licensePlate, weight, operatorName, ScaleNo);
    }
    private TaskRecord createNewTaskAndFirstWeigh(String orderNo, String licensePlate, BigDecimal weight, String operatorName, String ScaleNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在: " + orderNo));
        TaskRecord task=new TaskRecord();
        task.setOrderNo(orderNo);
        task.setLicensePlate(licensePlate);
        String taskType=order.getTaskType();
        task.setTaskType(taskType);
        System.out.println("taskType"+taskType);
        task.setFirstScaleNo(ScaleNo);

        if("RECEIVE".equals(taskType)){
            task.setGrossWeight(weight);
            task.setGrossTime(LocalDateTime.now());
            task.setStatus("GROSS_DONE");
            saveWeightRecord(order, licensePlate, weight, operatorName,
                    "GROSS", 1, ScaleNo);
        }else { // SHIP
            task.setTareWeight(weight);
            task.setTareTime(LocalDateTime.now());
            task.setStatus("TARE_DONE");
            saveWeightRecord(order, licensePlate, weight, operatorName,
                    "TARE", 1, ScaleNo);
        }
        task.setCreatedTime(LocalDateTime.now());
        taskRecordRepository.save(task);

        return task;
    }
    public TaskRecord secondWeight(String orderNo, String licensePlate, BigDecimal weight, String operatorName, String ScaleNo) {
        TaskRecord task=taskRecordRepository.findByOrderNoAndLicensePlate(orderNo,licensePlate)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        // 校验过磅顺序是否合规
        if ("RECEIVE".equals(task.getTaskType()) && !"GROSS_DONE".equals(task.getStatus())) {
            throw new RuntimeException("收货任务必须先称毛重，再称皮重");
        }
        if ("SHIP".equals(task.getTaskType()) && !"TARE_DONE".equals(task.getStatus())) {
            throw new RuntimeException("发货任务必须先称皮重，再称毛重");
        }
        // 完成第二次过磅
        if ("RECEIVE".equals(task.getTaskType())) {
            task.setTareWeight(weight);
            task.setTareTime(LocalDateTime.now());
            task.setNetWeight(task.getGrossWeight().subtract(weight));
        } else {
            task.setGrossWeight(weight);
            task.setGrossTime(LocalDateTime.now());
            task.setNetWeight(weight.subtract(task.getTareWeight()));
        }
        task.setSecondScaleNo(ScaleNo);
        task.setStatus("COMPLETED");
        task.setCompletedTime(LocalDateTime.now());
        taskRecordRepository.save(task);

        // 保存第二次过磅记录
        Order order = orderRepository.findByOrderNo(task.getOrderNo())
                .orElseThrow(() -> new RuntimeException("订单不存在: " + task.getOrderNo()));
        saveWeightRecord(order, task.getLicensePlate(), weight, operatorName,
                "RECEIVE".equals(task.getTaskType()) ? "TARE" : "GROSS", 2, ScaleNo);

        return task;
    }

    /**
     * 保存过磅原始流水记录
     * 第一次过磅：创建新记录，填入首次称重数据
     * 第二次过磅：更新已有记录，填入二次称重数据并计算净重
     *
     * @param order        关联订单
     * @param licensePlate 车牌号
     * @param weight       过磅重量
     * @param operatorName 操作员
     * @param weighType    称重类型：GROSS(毛重) / TARE(皮重)
     * @param sequence     第几次过磅：1=第一次, 2=第二次
     */
    private void saveWeightRecord(Order order, String licensePlate, BigDecimal weight,
                                   String operatorName, String weighType, int sequence, String ScaleNo) {
        if (sequence == 1) {
            WeightRecord record = new WeightRecord();
            record.setOrderNo(order.getOrderNo());
            record.setOrderType(order.getTaskType());
            record.setMaterialName(order.getMaterialName());
            record.setTargetNetWeight(order.getTargetNetWeight());
            record.setLicensePlate(licensePlate);
            record.setFirstWeight(weight);
            record.setFirstWeightDate(LocalDateTime.now());
            record.setFirstOperatorName(operatorName);
            record.setFirst_scaleID(ScaleNo);
            record.setCreateDate(LocalDateTime.now());
            record.setStatus(weighType + "_DONE");
            weightRecordRepository.save(record);

        } else if (sequence == 2) {
            WeightRecord record = weightRecordRepository
                    .findByOrderNoAndLicensePlate(order.getOrderNo(), licensePlate)
                    .orElseThrow(() -> new RuntimeException(
                            "未找到该订单和车牌的首次过磅流水记录，请先完成第一次过磅"));

            record.setSecondWeight(weight);
            record.setSecondWeightDate(LocalDateTime.now());
            record.setSecondOperatorName(operatorName);
            record.setSecond_scaleID(ScaleNo);
            if ("RECEIVE".equals(order.getTaskType())) {
                record.setNetWeight(record.getFirstWeight().subtract(weight));
            } else {
                record.setNetWeight(weight.subtract(record.getFirstWeight()));
            }
            record.setStatus("COMPLETE");
            weightRecordRepository.save(record);

        } else {
            throw new IllegalArgumentException("无效的过磅序次: " + sequence + "，只能为 1 或 2");
        }
    }
}


