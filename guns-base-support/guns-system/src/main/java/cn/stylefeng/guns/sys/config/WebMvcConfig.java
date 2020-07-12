package cn.stylefeng.guns.sys.config;

import cn.stylefeng.guns.sys.core.error.GunsErrorAttributes;
import cn.stylefeng.guns.sys.core.filter.RequestNoFilter;
import cn.stylefeng.guns.sys.core.filter.xss.XssFilter;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web配置
 *
 * @author stylefeng
 * @date 2020/4/11 10:23
 */
@Configuration
@Import({cn.hutool.extra.spring.SpringUtil.class})
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 错误信息提示重写
     *
     * @author stylefeng
     * @date 2020/4/14 22:27
     */
    @Bean
    public GunsErrorAttributes gunsErrorAttributes() {
        return new GunsErrorAttributes();
    }

    /**
     * 静态资源映射
     *
     * @author stylefeng
     * @date 2020/4/11 10:23
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //swagger增强的静态资源映射
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * xss过滤器
     *
     * @author stylefeng
     * @date 2020/6/21 10:30
     */
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterFilterRegistrationBean() {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>(new XssFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    /**
     * 请求唯一编号生成器
     *
     * @author stylefeng
     * @date 2020/6/21 10:30
     */
    @Bean
    public FilterRegistrationBean<RequestNoFilter> requestNoFilterFilterRegistrationBean() {
        FilterRegistrationBean<RequestNoFilter> registration = new FilterRegistrationBean<>(new RequestNoFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    /**
     * json自定义序列化工具,long转string
     *
     * @author stylefeng
     * @date 2020/5/28 14:48
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder ->
                jacksonObjectMapperBuilder
                        .serializerByType(Long.class, ToStringSerializer.instance)
                        .serializerByType(Long.TYPE, ToStringSerializer.instance);
    }

}