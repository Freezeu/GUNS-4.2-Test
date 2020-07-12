package cn.stylefeng.guns.sys.modular.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.stylefeng.guns.sys.modular.user.entity.SysUserDataScope;
import cn.stylefeng.guns.sys.modular.user.mapper.SysUserDataScopeMapper;
import cn.stylefeng.guns.sys.modular.user.param.SysUserParam;
import cn.stylefeng.guns.sys.modular.user.service.SysUserDataScopeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统用户数据范围service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/13 15:49
 */
@Service
public class SysUserDataScopeServiceImpl extends ServiceImpl<SysUserDataScopeMapper, SysUserDataScope>
        implements SysUserDataScopeService {

    /**
     * 授权数据
     *
     * @author xuyuxiang
     * @date 2020/3/28 16:58
     */
    @Override
    public void grantData(SysUserParam sysUserParam) {
        Long userId = sysUserParam.getId();
        //删除所拥有数据
        LambdaQueryWrapper<SysUserDataScope> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserDataScope::getUserId, userId);
        this.remove(queryWrapper);
        //授权数据
        sysUserParam.getGrantOrgIdList().forEach(orgId -> {
            SysUserDataScope sysUserDataScope = new SysUserDataScope();
            sysUserDataScope.setUserId(userId);
            sysUserDataScope.setOrgId(orgId);
            this.save(sysUserDataScope);
        });
    }

    /**
     * 获取用户的数据范围id集合
     *
     * @author xuyuxiang
     * @date 2020/4/5 17:28
     */
    @Override
    public List<Long> getUserDataScopeIdList(Long uerId) {
        List<Long> userDataScopeIdList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysUserDataScope> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserDataScope::getUserId, uerId);
        this.list(queryWrapper).forEach(sysUserDataScope -> userDataScopeIdList.add(sysUserDataScope.getOrgId()));
        return userDataScopeIdList;
    }

    /**
     * 根据机构id集合删除对应的用户-数据范围关联信息
     *
     * @author xuyuxiang
     * @date 2020/6/28 14:15
     */
    @Override
    public void deleteUserDataScopeListByOrgIdList(List<Long> orgIdList) {
        LambdaQueryWrapper<SysUserDataScope> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysUserDataScope::getOrgId, orgIdList);
        this.remove(queryWrapper);
    }

    /**
     * 根据用户id删除对应的用户-数据范围关联信息
     *
     * @author xuyuxiang
     * @date 2020/6/28 14:54
     */
    @Override
    public void deleteUserDataScopeListByUserId(Long userId) {
        LambdaQueryWrapper<SysUserDataScope> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserDataScope::getUserId, userId);
        this.remove(queryWrapper);
    }
}
