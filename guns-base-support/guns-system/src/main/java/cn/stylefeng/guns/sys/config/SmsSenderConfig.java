package cn.stylefeng.guns.sys.config;

import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.guns.core.context.constant.ConstantContextHolder;
import cn.stylefeng.guns.core.pojo.sms.AliyunSmsConfigs;
import cn.stylefeng.roses.sms.SmsSender;
import cn.stylefeng.roses.sms.modular.aliyun.AliyunSmsSender;
import cn.stylefeng.roses.sms.modular.aliyun.msign.impl.MapBasedMultiSignManager;
import cn.stylefeng.roses.sms.modular.aliyun.prop.AliyunSmsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 短信发送配置，短信发送的配置属性都在数据库的sys_config表中
 * <p>
 * 默认开启了阿里云的短信配置
 *
 * @author stylefeng
 * @date 2020/6/6 22:27
 */
@Configuration
public class SmsSenderConfig {

    /**
     * 短信发送器（阿里云）
     *
     * @author stylefeng
     * @date 2020/6/6 22:30
     */
    @Bean
    public SmsSender aliyunSmsSender() {

        // 从数据库配置读取阿里云配置
        AliyunSmsConfigs aliyunSmsConfigs = ConstantContextHolder.getAliyunSmsConfigs();
        AliyunSmsProperties aliyunSmsProperties = new AliyunSmsProperties();
        BeanUtil.copyProperties(aliyunSmsConfigs, aliyunSmsProperties);

        return new AliyunSmsSender(new MapBasedMultiSignManager(), aliyunSmsProperties);
    }

}
