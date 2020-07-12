package cn.stylefeng.guns.sys.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.stylefeng.guns.core.context.constant.ConstantContextHolder;
import cn.stylefeng.guns.sys.config.properties.DruidProperties;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * Druid配置
 *
 * @author stylefeng
 * @date 2017/5/20 21:58
 */
@Configuration
public class DataSourceConfig {

    /**
     * druid配置
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DruidProperties druidProperties() {
        return new DruidProperties();
    }

    /**
     * druid数据库连接池
     */
    @Bean(initMethod = "init")
    public DruidDataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        druidProperties().config(dataSource);
        return dataSource;
    }

    /**
     * druid监控，配置StatViewServlet
     *
     * @author xuyuxiang
     * @date 2020/6/28 16:03
     */
    @Bean
    public ServletRegistrationBean<StatViewServlet> druidServletRegistration() {

        // 设置servelet的参数
        HashMap<String, String> statViewServletParams = CollectionUtil.newHashMap();
        statViewServletParams.put("resetEnable", "true");
        statViewServletParams.put("loginUsername", ConstantContextHolder.getDruidMonitorUsername());
        statViewServletParams.put("loginPassword", ConstantContextHolder.getDruidMonitorPassword());


        ServletRegistrationBean<StatViewServlet> registration = new ServletRegistrationBean<>(new StatViewServlet());
        registration.addUrlMappings("/druid/*");
        registration.setInitParameters(statViewServletParams);
        return registration;
    }

}
