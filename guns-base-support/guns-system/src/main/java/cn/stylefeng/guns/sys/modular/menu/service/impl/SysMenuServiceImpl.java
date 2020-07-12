package cn.stylefeng.guns.sys.modular.menu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.stylefeng.guns.core.consts.SymbolConstant;
import cn.stylefeng.guns.core.context.login.LoginContextHolder;
import cn.stylefeng.guns.core.enums.CommonStatusEnum;
import cn.stylefeng.guns.core.enums.YesOrNotEnum;
import cn.stylefeng.guns.core.exception.ServiceException;
import cn.stylefeng.guns.core.factory.TreeBuildFactory;
import cn.stylefeng.guns.core.pojo.node.LoginMenuTreeNode;
import cn.stylefeng.guns.sys.core.cache.ResourceCache;
import cn.stylefeng.guns.sys.core.enums.AdminTypeEnum;
import cn.stylefeng.guns.sys.core.enums.MenuOpenTypeEnum;
import cn.stylefeng.guns.sys.core.enums.MenuTypeEnum;
import cn.stylefeng.guns.sys.core.enums.MenuWeightEnum;
import cn.stylefeng.guns.sys.modular.menu.entity.SysMenu;
import cn.stylefeng.guns.sys.modular.menu.enums.SysMenuExceptionEnum;
import cn.stylefeng.guns.sys.modular.menu.mapper.SysMenuMapper;
import cn.stylefeng.guns.sys.modular.menu.node.MenuTreeNode;
import cn.stylefeng.guns.sys.modular.menu.param.SysMenuParam;
import cn.stylefeng.guns.sys.modular.menu.service.SysMenuService;
import cn.stylefeng.guns.sys.modular.role.service.SysRoleMenuService;
import cn.stylefeng.guns.sys.modular.user.entity.SysUser;
import cn.stylefeng.guns.sys.modular.user.service.SysUserRoleService;
import cn.stylefeng.guns.sys.modular.user.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 系统菜单service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/13 16:05
 */
