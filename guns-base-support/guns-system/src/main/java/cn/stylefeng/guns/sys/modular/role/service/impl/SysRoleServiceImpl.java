package cn.stylefeng.guns.sys.modular.role.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.stylefeng.guns.core.annotion.DataScope;
import cn.stylefeng.guns.core.consts.CommonConstant;
import cn.stylefeng.guns.core.consts.SymbolConstant;
import cn.stylefeng.guns.core.context.login.LoginContextHolder;
import cn.stylefeng.guns.core.enums.CommonStatusEnum;
import cn.stylefeng.guns.core.exception.PermissionException;
import cn.stylefeng.guns.core.exception.enums.PermissionExceptionEnum;
import cn.stylefeng.guns.sys.core.enums.DataScopeTypeEnum;
import cn.stylefeng.guns.core.exception.ServiceException;
import cn.stylefeng.guns.core.factory.PageFactory;
import cn.stylefeng.guns.core.pojo.page.PageResult;
import cn.stylefeng.guns.sys.modular.emp.result.SysEmpInfo;
import cn.stylefeng.guns.sys.modular.org.service.SysOrgService;
import cn.stylefeng.guns.sys.modular.role.entity.SysRole;
import cn.stylefeng.guns.sys.modular.role.enums.SysRoleExceptionEnum;
import cn.stylefeng.guns.sys.modular.role.mapper.SysRoleMapper;
import cn.stylefeng.guns.sys.modular.role.param.SysRoleParam;
import cn.stylefeng.guns.sys.modular.role.service.SysRoleDataScopeService;
import cn.stylefeng.guns.sys.modular.role.service.SysRoleMenuService;
import cn.stylefeng.guns.sys.modular.role.service.SysRoleService;
import cn.stylefeng.guns.sys.modular.user.service.SysUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;
import java.util.Set;

