package cn.stylefeng.guns.sys.modular.log.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.stylefeng.guns.core.factory.PageFactory;
import cn.stylefeng.guns.core.pojo.page.PageResult;
import cn.stylefeng.guns.sys.modular.log.entity.SysVisLog;
import cn.stylefeng.guns.sys.modular.log.mapper.SysVisLogMapper;
import cn.stylefeng.guns.sys.modular.log.param.SysVisLogParam;
import cn.stylefeng.guns.sys.modular.log.service.SysVisLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 系统访问日志service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/12 14:23
 */
@Service
public class SysVisLogServiceImpl extends ServiceImpl<SysVisLogMapper, SysVisLog> implements SysVisLogService {

    /**
     * 查询系统访问日志
     *
     * @author xuyuxiang
     * @date 2020/3/24 20:55
     */
    @Override
    public PageResult<SysVisLog> page(SysVisLogParam sysVisLogParam) {
        LambdaQueryWrapper<SysVisLog> queryWrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotNull(sysVisLogParam)) {
            //根据名称模糊查询
            if(ObjectUtil.isNotEmpty(sysVisLogParam.getName())) {
                queryWrapper.like(SysVisLog::getName, sysVisLogParam.getName());
            }
            //跟据访问类型（字典 1登入 2登出）查询
            if(ObjectUtil.isNotEmpty(sysVisLogParam.getVisType())) {
                queryWrapper.eq(SysVisLog::getVisType, sysVisLogParam.getVisType());
            }
            //根据是否成功查询
            if(ObjectUtil.isNotEmpty(sysVisLogParam.getSuccess())) {
                queryWrapper.eq(SysVisLog::getSuccess, sysVisLogParam.getSuccess());
            }
        }
        return new PageResult<>(this.page(PageFactory.defaultPage(), queryWrapper));
    }

    /**
     * 清空系统访问日志
     *
     * @author xuyuxiang
     * @date 2020/6/1 11:06
     */
    @Override
    public void delete() {
        this.remove(new QueryWrapper<>());
    }
}
