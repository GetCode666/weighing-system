package com.weighing.Service;
import com.weighing.entity.Order;
import com.weighing.entity.TaskGroup;
import com.weighing.entity.TaskRecord;
import com.weighing.entity.WeightRecord;
import com.weighing.repository.OrderRespository;
import com.weighing.repository.TaskRecordReposition;
import com.weighing.repository.WeightRecoedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class WeighingService  {
    @Autowired  //自动装配。
    //这段代码是 Java Spring 框架中典型的‌依赖注入（Dependency Injection）‌写法。
    //
    //简单来说，它的意思是：‌告诉 Spring 容器，“我需要这三个数据库操作工具（Repository），
    // 请自动帮我创建好并赋值给我，我可以直接使用它们来操作数据库。”
    private TaskRecordReposition taskRecordReposition;
    private TaskGroup taskGroup;
    @Autowired
    private WeightRecoedRepository weightRecoedRepository;
    @Autowired
    private OrderRespository orderRespository;
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
        Order order = orderRespository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在: " + orderNo));

        // 2. 查询该订单下该车牌是否存在未完成的任务
        TaskRecord taskRecord = taskRecordReposition.findByOrderNoAndLicensePlate(orderNo, licensePlate)
                .filter(t -> !"COMPLETED".equals(t.getStatus()) && !"CANCELED".equals(t.getStatus()))
                .orElse(null);
        // 2. 如果没有未完成任务，则创建新任务并记录第一次过磅
        if (taskRecord == null) {
            //如果tastrecord里没有记录的话，order里有记录，表明还未进过称重，需要进行创建taskrecord记录。
            return createNewTaskAndFirstWeigh(orderNo, licensePlate, weight, operatorName);
        }
        // 3. 有未完成任务，根据任务类型和当前状态决定第二次过磅
        return secondWeight(orderNo,licensePlate, weight, operatorName);
    }
    private TaskRecord createNewTaskAndFirstWeigh(String orderNo, String licensePlate, BigDecimal weight, String operatorName) {
        Order order = orderRespository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在: " + orderNo));
        TaskRecord task=new TaskRecord();
        task.setOrderNo(orderNo);
        task.setLicensePlate(licensePlate);
        task.setTaskType(order.getTaskType());
        task.setStatus("PENDING");
        task.setCreatedTime(LocalDateTime.now());

        if("RECEIVED".equals(task.getTaskType())){
            task.setGrossWeight(weight);
            task.setGrossTime(LocalDateTime.now());
            task.setStatus("GROSS_DONE");
        }else { // SHIP
            task.setTareWeight(weight);
            task.setTareTime(LocalDateTime.now());
            task.setStatus("TARE_DONE");
        }
        taskRecordReposition.save(task);

        // 保存原始过磅记录
        saveWeightRecord(orderNo, licensePlate, weight, operatorName,
                "RECEIVE".equals(order.getTaskType()) ? "GROSS" : "TARE");

        return task;
    }
    public TaskRecord secondWeight(String orderNo, String licensePlate, BigDecimal weight, String operatorName) {
        TaskRecord task=taskRecordReposition.findByOrderNoAndLicensePlate(orderNo,licensePlate)
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
        task.setStatus("COMPLETED");
        task.setCompletedTime(LocalDateTime.now());
        taskRecordReposition.save(task);

        // 保存第二次过磅记录
        saveWeightRecord(task.getOrderNo(), task.getLicensePlate(), weight, operatorName,
                "RECEIVE".equals(task.getTaskType()) ? "TARE" : "GROSS");

        return task;
    }

    private void saveWeightRecord(String orderNo, String licensePlate, BigDecimal weight, String operatorName, String status) {

    }
    }
//        // 如果任务已存在且状态为 COMPLETED，不允许再次过磅
//        if (taskRecord.getOrderNo() != null && "COMPLETED".equals(taskRecord.getStatus())) {
//            throw new RuntimeException("该车辆在此订单中已完成过磅，不可重复");
//        }

