package com.chao.interceptor;

import com.chao.common.BaseContext;
import com.chao.common.ReturnMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class CheckTokenInterceptor implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        // 排除预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()))
            return true;

        String token = request.getHeader("token");
        if (token == null)
        {
            //未登录
            doResponse(response, ReturnMessage.commonError("no_login"));
            return false;
        }

        try
        {
            JwtParser parser = Jwts.parser();
            parser.setSigningKey("tZe0M6");
            Jws<Claims> claimsJws = parser.parseClaimsJws(token);
            Claims body = claimsJws.getBody();
            //将当前登录用户信息写入线程中
            BaseContext.setCurrentUserInfo(body.get("id", Long.class), body.get("type", Integer.class));

            return true;
        } catch (Exception e)
        {
            doResponse(response, ReturnMessage.commonError("login_outDate"));
        }
        return false;
    }

    private void doResponse(HttpServletResponse response, ReturnMessage<String> returnMessage) throws IOException
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        String s = new ObjectMapper().writeValueAsString(returnMessage);
        out.print(s);
        out.flush();
        out.close();
    }
}
