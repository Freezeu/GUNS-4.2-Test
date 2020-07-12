package cn.stylefeng.guns.sys.config;

import cn.stylefeng.guns.sys.core.mybatis.dbid.GunsDatabaseIdProvider;
import cn.stylefeng.guns.sys.core.mybatis.fieldfill.CustomMetaObjectHandler;
import cn.stylefeng.guns.sys.core.mybatis.sqlfilter.DemoProfileSqlInterceptor;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis扩展插件配置
 *
 * @author xuyuxiang, fengshuonan
 * @date 2020/3/18 10:49
 */
@Configuration
@MapperScan(basePackages = {"cn.stylefeng.guns.**.mapper"})
public class MybatisConfig {

    /**
     * mybatis-plus分页插件
     *
     * @author xuyuxiang
     * @date 2020/3/31 15:42
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    /**
     * 演示环境的sql拦截器
     * <p>
     * 演示环境只开放查询操作，其他都不允许
     *
     * @author stylefeng
     * @date 2020/5/5 12:24
     */
    @Bean
    public DemoProfileSqlInterceptor demoProfileSqlInterceptor() {
        return new DemoProfileSqlInterceptor();
    }

    /**
     * 自定义公共字段自动注入
     *
     * @author xuyuxiang
     * @date 2020/3/31 15:42
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new CustomMetaObjectHandler();
    }

    /**
     * 数据库id选择器
     *
     * @author stylefeng
     * @date 2020/6/20 21:23
     */
    @Bean
    public GunsDatabaseIdProvider gunsDatabaseIdProvider() {
        return new GunsDatabaseIdProvider();
    }

}
