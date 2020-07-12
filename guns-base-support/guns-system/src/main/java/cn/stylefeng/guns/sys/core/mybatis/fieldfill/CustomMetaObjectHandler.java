package cn.stylefeng.guns.sys.core.mybatis.fieldfill;

import cn.hutool.log.Log;
import cn.stylefeng.guns.core.consts.SymbolConstant;
import cn.stylefeng.guns.core.context.login.LoginContextHolder;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectionException;

import java.util.Date;

/**
 * 自定义sql字段填充器，自动填充创建修改相关字段
 *
 * @author xuyuxiang
 * @date 2020/3/30 15:21
 */
public class CustomMetaObjectHandler implements MetaObjectHandler {

    private static final Log log = Log.get();

    private static final String CREATE_USER = "createUser";

    private static final String CREATE_TIME = "createTime";

    private static final String UPDATE_USER = "updateUser";

    private static final String UPDATE_TIME = "updateTime";

    private static final String FLW_PROCESS_STATUS = "flwProcessStatus";

    private static final String FLW_START_USER_ID = "flwStartUserId";

    private static final String FLW_START_USER_NAME = "flwStartUserName";

    private static final String FLW_START_ORG_ID = "flwStartOrgId";

    private static final String FLW_START_ORG_NAME = "flwStartOrgName";

    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            //设置createUser（BaseEntity)
            setFieldValByName(CREATE_USER, this.getUserUniqueId(), metaObject);

            //设置createTime（BaseEntity)
            setFieldValByName(CREATE_TIME, new Date(), metaObject);

            //设置processStatus为草稿（BaseFlowableEntity）
            setFieldValByName(FLW_PROCESS_STATUS, 0, metaObject);

            //设置flwStartUserId（BaseFlowableEntity）
            setFieldValByName(FLW_START_USER_ID, this.getUserUniqueId(), metaObject);

            //设置flwStartUserName（BaseFlowableEntity）
            setFieldValByName(FLW_START_USER_NAME, this.getUserName(), metaObject);

            //设置flwStartOrgId（BaseFlowableEntity）
            setFieldValByName(FLW_START_ORG_ID, this.getUserOrgId(), metaObject);

            //设置flwStartOrgName（BaseFlowableEntity）
            setFieldValByName(FLW_START_ORG_NAME, this.getUserOrgName(), metaObject);
        } catch (ReflectionException e) {
            log.warn(">>> CustomMetaObjectHandler处理过程中无相关字段，不做处理");
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            //设置updateUser（BaseEntity)
            setFieldValByName(UPDATE_USER, this.getUserUniqueId(), metaObject);
            //设置updateTime（BaseEntity)
            setFieldValByName(UPDATE_TIME, new Date(), metaObject);
        } catch (ReflectionException e) {
            log.warn(">>> CustomMetaObjectHandler处理过程中无相关字段，不做处理");
        }
    }

    /**
     * 获取用户唯一id
     */
    private Long getUserUniqueId() {
        try {
            return LoginContextHolder.me().getSysLoginUserId();
        } catch (Exception e) {
            //如果获取不到就返回-1
            return -1L;
        }
    }

    /**
     * 获取用户姓名
     */
    private String getUserName() {
        try {
            return LoginContextHolder.me().getSysLoginUser().getName();
        } catch (Exception e) {
            //如果获取不到户就返回"-"
            return SymbolConstant.DASH;
        }
    }

    /**
     * 获取用户所属机构id
     */
    private Long getUserOrgId() {
        try {
            return LoginContextHolder.me().getSysLoginUser().getLoginEmpInfo().getOrgId();
        } catch (Exception e) {
            //如果获取不到就返回-1
            return -1L;
        }
    }

    /**
     * 获取用户所属机构名称
     */
    private String getUserOrgName() {
        try {
            return LoginContextHolder.me().getSysLoginUser().getLoginEmpInfo().getOrgName();
        } catch (Exception e) {
            //如果获取不到就返回"-"
            return SymbolConstant.DASH;
        }
    }
}