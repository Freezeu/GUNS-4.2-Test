package cn.stylefeng.guns.core.context.system;

import cn.hutool.core.lang.Dict;
import cn.stylefeng.guns.core.pojo.login.SysLoginUser;

import java.util.List;

/**
 * 系统相关上下文接口，在system模块实现，用于某些模块不能引用system模块时，通过此方式调用system相关功能
 *
 * @author xuyuxiang
 * @date 2020/5/6 14:52
 */
public interface SystemContext {

    /**
     * 根据用户id获取姓名
     *
     * @param userId 用户id
     * @return 用户姓名
     * @author xuyuxiang
     * @date 2020/5/6 14:57
     */
    String getNameByUserId(Long userId);

    /**
     * 根据角色id获取角色名称
     *
     * @param roleId 角色id
     * @return 角色名称
     * @author xuyuxiang
     * @date 2020/5/22 15:55
     */
    String getNameByRoleId(Long roleId);

    /**
     * 根据token获取登录用户信息
     *
     * @param token token
     * @return 登录用户信息
     * @author xuyuxiang
     * @date 2020/3/13 11:59
     */
    SysLoginUser getLoginUserByToken(String token);

    /**
     * 根据用户账号模糊搜索系统用户列表
     *
     * @param account 账号
     * @return 增强版hashMap，格式：[{"id:":123, "firstName":"张三"}]
     * @author xuyuxiang
     * @date 2020/6/1 15:12
     */
    List<Dict> listUser(String account);

    /**
     * 根据角色名模糊搜索系统角色列表
     *
     * @param name 角色名
     * @return 增强版hashMap，格式：[{"id:":123, "name":"总经理"}]
     * @author xuyuxiang
     * @date 2020/6/1 15:13
     */
    List<Dict> listRole(String name);
}
