package cn.stylefeng.guns.sys.modular.email.enums;

import cn.stylefeng.guns.core.annotion.ExpEnumType;
import cn.stylefeng.guns.core.consts.ExpEnumConstant;
import cn.stylefeng.guns.core.exception.enums.abs.AbstractBaseExceptionEnum;
import cn.stylefeng.guns.core.factory.ExpEnumCodeFactory;
import cn.stylefeng.guns.sys.core.consts.SysExpEnumConstant;

/**
 * 系统应用相关异常枚举
 *
 * @author xuyuxiang
 * @date 2020/3/26 10:11
 */
@ExpEnumType(module = ExpEnumConstant.GUNS_SYS_MODULE_EXP_CODE, kind = SysExpEnumConstant.EMAIL_EXCEPTION_ENUM)
public enum SysEmailExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 收件人为空
     */
    EMAIL_TO_EMPTY(1, "收件人为空，请检查to参数"),

    /**
     * 标题为空
     */
    EMAIL_TITLE_EMPTY(2, "标题为空，请检查title参数"),

    /**
     * 内容为空
     */
    EMAIL_CONTENT_EMPTY(3, "内容为空，请检查content参数");

    private final Integer code;

    private final String message;

    SysEmailExceptionEnum(Integer code, String message) {
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
