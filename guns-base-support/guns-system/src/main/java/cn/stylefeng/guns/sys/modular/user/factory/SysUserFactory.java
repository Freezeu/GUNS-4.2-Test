package cn.stylefeng.guns.sys.modular.user.factory;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.stylefeng.guns.core.context.constant.ConstantContextHolder;
import cn.stylefeng.guns.sys.core.enums.AdminTypeEnum;
import cn.stylefeng.guns.core.enums.CommonStatusEnum;
import cn.stylefeng.guns.sys.core.enums.SexEnum;
import cn.stylefeng.guns.sys.modular.user.entity.SysUser;

/**
 * 填充用户附加信息工厂
 *
 * @author xuyuxiang
 * @date 2020/3/23 16:40
 */
public class SysUserFactory {

    /**
     * 管理员类型（1超级管理员 2非管理员）
     * 新增普通用户时填充相关信息
     *
     * @author xuyuxiang
     * @date 2020/3/23 16:41
     */
    public static void fillAddCommonUserInfo(SysUser sysUser) {
        fillBaseUserInfo(sysUser);
        sysUser.setAdminType(AdminTypeEnum.NONE.getCode());
    }

    /**
     * 填充用户基本字段
     *
     * @author xuyuxiang
     * @date 2020/3/23 16:50
     */
    public static void fillBaseUserInfo(SysUser sysUser) {
        //盐值为空则设置盐值
        if(ObjectUtil.isEmpty(sysUser.getSalt())) {
            sysUser.setSalt(RandomUtil.randomString(5));
        }
        //密码为空则设置密码
        if(ObjectUtil.isEmpty(sysUser.getPassword())) {
            //没有密码则设置默认密码
            String password = ConstantContextHolder.getDefaultPassWord();
            //设置密码为Md5加密后的密码
            sysUser.setPassword(SecureUtil.md5(password + sysUser.getSalt()));
        }

        if(ObjectUtil.isEmpty(sysUser.getAvatar())) {
            sysUser.setAvatar(null);
        }

        if(ObjectUtil.isEmpty(sysUser.getSex())) {
            sysUser.setSex(SexEnum.NONE.getCode());
        }

        sysUser.setStatus(CommonStatusEnum.ENABLE.getCode());
    }
}
