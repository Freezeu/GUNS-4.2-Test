/*
Copyright [2020] [https://www.stylefeng.cn]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Guns采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改Guns源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/stylefeng/guns-separation
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/stylefeng/guns-separation
6.若您的项目无法满足以上几点，可申请商业授权，获取Guns商业授权许可，请在官网购买授权，地址为 https://www.stylefeng.cn
 */
package cn.stylefeng.guns.sys.modular.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.stylefeng.guns.core.consts.SymbolConstant;
import cn.stylefeng.guns.core.context.constant.ConstantContextHolder;
import cn.stylefeng.guns.core.context.login.LoginContextHolder;
import cn.stylefeng.guns.core.enums.CommonStatusEnum;
import cn.stylefeng.guns.core.exception.PermissionException;
import cn.stylefeng.guns.core.exception.ServiceException;
import cn.stylefeng.guns.core.exception.enums.PermissionExceptionEnum;
import cn.stylefeng.guns.core.exception.enums.StatusExceptionEnum;
import cn.stylefeng.guns.core.factory.PageFactory;
import cn.stylefeng.guns.core.pojo.page.PageResult;
import cn.stylefeng.guns.core.util.PoiUtil;
import cn.stylefeng.guns.sys.core.enums.AdminTypeEnum;
import cn.stylefeng.guns.sys.modular.emp.param.SysEmpParam;
import cn.stylefeng.guns.sys.modular.emp.result.SysEmpInfo;
import cn.stylefeng.guns.sys.modular.emp.service.SysEmpService;
import cn.stylefeng.guns.sys.modular.file.service.SysFileInfoService;
import cn.stylefeng.guns.sys.modular.user.entity.SysUser;
import cn.stylefeng.guns.sys.modular.user.enums.SysUserExceptionEnum;
import cn.stylefeng.guns.sys.modular.user.factory.SysUserFactory;
import cn.stylefeng.guns.sys.modular.user.mapper.SysUserMapper;
import cn.stylefeng.guns.sys.modular.user.param.SysUserParam;
import cn.stylefeng.guns.sys.modular.user.result.SysUserResult;
import cn.stylefeng.guns.sys.modular.user.service.SysUserDataScopeService;
import cn.stylefeng.guns.sys.modular.user.service.SysUserRoleService;
import cn.stylefeng.guns.sys.modular.user.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 系统用户service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/11 17:49
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private SysEmpService sysEmpService;

    @Resource
    private SysUserRoleService sysUserRoleService;

    @Resource
    private SysUserDataScopeService sysUserDataScopeService;

    @Resource
    private SysFileInfoService sysFileInfoService;

    /**
     * 根据账号获取用户
     *
     * @author xuyuxiang
     * @date 2020/3/11 17:51
     */
    @Override
    public SysUser getUserByCount(String account) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, account);
        return this.getOne(queryWrapper);
    }

    /**
     * 查询系统用户
     *
     * @author xuyuxiang yubaoshan
     * @date 2020/3/23 9:37
     */
    @Override
    public PageResult<SysUserResult> page(SysUserParam sysUserParam) {
        QueryWrapper<SysUserResult> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotNull(sysUserParam)) {
            //根据关键字模糊查询（姓名，账号，手机号）
            if(ObjectUtil.isNotEmpty(sysUserParam.getSearchValue())) {
                queryWrapper.like("sys_user.account", sysUserParam.getSearchValue())
                        .or().like("sys_user.name", sysUserParam.getSearchValue())
                        .or().like("sys_user.phone", sysUserParam.getSearchValue());
            }
            //根据员工所属机构查询
            if(ObjectUtil.isNotEmpty(sysUserParam.getSysEmpParam())) {
                SysEmpParam sysEmpParam = sysUserParam.getSysEmpParam();
                if(ObjectUtil.isNotEmpty(sysEmpParam.getOrgId())) {
                    //查询属于该机构的，或该机构下级所有的人员
                    queryWrapper.eq("sys_emp.org_id", sysEmpParam.getOrgId())
                            .or().like("sys_org.pids", sysEmpParam.getOrgId());
                }
            }
            //根据状态查询（0正常 1停用），删除的不会展示在列表
            if(ObjectUtil.isNotEmpty(sysUserParam.getSearchStatus())) {
                queryWrapper.eq("sys_user.status", sysUserParam.getSearchStatus());
            }
        }
        //查询非删除状态，排除超级管理员
        queryWrapper.ne("sys_user.status", CommonStatusEnum.DELETED.getCode())
                .ne("sys_user.admin_type", AdminTypeEnum.SUPER_ADMIN.getCode());

        //如果是超级管理员则获取所有用户，否则只获取其数据范围的用户
        boolean superAdmin = LoginContextHolder.me().isSuperAdmin();
        if(!superAdmin) {
            List<Long> dataScope = sysUserParam.getDataScope();
            if(ObjectUtil.isEmpty(dataScope)) {
                return new PageResult<>(new Page<>());
            } else {
                Set<Long> dataScopeSet = CollectionUtil.newHashSet(dataScope);
                queryWrapper.in("sys_emp.org_id", dataScopeSet);
            }
        }
        return new PageResult<>(this.baseMapper.page(PageFactory.defaultPage(), queryWrapper));
    }

    /**
     * 根据用户账号模糊搜索系统用户列表
     *
     * @author xuyuxiang
     * @date 2020/4/14 9:22
     */
    @Override
    public List<Dict> list(SysUserParam sysUserParam) {
        List<Dict> dictList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotNull(sysUserParam)) {
            //根据账号，姓名模糊查询
            if(ObjectUtil.isNotEmpty(sysUserParam.getAccount())) {
                queryWrapper.and(i ->i.like(SysUser::getAccount, sysUserParam.getAccount())
                        .or().like(SysUser::getName, sysUserParam.getAccount()));
            }
        }
        //查询正常状态，排除超级管理员
        queryWrapper.eq(SysUser::getStatus, CommonStatusEnum.ENABLE.getCode())
                .ne(SysUser::getAdminType, AdminTypeEnum.SUPER_ADMIN.getCode());
        this.list(queryWrapper).forEach(sysUser -> {
            Dict dict = Dict.create();
            dict.put("id", sysUser.getId().toString());
            dict.put("firstName", sysUser.getName() + SymbolConstant.LEFT_SQUARE_BRACKETS
                    + sysUser.getAccount() + SymbolConstant.RIGHT_SQUARE_BRACKETS);
            dictList.add(dict);
        });
        return dictList;
    }

    /**
     * 增加系统用户
     *
     * @author xuyuxiang
     * @date 2020/3/23 9:37
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(SysUserParam sysUserParam) {
        checkParam(sysUserParam, false);
        boolean superAdmin = LoginContextHolder.me().isSuperAdmin();
        //如果登录用户不是超级管理员，则进行数据权限校验
        if (!superAdmin) {
            List<Long> dataScope = sysUserParam.getDataScope();
            //获取添加的用户的所属机构
            Long orgId = sysUserParam.getSysEmpParam().getOrgId();
            //数据范围为空
            if (ObjectUtil.isEmpty(dataScope)) {
                throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
            } else if(!dataScope.contains(orgId)) {
                //所添加的用户的所属机构不在自己的数据范围内
                throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
            }
        }
        SysUser sysUser = new SysUser();
        BeanUtil.copyProperties(sysUserParam, sysUser);
        SysUserFactory.fillAddCommonUserInfo(sysUser);
        this.save(sysUser);
        Long sysUserId = sysUser.getId();
        //增加员工信息
        SysEmpParam sysEmpParam = sysUserParam.getSysEmpParam();
        sysEmpParam.setId(sysUserId);
        sysEmpService.addOrUpdate(sysEmpParam);
    }

    /**
     * 删除系统用户
     *
     * @author xuyuxiang
     * @date 2020/3/23 9:37
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        //不能删除超级管理员
        if(AdminTypeEnum.SUPER_ADMIN.getCode().equals(sysUser.getAdminType())) {
            throw new ServiceException(SysUserExceptionEnum.USER_CAN_NOT_DELETE_ADMIN);
        }
        boolean superAdmin = LoginContextHolder.me().isSuperAdmin();
        //如果登录用户不是超级管理员，则进行数据权限校验
        if (!superAdmin) {
            List<Long> dataScope = sysUserParam.getDataScope();
            //获取要删除的用户的所属机构
            SysEmpInfo sysEmpInfo = sysEmpService.getSysEmpInfo(sysUser.getId());
            Long orgId = sysEmpInfo.getOrgId();
            //数据范围为空
            if (ObjectUtil.isEmpty(dataScope)) {
                throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
            } else if(!dataScope.contains(orgId)) {
                //所要删除的用户的所属机构不在自己的数据范围内
                throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
            }
        }
        sysUser.setStatus(CommonStatusEnum.DELETED.getCode());
        this.updateById(sysUser);
        Long id = sysUser.getId();
        //删除该用户对应的员工表信息
        sysEmpService.deleteEmpInfoByUserId(id);

        //删除该用户对应的用户-角色表关联信息
        sysUserRoleService.deleteUserRoleListByUserId(id);

        //删除该用户对应的用户-数据范围表关联信息
        sysUserDataScopeService.deleteUserDataScopeListByUserId(id);
    }

    /**
     * 编辑系统用户
     *
     * @author xuyuxiang
     * @date 2020/3/23 9:37
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        checkParam(sysUserParam, true);
        boolean superAdmin = LoginContextHolder.me().isSuperAdmin();
        //如果登录用户不是超级管理员，则进行数据权限校验
        if (!superAdmin) {
            List<Long> dataScope = sysUserParam.getDataScope();
            //获取要编辑的用户的所属机构
            Long orgId = sysUserParam.getSysEmpParam().getOrgId();
            //数据范围为空
            if (ObjectUtil.isEmpty(dataScope)) {
                throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
            } else if(!dataScope.contains(orgId)) {
                //所要编辑的用户的所属机构不在自己的数据范围内
                throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
            }
        }

        BeanUtil.copyProperties(sysUserParam, sysUser);
        //不能修改状态，用修改状态接口修改状态
        sysUser.setStatus(null);
        //设置密码
        SysUserFactory.fillBaseUserInfo(sysUser);
        this.updateById(sysUser);
        Long sysUserId = sysUser.getId();
        //编辑员工信息
        SysEmpParam sysEmpParam = sysUserParam.getSysEmpParam();
        BeanUtil.copyProperties(sysUserParam, sysEmpParam);
        sysEmpParam.setId(sysUserId);
        sysEmpService.addOrUpdate(sysEmpParam);
    }

    /**
     * 查看系统用户
     *
     * @author xuyuxiang
     * @date 2020/3/26 9:52
     */
    @Override
    public SysUserResult detail(SysUserParam sysUserParam) {
        SysUserResult sysUserResult = new SysUserResult();
        //获取系统用户
        SysUser sysUser = this.querySysUser(sysUserParam);
        BeanUtil.copyProperties(sysUser, sysUserResult);
        //获取对应员工信息
        SysEmpInfo sysEmpInfo = sysEmpService.getSysEmpInfo(sysUser.getId());
        //设置员工信息
        sysUserResult.setSysEmpInfo(sysEmpInfo);
        return sysUserResult;
    }

    /**
     * 修改状态
     *
     * @author xuyuxiang
     * @date 2020/5/25 14:34
     */
    @Override
    public void changeStatus(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        //不能修改超级管理员状态
        if(AdminTypeEnum.SUPER_ADMIN.getCode().equals(sysUser.getAdminType())) {
            throw new ServiceException(SysUserExceptionEnum.USER_CAN_NOT_UPDATE_ADMIN);
        }

        Long id = sysUser.getId();

        Integer status = sysUserParam.getStatus();
        //校验状态在不在枚举值里
        CommonStatusEnum.validateStatus(status);

        //更新枚举，更新只能更新未删除状态的
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysUser::getId, id)
                .and(i -> i.ne(SysUser::getStatus, CommonStatusEnum.DELETED.getCode()))
                .set(SysUser::getStatus, status);
        boolean update = this.update(updateWrapper);
        if (!update) {
            throw new ServiceException(StatusExceptionEnum.UPDATE_STATUS_ERROR);
        }
    }

    /**
     * 授权角色
     *
     * @author xuyuxiang
     * @date 2020/3/28 16:55
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void grantRole(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        boolean superAdmin = LoginContextHolder.me().isSuperAdmin();
        //如果登录用户不是超级管理员，则进行数据权限校验
        if (!superAdmin) {
            List<Long> dataScope = sysUserParam.getDataScope();
            //获取要授权角色的用户的所属机构
            SysEmpInfo sysEmpInfo = sysEmpService.getSysEmpInfo(sysUser.getId());
            Long orgId = sysEmpInfo.getOrgId();
            //数据范围为空
            if (ObjectUtil.isEmpty(dataScope)) {
                throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
            } else if(!dataScope.contains(orgId)) {
                //所要授权角色的用户的所属机构不在自己的数据范围内
                throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
            }
        }
        sysUserRoleService.grantRole(sysUserParam);
    }

    /**
     * 授权数据
     *
     * @author xuyuxiang
     * @date 2020/3/28 16:55
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void grantData(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        boolean superAdmin = LoginContextHolder.me().isSuperAdmin();
        //如果登录用户不是超级管理员，则进行数据权限校验
        if (!superAdmin) {
            List<Long> dataScope = sysUserParam.getDataScope();
            //获取要授权数据的用户的所属机构
            SysEmpInfo sysEmpInfo = sysEmpService.getSysEmpInfo(sysUser.getId());
            Long orgId = sysEmpInfo.getOrgId();
            //数据范围为空
            if (ObjectUtil.isEmpty(dataScope)) {
                throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
            } else if(!dataScope.contains(orgId)) {
                //所要授权数据的用户的所属机构不在自己的数据范围内
                throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
            }
        }
        sysUserDataScopeService.grantData(sysUserParam);
    }

    /**
     * 更新信息
     *
     * @author xuyuxiang
     * @date 2020/4/1 14:44
     */
    @Override
    public void updateInfo(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        BeanUtil.copyProperties(sysUserParam, sysUser);
        this.updateById(sysUser);
    }

    /**
     * 修改密码
     *
     * @author xuyuxiang
     * @date 2020/4/1 14:44
     */
    @Override
    public void updatePwd(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        String passwordMd5 = SecureUtil.md5(sysUserParam.getPassword() + sysUser.getSalt());
        String currentPwdMd5 = sysUser.getPassword();
        //原密码错误
        if(!passwordMd5.equals(currentPwdMd5)) {
            throw new ServiceException(SysUserExceptionEnum.USER_PWD_ERROR);
        }
        String newPasswordMd5 = SecureUtil.md5(sysUserParam.getNewPassword()+ sysUser.getSalt());
        //新密码与原密码相同
        if(passwordMd5.equals(newPasswordMd5)) {
            throw new ServiceException(SysUserExceptionEnum.USER_PWD_REPEAT);
        }
        sysUser.setPassword(newPasswordMd5);
        this.updateById(sysUser);
    }

    /**
     * 获取用户的数据范围（组织机构id集合）
     *
     * @author xuyuxiang
     * @date 2020/4/5 17:24
     */
    @Override
    public List<Long> getUserDataScopeIdList(Long userId, Long orgId) {
        Set<Long> userDataScopeIdSet = CollectionUtil.newHashSet();
        if(ObjectUtil.isAllNotEmpty(userId, orgId)) {
            //获取该用户对应的数据范围集合
            List<Long> userDataScopeIdListForUser = sysUserDataScopeService.getUserDataScopeIdList(userId);
            //获取该用户的角色对应的数据范围集合
            List<Long> userDataScopeIdListForRole = sysUserRoleService.getUserDataScopeIdList(userId, orgId);
            userDataScopeIdSet.addAll(userDataScopeIdListForUser);
            userDataScopeIdSet.addAll(userDataScopeIdListForRole);
        }
        return CollectionUtil.newArrayList(userDataScopeIdSet);
    }

    /**
     * 根据用户id获取姓名
     *
     * @author xuyuxiang
     * @date 2020/5/6 15:02
     */
    @Override
    public String getNameByUserId(Long userId) {
        SysUser sysUser = this.getById(userId);
        if(ObjectUtil.isNull(sysUser)) {
            throw new ServiceException(SysUserExceptionEnum.USER_NOT_EXIST);
        }
        return sysUser.getName();
    }

    /**
     * 拥有角色
     *
     * @author xuyuxiang
     * @date 2020/5/29 14:10
     */
    @Override
    public List<Long> ownRole(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        return sysUserRoleService.getUserRoleIdList(sysUser.getId());
    }

    /**
     * 拥有数据
     *
     * @author xuyuxiang
     * @date 2020/5/29 14:11
     */
    @Override
    public List<Long> ownData(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        return sysUserDataScopeService.getUserDataScopeIdList(sysUser.getId());
    }

    /**
     * 重置密码
     *
     * @author xuyuxiang
     * @date 2020/5/29 14:57
     */
    @Override
    public void resetPwd(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        String password = ConstantContextHolder.getDefaultPassWord();
        sysUser.setPassword(SecureUtil.md5(password + sysUser.getSalt()));
        this.updateById(sysUser);
    }

    /**
     * 修改头像
     *
     * @author xuyuxiang
     * @date 2020/6/28 15:22
     */
    @Override
    public void updateAvatar(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        Long avatar = sysUserParam.getAvatar();
        sysFileInfoService.assertFile(avatar);
        sysUser.setAvatar(avatar);
        this.updateById(sysUser);
    }

    /**
     * 导出系统用户
     *
     * @author xuyuxiang
     * @date 2020/6/30 16:08
     */
    @Override
    public void export(SysUserParam sysUserParam) {
        List<SysUser> list = this.list();
        PoiUtil.exportExcelWithStream("GunsUsers.xls", SysUser.class, list);
    }

    /**
     * 用户选择器
     *
     * @author xuyuxiang
     * @date 2020/7/3 13:18
     */
    @Override
    public List<SysUser> selector(SysUserParam sysUserParam) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotNull(sysUserParam)) {
            //根据姓名模糊查询
            if(ObjectUtil.isNotEmpty(sysUserParam.getName())) {
                queryWrapper.like("sys_user.name", sysUserParam.getName());
            }
        }
        //如果是超级管理员则获取所有用户，否则只获取其数据范围的用户
        boolean superAdmin = LoginContextHolder.me().isSuperAdmin();
        if(!superAdmin) {
            List<Long> dataScope = sysUserParam.getDataScope();
            if(ObjectUtil.isEmpty(dataScope)) {
                return CollectionUtil.newArrayList();
            } else {
                Set<Long> dataScopeSet = CollectionUtil.newHashSet(dataScope);
                queryWrapper.in("sys_emp.org_id", dataScopeSet);
            }
        }
        //查询非删除状态，排除超级管理员
        queryWrapper.ne("sys_user.status", CommonStatusEnum.DELETED.getCode())
                .ne("sys_user.admin_type", AdminTypeEnum.SUPER_ADMIN.getCode());
        return this.baseMapper.selector(queryWrapper);
    }

    /**
     * 校验参数，检查是否存在相同的账号
     *
     * @author xuyuxiang
     * @date 2020/3/27 16:04
     */
    private void checkParam(SysUserParam sysUserParam, boolean isExcludeSelf) {
        Long id = sysUserParam.getId();
        String account = sysUserParam.getAccount();
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, account)
                .ne(SysUser::getStatus, CommonStatusEnum.DELETED.getCode());
        //是否排除自己，如果是则查询条件排除自己id
        if(isExcludeSelf) {
            queryWrapper.ne(SysUser::getId, id);
        }
        int countByAccount = this.count(queryWrapper);
        //大于等于1个则表示重复
        if (countByAccount >= 1) {
            throw new ServiceException(SysUserExceptionEnum.USER_ACCOUNT_REPEAT);
        }
    }

    /**
     * 获取系统用户
     *
     * @author xuyuxiang
     * @date 2020/3/26 9:54
     */
    private SysUser querySysUser(SysUserParam sysUserParam) {
        SysUser sysUser = this.getById(sysUserParam.getId());
        if(ObjectUtil.isNull(sysUser)) {
            throw new ServiceException(SysUserExceptionEnum.USER_NOT_EXIST);
        }
        return sysUser;
    }
}
