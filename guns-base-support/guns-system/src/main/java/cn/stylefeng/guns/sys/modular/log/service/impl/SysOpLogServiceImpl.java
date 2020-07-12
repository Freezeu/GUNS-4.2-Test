package cn.stylefeng.guns.sys.modular.log.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.stylefeng.guns.core.factory.PageFactory;
import cn.stylefeng.guns.core.pojo.page.PageResult;
import cn.stylefeng.guns.sys.modular.log.entity.SysOpLog;
import cn.stylefeng.guns.sys.modular.log.mapper.SysOpLogMapper;
import cn.stylefeng.guns.sys.modular.log.param.SysOpLogParam;
import cn.stylefeng.guns.sys.modular.log.service.SysOpLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 系统操作日志service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/12 14:22
 */
@Service
public class SysOpLogServiceImpl extends ServiceImpl<SysOpLogMapper, SysOpLog> implements SysOpLogService {

    /**
     * 查询系统操作日志
     *
     * @author xuyuxiang
     * @date 2020/3/30 10:32
     */
    @Override
    public PageResult<SysOpLog> page(SysOpLogParam sysOpLogParam) {
        LambdaQueryWrapper<SysOpLog> queryWrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotNull(sysOpLogParam)) {
            //根据名称模糊查询
            if(ObjectUtil.isNotEmpty(sysOpLogParam.getName())) {
                queryWrapper.like(SysOpLog::getName, sysOpLogParam.getName());
            }
            //根据操作类型查询
            if(ObjectUtil.isNotEmpty(sysOpLogParam.getOpType())) {
                queryWrapper.eq(SysOpLog::getOpType, sysOpLogParam.getOpType());
            }
            //根据是否成功查询
            if(ObjectUtil.isNotEmpty(sysOpLogParam.getSuccess())) {
                queryWrapper.eq(SysOpLog::getSuccess, sysOpLogParam.getSuccess());
            }
        }
        Page<SysOpLog> page = this.page(PageFactory.defaultPage(), queryWrapper);
        return new PageResult<>(page);
    }

    /**
     * 清空系统操作日志
     *
     * @author xuyuxiang
     * @date 2020/6/1 11:06
     */
    @Override
    public void delete() {
        this.remove(new QueryWrapper<>());
    }
}
