package cn.stylefeng.guns.sys.modular.timer.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.extra.spring.SpringUtil;
import cn.stylefeng.guns.core.exception.ServiceException;
import cn.stylefeng.guns.core.timer.TimerTaskRunner;
import cn.stylefeng.guns.sys.modular.timer.enums.exp.SysTimersExceptionEnum;
import cn.stylefeng.guns.sys.modular.timer.service.TimerExeService;
import org.springframework.stereotype.Service;

import static cn.stylefeng.guns.sys.modular.timer.enums.exp.SysTimersExceptionEnum.EXE_EMPTY_PARAM;

/**
 * hutool方式的定时任务执行
 *
 * @author stylefeng
 * @date 2020/7/1 13:48
 */
@Service
public class HutoolTimerExeServiceImpl implements TimerExeService {

    @Override
    public void startTimer(String taskId, String cron, String className) {

        if (ObjectUtil.hasEmpty(taskId, cron, className)) {
            throw new ServiceException(EXE_EMPTY_PARAM);
        }

        // 预加载类看是否存在此定时任务类
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ServiceException(SysTimersExceptionEnum.TIMER_NOT_EXISTED);
        }

        // 定义hutool的任务
        Task task = () -> {
            try {
                TimerTaskRunner timerTaskRunner = (TimerTaskRunner) SpringUtil.getBean(Class.forName(className));
                timerTaskRunner.action();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        };

        // 开始执行任务
        CronUtil.schedule(taskId, cron, task);
    }

    @Override
    public void stopTimer(String taskId) {
        CronUtil.remove(taskId);
    }

}