@Service
@SuppressWarnings("unchecked")
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysUserRoleService sysUserRoleService;

    @Resource
    private SysRoleMenuService sysRoleMenuService;

    @Resource
    private ResourceCache resourceCache;

    @Override
    public List<String> getLoginPermissions(Long userId) {
        Set<String> permissions = CollectionUtil.newHashSet();
        List<Long> roleIdList = sysUserRoleService.getUserRoleIdList(userId);
        if (ObjectUtil.isNotEmpty(roleIdList)) {
            List<Long> menuIdList = sysRoleMenuService.getRoleMenuIdList(roleIdList);
            if (ObjectUtil.isNotEmpty(menuIdList)) {
                LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();

                queryWrapper.in(SysMenu::getId, menuIdList).eq(SysMenu::getType, MenuTypeEnum.BTN.getCode())
                        .eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode());

                this.list(queryWrapper).forEach(sysMenu -> permissions.add(sysMenu.getPermission()));
            }
        }
        return CollectionUtil.newArrayList(permissions);
    }

    /**
     * 根据应用分类编码获取当前用户AntDesign菜单相关信息，前端使用
     *
     * @author yubaoshan
     * @date 2020/4/17 17:51
     */
    @Override
    public List<LoginMenuTreeNode> getLoginMenusAntDesign(Long userId, String appCode) {
        List<SysMenu> sysMenuList;
        //如果是超级管理员则展示所有系统权重菜单，不能展示业务权重菜单
        SysUser sysUser = sysUserService.getById(userId);
        Integer adminType = sysUser.getAdminType();

        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();

        if (AdminTypeEnum.SUPER_ADMIN.getCode().equals(adminType)) {

            //查询权重不为业务权重的且类型不是按钮的
            queryWrapper.eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode())
                    .eq(SysMenu::getApplication, appCode)
                    .notIn(SysMenu::getType, MenuTypeEnum.BTN.getCode())
                    .notIn(SysMenu::getWeight, MenuWeightEnum.DEFAULT_WEIGHT.getCode())
                    .orderByAsc(SysMenu::getSort);
        } else {

            //非超级管理员则获取自己角色所拥有的菜单集合
            List<Long> roleIdList = sysUserRoleService.getUserRoleIdList(userId);
            if (ObjectUtil.isNotEmpty(roleIdList)) {
                List<Long> menuIdList = sysRoleMenuService.getRoleMenuIdList(roleIdList);
                if (ObjectUtil.isNotEmpty(menuIdList)) {
                    queryWrapper.in(SysMenu::getId, menuIdList)
                            .eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode())
                            .eq(SysMenu::getApplication, appCode)
                            .notIn(SysMenu::getType, MenuTypeEnum.BTN.getCode())
                            .orderByAsc(SysMenu::getSort);

                } else {
                    //如果角色的菜单为空，则查不到菜单
                    return CollectionUtil.newArrayList();
                }
            } else {
                //如果角色为空，则根本没菜单
                return CollectionUtil.newArrayList();
            }
        }
        //查询列表
        sysMenuList = this.list(queryWrapper);
        //转换成登录菜单
        return this.convertSysMenuToLoginMenu(sysMenuList);
    }

    /**
     * 将SysMenu格式菜单转换为LoginMenuTreeNode菜单
     *
     * @author xuyuxiang
     * @date 2020/4/17 17:53
     */
    private List<LoginMenuTreeNode> convertSysMenuToLoginMenu(List<SysMenu> sysMenuList) {
        List<LoginMenuTreeNode> antDesignMenuTreeNodeList = CollectionUtil.newArrayList();
        sysMenuList.forEach(sysMenu -> {
            LoginMenuTreeNode loginMenuTreeNode = new LoginMenuTreeNode();
            loginMenuTreeNode.setComponent(sysMenu.getComponent());
            loginMenuTreeNode.setId(sysMenu.getId());
            loginMenuTreeNode.setName(sysMenu.getCode());
            loginMenuTreeNode.setPath(sysMenu.getRouter());
            loginMenuTreeNode.setPid(sysMenu.getPid());
            LoginMenuTreeNode.Meta mateItem = new LoginMenuTreeNode().new Meta();
            mateItem.setIcon(sysMenu.getIcon());
            mateItem.setTitle(sysMenu.getName());
            mateItem.setLink(sysMenu.getLink());
            //是否可见
            if (YesOrNotEnum.N.getCode().equals(sysMenu.getVisible())) {
                mateItem.setShow(false);
            } else {
                mateItem.setShow(true);
            }
            //设置的首页，默认打开此链接
            loginMenuTreeNode.setRedirect(sysMenu.getRedirect());
            //是否是外链
            if (MenuOpenTypeEnum.OUTER.getCode().equals(sysMenu.getOpenType())) {
                //打开外链
                mateItem.setTarget("_blank");
                loginMenuTreeNode.setPath(sysMenu.getLink());
                loginMenuTreeNode.setRedirect(sysMenu.getLink());
            }
            loginMenuTreeNode.setMeta(mateItem);
            antDesignMenuTreeNodeList.add(loginMenuTreeNode);
        });
        return antDesignMenuTreeNodeList;
    }

    /**
     * 获取用户菜单所属的应用编码集合
     *
     * @author xuyuxiang
     * @date 2020/3/21 11:01
     */
    @Override
    public List<String> getUserMenuAppCodeList(Long userId) {
        Set<String> appCodeSet = CollectionUtil.newHashSet();
        List<Long> roleIdList = sysUserRoleService.getUserRoleIdList(userId);

        if (ObjectUtil.isNotEmpty(roleIdList)) {
            List<Long> menuIdList = sysRoleMenuService.getRoleMenuIdList(roleIdList);

            if (ObjectUtil.isNotEmpty(menuIdList)) {
                LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.in(SysMenu::getId, menuIdList)
                        .eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode());

                this.list(queryWrapper).forEach(sysMenu -> appCodeSet.add(sysMenu.getApplication()));
            }
        }

        return CollectionUtil.newArrayList(appCodeSet);
    }

    /**
     * 系统菜单列表（树表）
     *
     * @author xuyuxiang
     * @date 2020/3/26 20:45
     */
    @Override
    public List<SysMenu> list(SysMenuParam sysMenuParam) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(sysMenuParam)) {
            //根据所属应用查询
            if (ObjectUtil.isNotEmpty(sysMenuParam.getApplication())) {
                queryWrapper.eq(SysMenu::getApplication, sysMenuParam.getApplication());
            }
            //根据菜单名称模糊查询
            if (ObjectUtil.isNotEmpty(sysMenuParam.getName())) {
                queryWrapper.like(SysMenu::getName, sysMenuParam.getName());
            }
        }
        queryWrapper.eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode());
        List<SysMenu> sysMenuList = this.list(queryWrapper);
        //将结果集处理成树
        return new TreeBuildFactory<SysMenu>().doTreeBuild(sysMenuList);
    }

    /**
     * 添加系统菜单
     *
     * @author xuyuxiang
     * @date 2020/3/27 9:10
     */
    @Override
    public void add(SysMenuParam sysMenuParam) {
        //校验参数
        checkParam(sysMenuParam, false);
        SysMenu sysMenu = new SysMenu();
        BeanUtil.copyProperties(sysMenuParam, sysMenu);
        this.fillPids(sysMenu);
        sysMenu.setStatus(CommonStatusEnum.ENABLE.getCode());
        this.save(sysMenu);
    }

    /**
     * 删除系统菜单
     *
     * @author xuyuxiang
     * @date 2020/3/27 9:11
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(SysMenuParam sysMenuParam) {
        Long id = sysMenuParam.getId();
        //级联删除子节点
        List<Long> childIdList = this.getChildIdListById(id);
        childIdList.add(id);
        LambdaUpdateWrapper<SysMenu> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(SysMenu::getId, childIdList)
                .set(SysMenu::getStatus, CommonStatusEnum.DELETED.getCode());
        this.update(updateWrapper);
        //级联删除该菜单及子菜单对应的角色-菜单表信息
        sysRoleMenuService.deleteRoleMenuListByMenuIdList(childIdList);
    }

    /**
     * 编辑系统菜单
     *
     * @author xuyuxiang
     * @date 2020/3/27 9:11
     */
    @Override
    public void edit(SysMenuParam sysMenuParam) {
        SysMenu sysMenu = this.querySysMenu(sysMenuParam);
        //校验参数
        checkParam(sysMenuParam, true);
        //如果应用有变化
        if (!sysMenuParam.getApplication().equals(sysMenu.getApplication())) {
            //如果该菜单不为叶子节点，则将其子节点的数据全部修改为该应用
            LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(SysMenu::getPids, sysMenu.getId());
            List<SysMenu> list = this.list(queryWrapper);
            if(ObjectUtil.isNotEmpty(list)) {
                list.forEach(child -> child.setApplication(sysMenu.getApplication()));
                this.updateBatchById(list);
            } else {
                //否则查询父节点
                Long pid = sysMenu.getPid();
                if (!pid.equals(0L)) {
                    SysMenu pSysMenu = this.getById(pid);
                    //如果父节点不属于该应用，则无法修改其为该应用
                    if (!pSysMenu.getApplication().equals(sysMenu.getApplication())) {
                        throw new ServiceException(SysMenuExceptionEnum.MENU_PARENT_APPLICATION_ERROR);
                    }
                }
            }
        }
        BeanUtil.copyProperties(sysMenuParam, sysMenu);
        this.fillPids(sysMenu);
        this.updateById(sysMenu);
    }

    /**
     * 查看系统菜单
     *
     * @author xuyuxiang
     * @date 2020/3/26 9:56
     */
    @Override
    public SysMenu detail(SysMenuParam sysMenuParam) {
        return this.querySysMenu(sysMenuParam);
    }


    /**
     * 获取系统菜单树，用于新增编辑时获取上级菜单
     *
     * @author xuyuxiang
     * @date 2020/3/27 15:57
     */
    @Override
    public List<MenuTreeNode> tree(SysMenuParam sysMenuParam) {
        List<MenuTreeNode> menuTreeNodeList = CollectionUtil.newArrayList();

        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();

        if (ObjectUtil.isNotNull(sysMenuParam)) {
            if (ObjectUtil.isNotEmpty(sysMenuParam.getApplication())) {
                queryWrapper.eq(SysMenu::getApplication, sysMenuParam.getApplication());
            }
        }
        queryWrapper.eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode())
                .in(SysMenu::getType, CollectionUtil.newArrayList(MenuTypeEnum.DIR.getCode(), MenuTypeEnum.MENU.getCode()));

        this.list(queryWrapper).forEach(sysMenu -> {
            MenuTreeNode menuTreeNode = new MenuTreeNode();
            menuTreeNode.setId(sysMenu.getId());
            menuTreeNode.setParentId(sysMenu.getPid());
            menuTreeNode.setValue(String.valueOf(sysMenu.getId()));
            menuTreeNode.setTitle(sysMenu.getName());
            menuTreeNode.setWeight(sysMenu.getSort());
            menuTreeNodeList.add(menuTreeNode);
        });
        return new TreeBuildFactory<MenuTreeNode>().doTreeBuild(menuTreeNodeList);
    }

    /**
     * 获取系统菜单树，用于给角色授权时选择
     *
     * @author xuyuxiang
     * @date 2020/4/5 15:01
     */
    @Override
    public List<MenuTreeNode> treeForGrant(SysMenuParam sysMenuParam) {
        List<MenuTreeNode> menuTreeNodeList = CollectionUtil.newArrayList();

        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        //根据应用查询
        if (ObjectUtil.isNotNull(sysMenuParam)) {
            if (ObjectUtil.isNotEmpty(sysMenuParam.getApplication())) {
                queryWrapper.eq(SysMenu::getApplication, sysMenuParam.getApplication());
            }
        }
        //如果是超级管理员给角色授权菜单时可选择所有菜单
        if (LoginContextHolder.me().isSuperAdmin()) {
            queryWrapper.eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode());
        } else {
            //非超级管理员则获取自己拥有的菜单，分配给人员，防止越级授权
            Long userId = LoginContextHolder.me().getSysLoginUserId();
            List<Long> roleIdList = sysUserRoleService.getUserRoleIdList(userId);
            if (ObjectUtil.isNotEmpty(roleIdList)) {
                List<Long> menuIdList = sysRoleMenuService.getRoleMenuIdList(roleIdList);
                if (ObjectUtil.isNotEmpty(menuIdList)) {
                    queryWrapper.in(SysMenu::getId, menuIdList)
                            .eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode());
                } else {
                    //如果角色的菜单为空，则查不到菜单
                    return CollectionUtil.newArrayList();
                }
            } else {
                //如果角色为空，则根本没菜单
                return CollectionUtil.newArrayList();
            }
        }
        this.list(queryWrapper).forEach(sysMenu -> {
            MenuTreeNode menuTreeNode = new MenuTreeNode();
            menuTreeNode.setId(sysMenu.getId());
            menuTreeNode.setParentId(sysMenu.getPid());
            menuTreeNode.setValue(String.valueOf(sysMenu.getId()));
            menuTreeNode.setTitle(sysMenu.getName());
            menuTreeNode.setWeight(sysMenu.getSort());
            menuTreeNodeList.add(menuTreeNode);
        });
        return new TreeBuildFactory<MenuTreeNode>().doTreeBuild(menuTreeNodeList);
    }

    /**
     * 根据应用编码判断该机构下是否有状态为正常的菜单
     *
     * @author xuyuxiang
     * @date 2020/6/28 12:17
     */
    @Override
    public boolean hasMenu(String appCode) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getApplication, appCode)
                .ne(SysMenu::getStatus, CommonStatusEnum.DELETED.getCode());
        return this.list(queryWrapper).size() != 0;
    }

    /**
     * 校验参数
     *
     * @author xuyuxiang
     * @date 2020/3/27 9:15
     */
    private void checkParam(SysMenuParam sysMenuParam, boolean isExcludeSelf) {
        //菜单类型（字典 0目录 1菜单 2按钮）
        Integer type = sysMenuParam.getType();

        String router = sysMenuParam.getRouter();

        String permission = sysMenuParam.getPermission();

        Integer openType = sysMenuParam.getOpenType();

        if (MenuTypeEnum.DIR.getCode().equals(type)) {
            if (ObjectUtil.isEmpty(router)) {
                throw new ServiceException(SysMenuExceptionEnum.MENU_ROUTER_EMPTY);
            }
        }

        if (MenuTypeEnum.MENU.getCode().equals(type)) {
            if (ObjectUtil.isEmpty(router)) {
                throw new ServiceException(SysMenuExceptionEnum.MENU_ROUTER_EMPTY);
            }
            if (ObjectUtil.isEmpty(openType)) {
                throw new ServiceException(SysMenuExceptionEnum.MENU_OPEN_TYPE_EMPTY);
            }
        }

        if (MenuTypeEnum.BTN.getCode().equals(type)) {
            if (ObjectUtil.isEmpty(permission)) {
                throw new ServiceException(SysMenuExceptionEnum.MENU_PERMISSION_EMPTY);
            } else {
                Set<String> urlSet = resourceCache.getAllResources();

                if (!permission.contains(SymbolConstant.COLON)) {
                    throw new ServiceException(SysMenuExceptionEnum.MENU_PERMISSION_ERROR);
                }
                permission = SymbolConstant.COLON + permission;
                if (!urlSet.contains(permission.replaceAll(SymbolConstant.COLON, SymbolConstant.LEFT_DIVIDE))) {
                    throw new ServiceException(SysMenuExceptionEnum.MENU_PERMISSION_NOT_EXIST);
                }
            }
        }

        Long id = sysMenuParam.getId();
        String name = sysMenuParam.getName();
        String code = sysMenuParam.getCode();

        LambdaQueryWrapper<SysMenu> queryWrapperByName = new LambdaQueryWrapper<>();
        queryWrapperByName.eq(SysMenu::getName, name)
                .ne(SysMenu::getStatus, CommonStatusEnum.DELETED.getCode());

        LambdaQueryWrapper<SysMenu> queryWrapperByCode = new LambdaQueryWrapper<>();
        queryWrapperByCode.eq(SysMenu::getCode, code)
                .ne(SysMenu::getStatus, CommonStatusEnum.DELETED.getCode());

        if (isExcludeSelf) {
            queryWrapperByName.ne(SysMenu::getId, id);
            queryWrapperByCode.ne(SysMenu::getId, id);
        }
        int countByName = this.count(queryWrapperByName);
        int countByCode = this.count(queryWrapperByCode);

        if (countByName >= 1) {
            throw new ServiceException(SysMenuExceptionEnum.MENU_NAME_REPEAT);
        }
        if (countByCode >= 1) {
            throw new ServiceException(SysMenuExceptionEnum.MENU_CODE_REPEAT);
        }
    }

    /**
     * 获取系统菜单
     *
     * @author xuyuxiang
     * @date 2020/3/27 9:13
     */
    private SysMenu querySysMenu(SysMenuParam sysMenuParam) {
        SysMenu sysMenu = this.getById(sysMenuParam.getId());
        if (ObjectUtil.isNull(sysMenu)) {
            throw new ServiceException(SysMenuExceptionEnum.MENU_NOT_EXIST);
        }
        return sysMenu;
    }

    /**
     * 填充父ids
     *
     * @author xuyuxiang
     * @date 2020/3/26 11:28
     */
    private void fillPids(SysMenu sysMenu) {
        if (sysMenu.getPid().equals(0L)) {
            sysMenu.setPids(SymbolConstant.LEFT_SQUARE_BRACKETS +
                    0 +
                    SymbolConstant.RIGHT_SQUARE_BRACKETS +
                    SymbolConstant.COMMA);
        } else {
            //获取父组织机构
            SysMenu pSysMenu = this.getById(sysMenu.getPid());
            sysMenu.setPids(pSysMenu.getPids() +
                    SymbolConstant.LEFT_SQUARE_BRACKETS + pSysMenu.getId() +
                    SymbolConstant.RIGHT_SQUARE_BRACKETS +
                    SymbolConstant.COMMA);
        }
    }

    /**
     * 根据节点id获取所有子节点id集合
     *
     * @author xuyuxiang
     * @date 2020/3/26 11:31
     */
    private List<Long> getChildIdListById(Long id) {
        List<Long> childIdList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(SysMenu::getPids, SymbolConstant.LEFT_SQUARE_BRACKETS + id +
                SymbolConstant.RIGHT_SQUARE_BRACKETS);
        this.list(queryWrapper).forEach(sysMenu -> childIdList.add(sysMenu.getId()));
        return childIdList;
    }
}
