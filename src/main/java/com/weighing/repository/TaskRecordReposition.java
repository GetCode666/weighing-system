package com.weighing.repository;

import com.weighing.entity.TaskRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
/**
 * 过磅任务进度数据访问接口
 * 用于跟踪一个完整任务（一辆车一次完整的毛+皮流程）的状态
 */
public interface TaskRecordReposition extends JpaRepository<TaskRecord, Long> {
    /**
     * 根据订单ID和车牌号查找任务
     * 用于判断该车在该订单下是否已有进行中的任务
     * @param orderNo 订单ID
     * @param licensePlate 车牌号
     * @return 任务进度对象（可能为空）
     */
    Optional<TaskRecord> findByOrderNoAndLicensePlate(String orderNo, String licensePlate);
    Optional<TaskRecord> findFirstByLicensePlateAndStateNotOrderByTaskIdDesc(String licensePlate, String state);
/**
 * 查询所有状态为指定值的任务列表
 * 例如：查找所有未完成的任务（PENDING, GROSS_DONE, TARE_DONE）
 * @param state 任务状态
 * @return 任务列表
 */
    List<TaskRecord> findByState(String state);

}
