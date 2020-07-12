package cn.stylefeng.guns.sys.modular.email.controler;

import cn.hutool.core.util.ObjectUtil;
import cn.stylefeng.guns.core.annotion.BusinessLog;
import cn.stylefeng.guns.core.enums.LogAnnotionOpTypeEnum;
import cn.stylefeng.guns.core.exception.ServiceException;
import cn.stylefeng.guns.core.pojo.response.ResponseData;
import cn.stylefeng.guns.core.pojo.response.SuccessResponseData;
import cn.stylefeng.guns.sys.modular.email.enums.SysEmailExceptionEnum;
import cn.stylefeng.roses.email.MailSender;
import cn.stylefeng.roses.email.modular.model.SendMailParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 邮件发送控制器
 *
 * @author stylefeng
 * @date 2020/6/9 23:02
 */
@RestController
public class EmailController {

    @Resource
    private MailSender mailSender;

    /**
     * 发送邮件
     *
     * @author stylefeng,xuyuxiang
     * @date 2020/6/9 23:02
     */
    @PostMapping("/email/sendEmail")
    @BusinessLog(title = "发送邮件", opType = LogAnnotionOpTypeEnum.OTHER)
    public ResponseData sendEmail(@RequestBody SendMailParam sendMailParam) {
        String to = sendMailParam.getTo();
        if(ObjectUtil.isEmpty(to)) {
            throw new ServiceException(SysEmailExceptionEnum.EMAIL_TO_EMPTY);
        }

        String title = sendMailParam.getTitle();
        if(ObjectUtil.isEmpty(title)) {
            throw new ServiceException(SysEmailExceptionEnum.EMAIL_TITLE_EMPTY);
        }

        String content = sendMailParam.getContent();
        if(ObjectUtil.isEmpty(content)) {
            throw new ServiceException(SysEmailExceptionEnum.EMAIL_CONTENT_EMPTY);
        }
        mailSender.sendMail(sendMailParam);
        return new SuccessResponseData();
    }

    /**
     * 发送邮件(html)
     *
     * @author stylefeng,xuyuxiang
     * @date 2020/6/9 23:02
     */
    @PostMapping("/email/sendEmailHtml")
    @BusinessLog(title = "发送邮件", opType = LogAnnotionOpTypeEnum.OTHER)
    public ResponseData sendEmailHtml(@RequestBody SendMailParam sendMailParam) {
        String to = sendMailParam.getTo();
        if(ObjectUtil.isEmpty(to)) {
            throw new ServiceException(SysEmailExceptionEnum.EMAIL_TO_EMPTY);
        }

        String title = sendMailParam.getTitle();
        if(ObjectUtil.isEmpty(title)) {
            throw new ServiceException(SysEmailExceptionEnum.EMAIL_TITLE_EMPTY);
        }

        String content = sendMailParam.getContent();
        if(ObjectUtil.isEmpty(content)) {
            throw new ServiceException(SysEmailExceptionEnum.EMAIL_CONTENT_EMPTY);
        }
        mailSender.sendMailHtml(sendMailParam);
        return new SuccessResponseData();
    }
}
