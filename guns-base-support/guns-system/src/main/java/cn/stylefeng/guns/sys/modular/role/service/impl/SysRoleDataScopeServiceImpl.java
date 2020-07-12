package cn.stylefeng.guns.sys.modular.role.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.stylefeng.guns.sys.modular.role.entity.SysRoleDataScope;
import cn.stylefeng.guns.sys.modular.role.mapper.SysRoleDataScopeMapper;
import cn.stylefeng.guns.sys.modular.role.param.SysRoleParam;
import cn.stylefeng.guns.sys.modular.role.service.SysRoleDataScopeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统角色数据范围service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/13 15:55
 */
@Service
public class SysRoleDataScopeServiceImpl extends ServiceImpl<SysRoleDataScopeMapper, SysRoleDataScope>implements SysRoleDataScopeService {

    /**
     * 授权数据
     *
     * @author xuyuxiang
     * @date 2020/3/28 16:49
     */
    @Override
    public void grantDataScope(SysRoleParam sysRoleParam) {
        Long roleId = sysRoleParam.getId();
        LambdaQueryWrapper<SysRoleDataScope> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleDataScope::getRoleId, roleId);
        //删除所拥有数据
        this.remove(queryWrapper);
        //授权数据
        sysRoleParam.getGrantOrgIdList().forEach(orgId -> {
            SysRoleDataScope sysRoleDataScope = new SysRoleDataScope();
            sysRoleDataScope.setRoleId(roleId);
            sysRoleDataScope.setOrgId(orgId);
            this.save(sysRoleDataScope);
        });
    }

    /**
     * 根据角色id获取角色数据范围集合
     *
     * @author xuyuxiang
     * @date 2020/4/5 18:24
     */
    @Override
    public List<Long> getRoleDataScopeIdList(List<Long> roleIdList) {
        List<Long> resultList = CollectionUtil.newArrayList();
        if(ObjectUtil.isNotEmpty(roleIdList)) {
            LambdaQueryWrapper<SysRoleDataScope> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SysRoleDataScope::getRoleId, roleIdList);
            this.list(queryWrapper).forEach(sysRoleDataScope -> resultList.add(sysRoleDataScope.getOrgId()));
        }
        return resultList;
    }

    /**
     * 根据机构id集合删除对应的角色-数据范围关联信息
     *
     * @author xuyuxiang
     * @date 2020/6/28 14:15
     */
    @Override
    public void deleteRoleDataScopeListByOrgIdList(List<Long> orgIdList) {
        LambdaQueryWrapper<SysRoleDataScope> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysRoleDataScope::getOrgId, orgIdList);
        this.remove(queryWrapper);
    }

    /**
     * 根据角色id删除对应的角色-数据范围关联信息
     *
     * @author xuyuxiang
     * @date 2020/6/28 14:48
     */
    @Override
    public void deleteRoleDataScopeListByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleDataScope> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleDataScope::getRoleId, roleId);
        this.remove(queryWrapper);
    }
}
