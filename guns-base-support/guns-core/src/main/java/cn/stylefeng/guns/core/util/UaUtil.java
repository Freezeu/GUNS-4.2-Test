package cn.stylefeng.guns.core.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.stylefeng.guns.core.consts.CommonConstant;
import cn.stylefeng.guns.core.consts.SymbolConstant;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户代理工具类
 *
 * @author xuyuxiang
 * @date 2020/3/19 14:52
 */
public class UaUtil {

    /**
     * 获取客户端浏览器
     *
     * @author xuyuxiang
     * @date 2020/3/19 14:53
     */
    public static String getBrowser(HttpServletRequest request) {
        UserAgent userAgent = getUserAgent(request);
        if (ObjectUtil.isEmpty(userAgent)) {
            return SymbolConstant.DASH;
        } else {
            String browser = userAgent.getBrowser().toString();
            return CommonConstant.UNKNOWN.equals(browser) ? SymbolConstant.DASH : browser;
        }
    }

    /**
     * 获取客户端操作系统
     *
     * @author xuyuxiang
     * @date 2020/3/19 14:53
     */
    public static String getOs(HttpServletRequest request) {
        UserAgent userAgent = getUserAgent(request);
        if (ObjectUtil.isEmpty(userAgent)) {
            return SymbolConstant.DASH;
        } else {
            String os = userAgent.getOs().toString();
            return CommonConstant.UNKNOWN.equals(os) ? SymbolConstant.DASH : os;
        }
    }

    /**
     * 获取请求代理头
     *
     * @author xuyuxiang
     * @date 2020/3/19 14:54
     */
    private static UserAgent getUserAgent(HttpServletRequest request) {
        String userAgentStr = ServletUtil.getHeaderIgnoreCase(request, CommonConstant.USER_AGENT);
        return UserAgentUtil.parse(userAgentStr);
    }
}
