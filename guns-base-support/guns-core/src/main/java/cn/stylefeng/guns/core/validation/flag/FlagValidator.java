package cn.stylefeng.guns.core.validation.flag;

import cn.stylefeng.guns.core.enums.YesOrNotEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 校验标识，只有Y和N两种状态的标识
 *
 * @author stylefeng
 * @date 2020/4/14 23:49
 */
public class FlagValidator implements ConstraintValidator<Flag, String> {

    @Override
    public boolean isValid(String flagValue, ConstraintValidatorContext context) {
        return YesOrNotEnum.Y.getCode().equals(flagValue) || YesOrNotEnum.N.getCode().equals(flagValue);
    }
}
