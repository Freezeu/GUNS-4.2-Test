package cn.stylefeng.guns.sys.modular.menu.enums;

import cn.stylefeng.guns.core.annotion.ExpEnumType;
import cn.stylefeng.guns.core.consts.ExpEnumConstant;
import cn.stylefeng.guns.core.exception.enums.abs.AbstractBaseExceptionEnum;
import cn.stylefeng.guns.core.factory.ExpEnumCodeFactory;
import cn.stylefeng.guns.sys.core.consts.SysExpEnumConstant;

/**
 * 系统菜单相关异常枚举
 *
 * @author xuyuxiang
 * @date 2020/3/26 10:12
 */
@ExpEnumType(module = ExpEnumConstant.GUNS_SYS_MODULE_EXP_CODE, kind = SysExpEnumConstant.SYS_MENU_EXCEPTION_ENUM)
public enum SysMenuExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 菜单不存在
     */
    MENU_NOT_EXIST(1, "菜单不存在"),

    /**
     * 菜单编码重复
     */
    MENU_CODE_REPEAT(2, "菜单编码重复，请检查code参数"),

    /**
     * 菜单名称重复
     */
    MENU_NAME_REPEAT(3, "菜单名称重复，请检查name参数"),

    /**
     * 路由地址为空
     */
    MENU_ROUTER_EMPTY(4, "路由地址为空，请检查router参数"),

    /**
     * 组件地址为空
     */
    MENU_COMPONENT_EMPTY(5, "组件地址为空，请检查component参数"),

    /**
     * 打开方式为空
     */
    MENU_OPEN_TYPE_EMPTY(6, "打开方式为空，请检查openType参数"),

    /**
     * 权限标识格式为空
     */
    MENU_PERMISSION_EMPTY(7, "权限标识为空，请检查permission参数"),

    /**
     * 权限标识格式错误
     */
    MENU_PERMISSION_ERROR(8, "权限标识格式错误，请检查permission参数"),

    /**
     * 权限不存在
     */
    MENU_PERMISSION_NOT_EXIST(9, "权限不存在，请检查permission参数"),

    /**
     * 编辑失败
     */
    MENU_PARENT_APPLICATION_ERROR(10, "编辑失败，菜单所属应用应与父菜单所属应用一致");

    private final Integer code;

    private final String message;

    SysMenuExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return ExpEnumCodeFactory.getExpEnumCode(this.getClass(), code);
    }

    @Override
    public String getMessage() {
        return message;
    }

}
