package cn.stylefeng.guns.core.consts;

/**
 * SpringSecurity相关常量
 *
 * @author xuyuxiang
 * @date 2020/3/18 17:49
 */
public interface SpringSecurityConstant {

    /**
     * 放开权限校验的接口
     */
    String[] NONE_SECURITY_URL_PATTERNS = {

            //前端的
            "/favicon.ico",

            //swagger相关的
            "/doc.html",
            "/webjars/**",
            "/swagger-resources/**",
            "/v2/api-docs",
            "/v2/api-docs-ext",
            "/configuration/ui",
            "/configuration/security",

            //后端的
            "/",
            "/login",
            "/logout",

            //文件的
            "/sysFileInfo/upload",
            "/sysFileInfo/download",
            "/sysFileInfo/preview",

            //druid的
            "/druid/**"
    };

}
