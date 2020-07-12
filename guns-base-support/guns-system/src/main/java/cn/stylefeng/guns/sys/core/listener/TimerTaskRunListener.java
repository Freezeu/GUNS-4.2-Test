package cn.stylefeng.guns.sys.core.listener;

import cn.hutool.cron.CronUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.stylefeng.guns.sys.modular.timer.entity.SysTimers;
import cn.stylefeng.guns.sys.modular.timer.enums.TimerJobStatusEnum;
import cn.stylefeng.guns.sys.modular.timer.param.SysTimersParam;
import cn.stylefeng.guns.sys.modular.timer.service.SysTimersService;
import cn.stylefeng.guns.sys.modular.timer.service.TimerExeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

import java.util.List;

/**
 * 项目定时任务启动的listener
 *
 * @author stylefeng
 * @date 2020/7/1 15:30
 */
@Slf4j
public class TimerTaskRunListener implements ApplicationListener<ApplicationStartedEvent>, Ordered {

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {

        SysTimersService sysTimersService = SpringUtil.getBean(SysTimersService.class);
        TimerExeService timerExeService = SpringUtil.getBean(TimerExeService.class);

        // 获取所有开启状态的任务
        SysTimersParam sysTimersParam = new SysTimersParam();
        sysTimersParam.setJobStatus(TimerJobStatusEnum.RUNNING.getCode());
        List<SysTimers> list = sysTimersService.list(sysTimersParam);

        // 添加定时任务到调度器
        for (SysTimers sysTimers : list) {
            timerExeService.startTimer(String.valueOf(sysTimers.getId()), sysTimers.getCron(), sysTimers.getActionClass());
        }

        // 设置秒级别的启用
        CronUtil.setMatchSecond(true);

        // 启动定时器执行器
        CronUtil.start();
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
