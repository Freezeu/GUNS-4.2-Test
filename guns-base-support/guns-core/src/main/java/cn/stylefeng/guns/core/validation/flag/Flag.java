package cn.stylefeng.guns.core.validation.flag;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 校验标识，只有Y和N两种状态的标识
 *
 * @author stylefeng
 * @date 2020/4/14 23:49
 */
@Documented
@Constraint(validatedBy = FlagValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Flag {

    String message() default "不正确的状态标识，请传递Y或者N";

    Class[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        NotNull[] value();
    }
}
