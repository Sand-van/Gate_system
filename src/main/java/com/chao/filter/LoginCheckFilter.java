package com.chao.filter;

import com.alibaba.fastjson.JSON;
import com.chao.common.BaseContext;
import com.chao.common.ReturnMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter
{
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();

        //定义不需要处理的请求路径
        String[] acceptUrls = new String[]
                {
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources",
                        "/v2/api-docs",

                        "/user/login",
                        "/user/logout",
                        "/backend/**",
                        "/front/**",
                        "/deviceSocket"
                };
        //2、判断本次请求是否需要处理，如果不需要处理，则直接放行
        if (checkUrl(acceptUrls, requestURI))
        {
            filterChain.doFilter(request, response);
            return;
        }
        //3、判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("id") != null)
        {
            BaseContext.setCurrentID((Long) request.getSession().getAttribute("id"));
            filterChain.doFilter(request, response);
            return;
        }
        //4、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(ReturnMessage.commonError("NOT_LOGIN")));
    }

    /**
     * 判断是否拦截，路径匹配，检查本次请求是否需要放行
     * @param acceptUrls 允许的Url
     * @param requestUrl 请求的Url
     * @return 如果可以，返回true；否则false
     */
    public boolean checkUrl(String[] acceptUrls, String requestUrl)
    {
        for (String acceptUrl : acceptUrls)
        {
            if (PATH_MATCHER.match(acceptUrl, requestUrl))
                return true;
        }
        return false;
    }
}
