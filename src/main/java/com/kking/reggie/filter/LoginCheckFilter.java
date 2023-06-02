package com.kking.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.kking.reggie.common.BaseContext;
import com.kking.reggie.common.R;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

/**
 * 检查用户是否已经完成
 *
 * @author kking
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        log.info("拦截到请求:{}", request.getRequestURI());
        /**
         * 1.获取本次请求的uri
         * 定义不需要处理的请求路径
         */
        String requestURI = request.getRequestURI();
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",//移动端发送短信
                "/user/login"//移动端登录
        };

        /**
         * 2.判断本次请求是否需要处理
         */
        boolean check = check(requestURI, urls);
        /**
         * 3.如果不需要处理，直接放行
         */
        if (check) {
            log.info("本次{}请求不需要处理", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }
        /**
         * 4-1.如果需要处理，判断用户是否已经登录
         */
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已登录，用户id{}", request.getSession().getAttribute("employee"));
            Long employeeId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(employeeId);
            filterChain.doFilter(request, response);
            return;
        }
        /**
         * 4-2.判断移动端用户登陆状态，如果需要处理，判断用户是否已经登录
         */
        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已登录，用户id{}", request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }


        log.info("用户未登录");
        /**
         * 5.如果未登录则返回未登录结果,通过输出流的方式向客户端界面响应数据
         */
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 路径匹配，判断是否需要放行
     *
     * @param requestURI
     * @return
     */
    public boolean check(String requestURI, String[] urls) {
        for (String url : urls) {
            if (antPathMatcher.match(url, requestURI)) {
                return true;
            }
        }
        return false;
    }
}
