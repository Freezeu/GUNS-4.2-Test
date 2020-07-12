package cn.stylefeng.guns.sys.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.stylefeng.guns.core.context.constant.ConstantContextHolder;
import cn.stylefeng.guns.core.pojo.email.EmailConfigs;
import cn.stylefeng.roses.email.MailSender;
import cn.stylefeng.roses.email.modular.SimpleMailSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 邮件发送控制器
 *
 * @author stylefeng
 * @date 2020/6/6 22:27
 */
@Configuration
public class MailSenderConfig {

    /**
     * 邮件发射器
     *
     * @author stylefeng
     * @date 2020/6/9 23:13
     */
    @Bean
    public MailSender mailSender() {
        EmailConfigs emailConfigs = ConstantContextHolder.getEmailConfigs();
        MailAccount mailAccount = new MailAccount();
        BeanUtil.copyProperties(emailConfigs, mailAccount);
        return new SimpleMailSender(mailAccount);
    }

}
