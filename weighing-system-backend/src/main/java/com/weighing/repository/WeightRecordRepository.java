package com.weighing.repository;

import com.weighing.entity.WeightRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 过磅原始记录数据访问接口
 * 每次过磅动作都会生成一条记录
 */
public interface WeightRecordRepository extends JpaRepository<WeightRecord, Long> {

    /**
     * 根据提单号和车牌号查找过磅记录
     * 用于第二次过磅时找到第一次的流水记录进行更新
     */
    Optional<WeightRecord> findByOrderNoAndLicensePlate(String orderNo, String licensePlate);
}
