package com.weighing.Controller;

import com.weighing.dto.WeightRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import com.weighing.Service.WeighingService;
import com.weighing.entity.TaskGroup;
import com.weighing.entity.TaskRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 过磅控制器
 * 提供第一次过磅和第二次过磅的接口
 */
@RestController
@RequestMapping("/api/weighing")

public class WeightController {
    @Autowired
    private WeighingService weighingService;
    @Autowired
    private TaskRecord taskRecord;
    /**
     * 获取当前登录用户的ID（简化版，MVP阶段先返回固定值）
     * 完整实现应从 SecurityContext 中获取用户名，再查询数据库得到 userId
     * @return 用户ID（1L 为 admin）
     */
    private String getCurrentUserName() {
        // 1. 获取安全上下文
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 2. 安全检查：防止未登录或匿名访问导致 NullPointerException
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }
        String username =authentication.getName();
        // 实际应调用 UserService 根据用户名获取 userId，此处简化
        // 3. 排除匿名账户
        if ("anonymousUser".equals(username)) {
            throw new RuntimeException("匿名访问不允许操作过磅业务");
        }
        // 4. 【重要】实际业务中，username 通常是字符串（如 "admin"），而数据库用户ID是 Long。
        // 此处你返回了固定值 1L，这在 MVP 阶段可以，但生产环境必须通过 username 查询数据库获取真实 ID。
        // 示例伪代码：
        // User user = userService.findByUsername(username);
        // return user.getId();
        System.out.println("当前操作用户: " + username);
        return "1L";
    }
    /**
     * 第一次过磅
     * @param req 包含订单ID、车牌号、重量
     * @return 更新后的任务进度
     */
    @PostMapping
    public TaskRecord weight(@RequestBody WeightRequest req) {
        // 调用服务层，传入获取到的操作员ID
        return weighingService.handleWeighing(
                req.getOrderNo(),
                req.getLicensePlate(),
                req.getWeight(),
                req.getScaleNo(),
                getCurrentUserName()
        );
    }
    /**
     * 第二次过磅
     * @param req 包含车牌号和重量（订单ID可省略，系统自动查找未完成任务）
     * @return 完成后的任务进度（含净重）
     */
//    @PostMapping("/second")
//    public TaskRecord secondWeight(@RequestBody WeightRequest req) {
//        return weighingService.secondWeight(
//                req.getOrderNo(),
//                req.getLicensePlate(),
//                req.getWeight(),
//                getCurrentUserName()
//        );
//    }

}
