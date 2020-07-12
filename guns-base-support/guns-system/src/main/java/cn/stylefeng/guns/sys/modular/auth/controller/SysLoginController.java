package cn.stylefeng.guns.sys.modular.auth.controller;

import cn.hutool.core.lang.Dict;
import cn.stylefeng.guns.core.context.login.LoginContextHolder;
import cn.stylefeng.guns.core.pojo.response.ResponseData;
import cn.stylefeng.guns.core.pojo.response.SuccessResponseData;
import cn.stylefeng.guns.sys.modular.auth.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 登录控制器
 *
 * @author xuyuxiang
 * @date 2020/3/11 12:20
 */
@RestController
public class SysLoginController {

    @Resource
    private AuthService authService;

    /**
     * 账号密码登录
     *
     * @author xuyuxiang
     * @date 2020/3/11 15:52
     */
    @PostMapping("/login")
    public ResponseData login(@RequestBody Dict dict) {
        String account = dict.getStr("account");
        String password = dict.getStr("password");
        String token = authService.login(account, password);
        return new SuccessResponseData(token);
    }

    /**
     * 退出登录
     *
     * @author xuyuxiang
     * @date 2020/3/16 15:02
     */
    @GetMapping("/logout")
    public void logout() {
        authService.logout();
    }

    /**
     * 获取当前登录用户信息
     *
     * @author xuyuxiang, fengshuonan
     * @date 2020/3/23 17:57
     */
    @GetMapping("/getLoginUser")
    public ResponseData getLoginUser() {
        return new SuccessResponseData(LoginContextHolder.me().getSysLoginUser());
    }

}