/**
 * 系统角色service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/13 15:55
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Resource
    private SysUserRoleService sysUserRoleService;

    @Resource
    private SysRoleMenuService sysRoleMenuService;

    @Resource
    private SysRoleDataScopeService sysRoleDataScopeService;

    @Resource
    private SysOrgService sysOrgService;

    /**
     * 获取用户角色相关信息
     *
     * @author xuyuxiang
     * @date 2020/3/13 16:28
     */
    @Override
    public List<Dict> getLoginRoles(Long userId) {
        List<Dict> dictList = CollectionUtil.newArrayList();
        //获取用户角色id集合
        List<Long> roleIdList = sysUserRoleService.getUserRoleIdList(userId);
        if(ObjectUtil.isNotEmpty(roleIdList)) {
            LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SysRole::getId, roleIdList).eq(SysRole::getStatus, CommonStatusEnum.ENABLE.getCode());
            //根据角色id集合查询并返回结果
            this.list(queryWrapper).forEach(sysRole -> {
                Dict dict = Dict.create();
                dict.put(CommonConstant.ID, sysRole.getId());
                dict.put(CommonConstant.CODE, sysRole.getCode());
                dict.put(CommonConstant.NAME, sysRole.getName());
                dictList.add(dict);
            });
        }
        return dictList;
    }

    /**
     * 查询系统角色
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:57
     */
    @Override
    public PageResult<SysRole> page(SysRoleParam sysRoleParam) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotNull(sysRoleParam)) {
            //根据名称模糊查询
            if(ObjectUtil.isNotEmpty(sysRoleParam.getName())) {
                queryWrapper.like(SysRole::getName, sysRoleParam.getName());
            }
            //根据编码模糊查询
            if(ObjectUtil.isNotEmpty(sysRoleParam.getCode())) {
                queryWrapper.like(SysRole::getCode, sysRoleParam.getCode());
            }
        }
        //查询角色列表时，如果当前登录用户不是超级管理员，则查询自己拥有的
        if(!LoginContextHolder.me().isSuperAdmin()) {

            //查询自己拥有的
            List<String> loginUserRoleIds = LoginContextHolder.me().getLoginUserRoleIds();
            if(ObjectUtil.isEmpty(loginUserRoleIds)) {
                return new PageResult<>();
            }
            queryWrapper.in(SysRole::getId, loginUserRoleIds);
        }

        queryWrapper.eq(SysRole::getStatus, CommonStatusEnum.ENABLE.getCode());
        return new PageResult<>(this.page(PageFactory.defaultPage(), queryWrapper));
    }

    /**
     * 根据角色名模糊搜索系统角色列表
     *
     * @author xuyuxiang
     * @date 2020/4/14 17:21
     */
    @Override
    public List<Dict> list(SysRoleParam sysRoleParam) {
        List<Dict> dictList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotNull(sysRoleParam)) {
            //根据角色名称或编码模糊查询
            if(ObjectUtil.isNotEmpty(sysRoleParam.getName())) {
                queryWrapper.and(i ->i.like(SysRole::getName, sysRoleParam.getName())
                        .or().like(SysRole::getCode, sysRoleParam.getName()));
            }
        }
        //只查询正常状态
        queryWrapper.eq(SysRole::getStatus, CommonStatusEnum.ENABLE.getCode());
        this.list(queryWrapper).forEach(sysRole -> {
            Dict dict = Dict.create();
            dict.put(CommonConstant.ID, sysRole.getId());
            dict.put(CommonConstant.NAME, sysRole.getName() + SymbolConstant.LEFT_SQUARE_BRACKETS
                    + sysRole.getCode() + SymbolConstant.RIGHT_SQUARE_BRACKETS);
            dictList.add(dict);
        });
        return dictList;
    }

    /**
     * 系统角色下拉（用于授权角色时选择）
     *
     * @author xuyuxiang
     * @date 2020/4/5 16:48
     */
    @Override
    public List<Dict> dropDown() {
        List<Dict> dictList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        //如果当前登录用户不是超级管理员，则查询自己拥有的
        if(!LoginContextHolder.me().isSuperAdmin()) {

            //查询自己拥有的
            List<String> loginUserRoleIds = LoginContextHolder.me().getLoginUserRoleIds();
            if(ObjectUtil.isEmpty(loginUserRoleIds)) {
                return dictList;
            }
            queryWrapper.in(SysRole::getId, loginUserRoleIds);
        }
        //只查询正常状态
        queryWrapper.eq(SysRole::getStatus, CommonStatusEnum.ENABLE.getCode());
        this.list(queryWrapper)
            .forEach(sysRole -> {
            Dict dict = Dict.create();
            dict.put(CommonConstant.ID, sysRole.getId());
            dict.put(CommonConstant.CODE, sysRole.getCode());
            dict.put(CommonConstant.NAME, sysRole.getName());
            dictList.add(dict);
        });
        return dictList;
    }

    /**
     * 添加系统角色
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:58
     */
    @Override
    public void add(SysRoleParam sysRoleParam) {
        //校验参数，检查是否存在相同的名称和编码
        checkParam(sysRoleParam, false);
        SysRole sysRole = new SysRole();
        BeanUtil.copyProperties(sysRoleParam, sysRole);
        sysRole.setStatus(CommonStatusEnum.ENABLE.getCode());
        this.save(sysRole);
    }

    /**
     * 删除系统角色
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:58
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.querySysRole(sysRoleParam);
        sysRole.setStatus(CommonStatusEnum.DELETED.getCode());
        this.updateById(sysRole);
        Long id = sysRole.getId();
        //级联删除该角色对应的角色-数据范围关联信息
        sysRoleDataScopeService.deleteRoleDataScopeListByRoleId(id);

        //级联删除该角色对应的用户-角色表关联信息
        sysUserRoleService.deleteUserRoleListByRoleId(id);

        //级联删除该角色对应的角色-菜单表关联信息
        sysRoleMenuService.deleteRoleMenuListByRoleId(id);
    }

    /**
     * 编辑系统角色
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:58
     */
    @Override
    public void edit(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.querySysRole(sysRoleParam);
        //校验参数，检查是否存在相同的名称和编码
        checkParam(sysRoleParam, true);
        BeanUtil.copyProperties(sysRoleParam, sysRole);
        //不能修改状态，用修改状态接口修改状态
        sysRole.setStatus(null);
        this.updateById(sysRole);
    }

    /**
     * 查看系统角色
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:59
     */
    @Override
    public SysRole detail(SysRoleParam sysRoleParam) {
        return this.querySysRole(sysRoleParam);
    }

    /**
     * 授权菜单
     *
     * @author xuyuxiang
     * @date 2020/3/28 16:19
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void grantMenu(SysRoleParam sysRoleParam) {
        this.querySysRole(sysRoleParam);
        sysRoleMenuService.grantMenu(sysRoleParam);
    }

    /**
     * 授权数据
     *
     * @author xuyuxiang
     * @date 2020/3/28 16:20
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void grantData(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.querySysRole(sysRoleParam);
        boolean superAdmin = LoginContextHolder.me().isSuperAdmin();
        //如果登录用户不是超级管理员，则进行数据权限校验
        if (!superAdmin) {
            Integer dataScopeType = sysRoleParam.getDataScopeType();
            //如果授权的角色的数据范围类型为全部，则没权限，只有超级管理员有
            if(DataScopeTypeEnum.ALL.getCode().equals(dataScopeType)) {
                throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
            }
            //如果授权的角色数据范围类型为自定义，则要判断授权的数据范围是否在自己的数据范围内
            if(DataScopeTypeEnum.DEFINE.getCode().equals(dataScopeType)){
                List<Long> dataScope = sysRoleParam.getDataScope();
                //要授权的数据范围列表
                List<Long> grantOrgIdList = sysRoleParam.getGrantOrgIdList();
                if(ObjectUtil.isNotEmpty(grantOrgIdList)) {
                    //数据范围为空
                    if (ObjectUtil.isEmpty(dataScope)) {
                        throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
                    } else if(!dataScope.containsAll(grantOrgIdList)) {
                        //所要授权的数据不在自己的数据范围内
                        throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
                    }
                }
            }
        }
        sysRole.setDataScopeType(sysRoleParam.getDataScopeType());
        this.updateById(sysRole);
        sysRoleDataScopeService.grantDataScope(sysRoleParam);
    }

    /**
     * 根据角色id集合获取数据范围集合
     * 数据范围类型（字典 1全部数据 2本部门及以下数据 3本部门数据 4仅本人数据 5自定义数据）
     *
     * @author xuyuxiang
     * @date 2020/4/5 17:42
     */
    @Override
    public List<Long> getUserDataScopeIdList(List<Long> roleIdList, Long orgId) {
        Set<Long> resultList = CollectionUtil.newHashSet();

        //自定义数据范围的角色id集合
        Integer minDataScopeType = DataScopeTypeEnum.SELF.getCode();

        //固定数据范围的角色id集合
        List<Long> customDataScopeRoleIdList = CollectionUtil.newArrayList();
        if(ObjectUtil.isNotEmpty(roleIdList)) {
            List<SysRole> sysRoleList = this.listByIds(roleIdList);
            for (SysRole sysRole: sysRoleList) {
                if(DataScopeTypeEnum.DEFINE.getCode().equals(sysRole.getDataScopeType())) {
                    customDataScopeRoleIdList.add(sysRole.getId());
                } else {
                    if(sysRole.getDataScopeType() <= minDataScopeType) {
                        minDataScopeType = sysRole.getDataScopeType();
                    }
                }
            }
        }

        //自定义数据返回id集合
        List<Long> roleDataScopeIdList = sysRoleDataScopeService.getRoleDataScopeIdList(customDataScopeRoleIdList);

        //固定数据范围id集合
        List<Long> dataScopeIdList = sysOrgService.getDataScopeListByDataScopeType(minDataScopeType, orgId);

        resultList.addAll(dataScopeIdList);
        resultList.addAll(roleDataScopeIdList);
        return CollectionUtil.newArrayList(resultList);
    }

    /**
     * 根据角色id获取名称
     *
     * @author xuyuxiang
     * @date 2020/5/22 16:15
     */
    @Override
    public String getNameByRoleId(Long roleId) {
        SysRole sysRole = this.getById(roleId);
        if(ObjectUtil.isEmpty(sysRole)) {
            throw new ServiceException(SysRoleExceptionEnum.ROLE_NOT_EXIST);
        }
        return sysRole.getName();
    }

    /**
     * 查询角色拥有菜单
     *
     * @author xuyuxiang
     * @date 2020/5/29 14:04
     */
    @Override
    public List<Long> ownMenu(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.querySysRole(sysRoleParam);
        return sysRoleMenuService.getRoleMenuIdList(CollectionUtil.newArrayList(sysRole.getId()));
    }

    /**
     * 查询角色拥有数据
     *
     * @author xuyuxiang
     * @date 2020/5/29 14:04
     */
    @Override
    public List<Long> ownData(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.querySysRole(sysRoleParam);
        return sysRoleDataScopeService.getRoleDataScopeIdList(CollectionUtil.newArrayList(sysRole.getId()));
    }

    /**
     * 校验参数，检查是否存在相同的名称和编码
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:59
     */
    private void checkParam(SysRoleParam sysRoleParam, boolean isExcludeSelf) {
        Long id = sysRoleParam.getId();
        String name = sysRoleParam.getName();
        String code = sysRoleParam.getCode();

        LambdaQueryWrapper<SysRole> queryWrapperByName = new LambdaQueryWrapper<>();
        queryWrapperByName.eq(SysRole::getName, name)
                .ne(SysRole::getStatus, CommonStatusEnum.DELETED.getCode());

        LambdaQueryWrapper<SysRole> queryWrapperByCode = new LambdaQueryWrapper<>();
        queryWrapperByCode.eq(SysRole::getCode, code)
                .ne(SysRole::getStatus, CommonStatusEnum.DELETED.getCode());

        //是否排除自己，如果排除自己则不查询自己的id
        if(isExcludeSelf) {
            queryWrapperByName.ne(SysRole::getId, id);
            queryWrapperByCode.ne(SysRole::getId, id);
        }
        int countByName = this.count(queryWrapperByName);
        int countByCode = this.count(queryWrapperByCode);

        if (countByName >= 1) {
            throw new ServiceException(SysRoleExceptionEnum.ROLE_NAME_REPEAT);
        }
        if (countByCode >= 1) {
            throw new ServiceException(SysRoleExceptionEnum.ROLE_CODE_REPEAT);
        }
    }

    /**
     * 获取系统角色
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:59
     */
    private SysRole querySysRole(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.getById(sysRoleParam.getId());
        if(ObjectUtil.isNull(sysRole)) {
            throw new ServiceException(SysRoleExceptionEnum.ROLE_NOT_EXIST);
        }
        return sysRole;
    }
}
