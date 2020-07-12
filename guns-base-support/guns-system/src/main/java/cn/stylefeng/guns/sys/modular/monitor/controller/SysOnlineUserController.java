package cn.stylefeng.guns.sys.modular.monitor.controller;

import cn.stylefeng.guns.core.annotion.BusinessLog;
import cn.stylefeng.guns.core.annotion.Permission;
import cn.stylefeng.guns.core.enums.LogAnnotionOpTypeEnum;
import cn.stylefeng.guns.core.pojo.response.ResponseData;
import cn.stylefeng.guns.core.pojo.response.SuccessResponseData;
import cn.stylefeng.guns.sys.modular.monitor.param.SysOnlineUserParam;
import cn.stylefeng.guns.sys.modular.monitor.service.SysOnlineUserService;
import cn.stylefeng.guns.sys.modular.org.param.SysOrgParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 在线用户控制器
 *
 * @author xuyuxiang
 * @date 2020/4/7 16:57
 */
@RestController
public class SysOnlineUserController {

    @Resource
    private SysOnlineUserService sysOnlineUserService;

    /**
     * 在线用户列表
     *
     * @author xuyuxiang
     * @date 2020/4/7 16:58
     */
    @Permission
    @GetMapping("/sysOnlineUser/list")
    @BusinessLog(title = "系统在线用户_列表", opType = LogAnnotionOpTypeEnum.QUERY)
    public ResponseData list(SysOnlineUserParam sysOnlineUserParam) {
        return new SuccessResponseData(sysOnlineUserService.list(sysOnlineUserParam));
    }

    /**
     * 在线用户强退
     *
     * @author xuyuxiang
     * @date 2020/4/7 17:36
     */
    @Permission
    @PostMapping("/sysOnlineUser/forceExist")
    @BusinessLog(title = "系统在线用户_强退", opType = LogAnnotionOpTypeEnum.FORCE)
    public ResponseData forceExist(@RequestBody  @Validated(SysOrgParam.force.class) SysOnlineUserParam sysOnlineUserParam) {
        sysOnlineUserService.forceExist(sysOnlineUserParam);
        return new SuccessResponseData();
    }
}
