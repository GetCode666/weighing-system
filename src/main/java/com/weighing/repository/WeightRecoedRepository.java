package com.weighing.repository;

import com.weighing.entity.WeightRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import com.weighing.entity.WeightRecord;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * 过磅原始记录数据访问接口
 * 每个过磅动作（毛重或皮重）都会生成一条记录
 */
public interface WeightRecoedRepository extends JpaRepository<WeightRecord,Long>{

        // 基础 CRUD 即可满足 MVP 需求
        // 后续可根据需要增加按订单、车牌、时间范围查询的方法
}
