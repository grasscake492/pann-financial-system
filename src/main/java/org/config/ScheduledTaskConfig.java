// 创建文件：ScheduledTaskConfig.java
package org.config;

import org.service.impl.RoyaltyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 定时任务配置
 */
@Configuration
@EnableScheduling
public class ScheduledTaskConfig {

    @Autowired
    private RoyaltyServiceImpl royaltyService;

    /**
     * 每月1号凌晨1点执行清理任务
     * cron表达式：秒 分 时 日 月 星期
     */
    @Scheduled(cron = "0 0 1 1 * ?")
    public void monthlyCleanupTask() {
        System.out.println("开始执行月度清理任务...");
        royaltyService.scheduledCleanup();
        System.out.println("月度清理任务执行完成");
    }
}