package cn.stylefeng.guns.sys.core.filter.xss;


import cn.stylefeng.guns.core.context.constant.ConstantContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * xss过滤器
 *
 * @author stylefeng
 * @date 2020/6/21 10:04
 */
public class XssFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String servletPath = httpServletRequest.getServletPath();

        // 获取不进行url过滤的接口
        List<String> unXssFilterUrl = ConstantContextHolder.getUnXssFilterUrl();
        if (unXssFilterUrl != null && unXssFilterUrl.contains(servletPath)) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request), response);
        }
    }

}