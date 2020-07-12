package cn.stylefeng.guns.sys.core.context;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.stylefeng.guns.core.context.system.SystemContext;
import cn.stylefeng.guns.core.pojo.login.SysLoginUser;
import cn.stylefeng.guns.sys.modular.auth.service.AuthService;
import cn.stylefeng.guns.sys.modular.role.param.SysRoleParam;
import cn.stylefeng.guns.sys.modular.role.service.SysRoleService;
import cn.stylefeng.guns.sys.modular.user.param.SysUserParam;
import cn.stylefeng.guns.sys.modular.user.service.SysUserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统相关上下文接口实现类
 *
 * @author xuyuxiang
 * @date 2020/5/6 14:59
 */
@Component
public class SystemContextImpl implements SystemContext {

    @Resource
    private AuthService authService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysRoleService sysRoleService;

    /**
     * 根据用户id获取姓名
     *
     * @author xuyuxiang
     * @date 2020/5/6 15:03
     */
    @Override
    public String getNameByUserId(Long userId) {
        return sysUserService.getNameByUserId(userId);
    }

    /**
     * 根据角色id获取名称
     *
     * @author xuyuxiang
     * @date 2020/5/22 15:56
     */
    @Override
    public String getNameByRoleId(Long roleId) {
        return sysRoleService.getNameByRoleId(roleId);
    }

    /**
     * 根据token获取登录用户信息
     *
     * @author xuyuxiang
     * @date 2020/6/1 15:10
     */
    @Override
    public SysLoginUser getLoginUserByToken(String token) {
        return authService.getLoginUserByToken(token);
    }

    /**
     * 根据用户账号模糊搜索系统用户列表
     *
     * @author xuyuxiang
     * @date 2020/6/1 15:13
     */
    @Override
    public List<Dict> listUser(String account) {
        SysUserParam sysUserParam = new SysUserParam();
        if (ObjectUtil.isNotEmpty(account)) {
            sysUserParam.setAccount(account);
        }
        return sysUserService.list(sysUserParam);
    }

    /**
     * 根据角色名模糊搜索系统角色列表
     *
     * @author xuyuxiang
     * @date 2020/6/1 15:13
     */
    @Override
    public List<Dict> listRole(String name) {
        SysRoleParam sysRoleParam = new SysRoleParam();
        if (ObjectUtil.isNotEmpty(name)) {
            sysRoleParam.setName(name);
        }
        return sysRoleService.list(sysRoleParam);
    }
}
