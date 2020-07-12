package cn.stylefeng.guns.sys.modular.timer.entity;

import cn.stylefeng.guns.core.pojo.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务
 *
 * @author stylefeng
 * @date 2020/6/30 18:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_timers")
public class SysTimers extends BaseEntity {


    /**
     * 定时器id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 任务名称
     */
    @TableField("timer_name")
    private String timerName;

    /**
     * 执行任务的class的类名（实现了TimerTaskRunner接口的类的全称）
     */
    @TableField("action_class")
    private String actionClass;

    /**
     * 定时任务表达式
     */
    @TableField("cron")
    private String cron;

    /**
     * 状态（字典 1运行  2停止）
     */
    @TableField("job_status")
    private Integer jobStatus;

    /**
     * 备注信息
     */
    @TableField("remark")
    private String remark;

}