//        // 3. 初始化任务（第一次调用时 task 无 ID）
//        if (taskRecord.getTaskGroupId() == null) { // 注意：原代码中字段是 taskGroupId，但后面用了 getOrderNo，请确保字段一致性
//            taskRecord.setOrderNo(String.valueOf(orderNo));
//            taskRecord.setLicensePlate(licensePlate);
//            taskRecord.setStatus("PENDING");
//            taskRecord.setCreatedTime(LocalDateTime.now());
//            // 建议生成一个唯一的 taskGroupId，例如 UUID
//            taskRecord.setTaskGroupId(UUID.randomUUID().toString());
//        }
//
//        // 4. 根据任务类型记录重量并更新状态
//        // 注意：原代码中 entity 定义的是 firstWeight/secondWeight，但 service 逻辑用的是 getGrossWeight/getTareWeight
//        // 请确保 Entity 中的字段名与 Service 中调用的 Getter/Setter 一致。
//        // 这里假设你希望统一逻辑，或者 Entity 中确实有 grossWeight/tareWeight 字段。
//        // 如果 Entity 只有 firstWeight/secondWeight，请调整下面的逻辑。
//
//        if ("RECEIVE".equals(taskRecord.getTaskType())) {
//            // 收货逻辑
//            if (taskRecord.getGrossWeight() != null) { // 假设 firstWeight 对应毛重
//                throw new RuntimeException("该任务已记录毛重，不能重复");
//            }
//            taskRecord.setGrossWeight(weight);
//            taskRecord.setGrossTime(LocalDateTime.now());
//            taskRecord.setStatus("GROSS_DONE");
//            taskRecord.setFirstScaleNo(ScaleNo);
//            // taskRecord.setFirstOperator(operatorName); // Entity 中似乎没有 firstOperator 字段，请检查
//        } else if ("SHIP".equals(order.getTaskType())) { // 注意：这里比较的是 order 的类型还是 taskRecord 的类型？通常应该一致
//            // 发货逻辑
//            if (taskRecord.getTareWeight() != null) { // 假设 secondWeight 对应皮重
//                throw new RuntimeException("该任务已记录皮重，不能重复");
//            }
//            taskRecord.setTareWeight(weight);
//            taskRecord.setTareTime(LocalDateTime.now());
//            taskRecord.setStatus("TARE_DONE");
//        } else {
//            throw new RuntimeException("无效的任务类型: " + order.getTaskType());
//        }
//
//        // 【关键修改】通过注入的实例对象调用 save，而不是通过类名   // 5. 保存任务进度
//        taskRecordReposition.save(taskRecord);
//
//        //5.无论哪种任务，都保存原始过磅记录（流水）
//        WeightRecord record = new WeightRecord();
//        record.setOrderNo(String.valueOf(orderNo));
//        record.setLicensePlate(licensePlate);
//        record.setOrderType("RECEIVE".equals(order.getTaskType()) ? "GROSS" : "TARE");
//        record.setFirstWeight(weight);
//        record.setFirstWeightDate(LocalDateTime.now());
//        record.setFirstOperatorName(operatorName);
//        // 5. 保存任务进度
//        weightRecoedRepository.save(record);
//
//        return taskRecord;
//    }
//    /**
//     * 第二次过磅
//     * 完成毛重和皮重的配对，计算净重，并标记任务为 COMPLETED
//     *
//     * @param licensePlate 车牌号
//     * @param weight       过磅重量（吨）
//     * @param operatorName   操作员ID
//     * @return 完成后的任务进度对象（含净重）
//     */
//    @Transactional
//    public TaskRecord secondWeight(String licensePlate, BigDecimal weight, String operatorName,double extraWeight){
//        // 1. 查找该车牌下状态为 GROSS_DONE 或 TARE_DONE 且未完成的任务
//        // 简化：优先查找状态为 GROSS_DONE 的任务（表示第一次已完成），再找 TARE_DONE
//        TaskRecord task=taskRecordReposition.findByState("GROSS_DONE").stream() // 1. 获取列表并转为流
//                .filter(t->t.getLicensePlate().equals(licensePlate))// 2. 过滤出指定车牌
//                .findFirst()// 3. 获取第一个匹配项，返回 Optional<TaskProgress>
//                .orElseGet(()-> taskRecordReposition.findByState("TARE_DONE").stream()
//                        .filter(t -> t.getLicensePlate().equals(licensePlate))
//                        .findFirst()
//                        .orElseThrow(() -> new RuntimeException("未找到进行中的过磅任务，请先完成第一次过磅")));
//
//        // 2. 获取关联订单信息
//        Order order = orderRespository.findByOrderNo(task.getOrderNo()).orElseThrow(() -> new RuntimeException("订单不存在"));
//        // 3. 根据任务类型记录第二次重量并计算净重
//        if ("RECEIVE".equals(order.getTaskType())) {
//            // 收货：第二次应为皮重
//            if (task.getTareWeight() != null) {
//                throw new RuntimeException("该任务已记录皮重，不可重复");
//            }
//            task.setTareWeight(weight);
//            task.setTareTime(LocalDateTime.now());
//            // 净重 = 毛重 - 皮重
//            task.setNetWeight(task.getGrossWeight().subtract(weight));
//        } else if ("SHIP".equals(order.getTaskType())) {
//            // 发货：第二次应为毛重
//            if (task.getGrossWeight() != null) {
//                throw new RuntimeException("该任务已记录毛重，不可重复");
//            }
//            task.setGrossWeight(weight);
//            task.setGrossTime(LocalDateTime.now());
//            // 净重 = 毛重 - 皮重
//            task.setNetWeight(weight.subtract(task.getTareWeight()));
//        } else {
//            throw new RuntimeException("无效的任务类型");
//        }
//        // 4. 更新任务状态为已完成
//        task.setStatus("COMPLETED");
//        task.setCompletedTime(LocalDateTime.now());
//        taskRecordReposition.save(task);
//
//        // 5. 保存原始过磅记录
//        WeightRecord record = new WeightRecord();
//        record.setOrderNo(task.getOrderNo());
//        record.setLicensePlate(licensePlate);
//        String weighType = ("RECEIVE".equals(order.getTaskType())) ? "TARE" : "GROSS";
//        record.setSecondWeight(weight);
//        record.setSecondWeightDate(LocalDateTime.now());
//        record.setSecondOperatorName(operatorName);
//        record.setExtraWeight(extraWeight);
//        weightRecoedRepository.save(record);
//
//        return task;
//    }




