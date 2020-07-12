package cn.stylefeng.guns.sys.config;

import cn.stylefeng.guns.sys.core.aop.BusinessLogAop;
import cn.stylefeng.guns.sys.core.aop.DataScopeAop;
import cn.stylefeng.guns.sys.core.aop.PermissionAop;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 切面配置
 *
 * @author xuyuxiang
 * @date 2020/3/18 11:25
 */
@Configuration
public class AopConfig {

    /**
     * 日志切面
     *
     * @author xuyuxiang
     * @date 2020/3/20 14:10
     */
    @Bean
    public BusinessLogAop businessLogAop() {
        return new BusinessLogAop();
    }

    /**
     * 权限切面
     *
     * @author xuyuxiang
     * @date 2020/3/23 17:36
     */
    @Bean
    public PermissionAop permissionAop() {
        return new PermissionAop();
    }

    /**
     * 数据范围切面
     *
     * @author xuyuxiang
     * @date 2020/4/6 13:47
     */
    @Bean
    public DataScopeAop dataScopeAop() {
        return new DataScopeAop();
    }
}
