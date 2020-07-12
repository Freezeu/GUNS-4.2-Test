package cn.stylefeng.guns.sys.modular.role.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.stylefeng.guns.sys.modular.role.entity.SysRoleMenu;
import cn.stylefeng.guns.sys.modular.role.mapper.SysRoleMenuMapper;
import cn.stylefeng.guns.sys.modular.role.param.SysRoleParam;
import cn.stylefeng.guns.sys.modular.role.service.SysRoleMenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统角色菜单service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/13 15:55
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {
    
    /**
     * 获取角色的菜单id集合
     *
     * @author xuyuxiang
     * @date 2020/3/21 10:18
     */
    @Override
    public List<Long> getRoleMenuIdList(List<Long> roleIdList) {
        List<Long> menuIdList = CollectionUtil.newArrayList();

        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.in(SysRoleMenu::getRoleId, roleIdList);

        this.list(queryWrapper).forEach(sysRoleMenu -> menuIdList.add(sysRoleMenu.getMenuId()));

        return menuIdList;
    }

    /**
     * 授权菜单
     *
     * @author xuyuxiang
     * @date 2020/3/28 16:40
     */
    @Override
    public void grantMenu(SysRoleParam sysRoleParam) {
        Long roleId = sysRoleParam.getId();
        //删除所拥有菜单
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        this.remove(queryWrapper);
        //授权菜单
        sysRoleParam.getGrantMenuIdList().forEach(menuId -> {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(roleId);
            sysRoleMenu.setMenuId(menuId);
            this.save(sysRoleMenu);
        });
    }

    /**
     * 根据菜单id集合删除对应的角色-菜单表信息
     *
     * @author xuyuxiang
     * @date 2020/6/28 14:20
     */
    @Override
    public void deleteRoleMenuListByMenuIdList(List<Long> menuIdList) {
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysRoleMenu::getMenuId, menuIdList);
        this.remove(queryWrapper);
    }

    /**
     * 根据角色id删除对应的角色-菜单表关联信息
     *
     * @author xuyuxiang
     * @date 2020/6/28 14:43
     */
    @Override
    public void deleteRoleMenuListByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        this.remove(queryWrapper);
    }
}
