package com.example.fooddelivery.filter;

import com.alibaba.fastjson.JSON;
import com.example.fooddelivery.common.BaseContext;
import com.example.fooddelivery.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登陆，知识点：
 * 1 使用webfilter开启过滤器，同时要开启@ServletComponentScan
 * 2 通过request.getRequestURI()获取链接，通过AntPathMatcher判断链接是否需要过滤
 * 3 filterChain.doFilter(request,response)放行
 *
 */
@Slf4j
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    String[] urls = new String[]{
            "/employee/login",
            "/employee/logout",
            "/backend/**",
            "/front/**",
            "/common/**",
            "/user/sendMsg",
            "/user/login",
    };
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        log.info("拦截到请求: {}",request.getRequestURI());
        //1 获取本次请求的url
        String currentUrl = request.getRequestURI();
        //2 判断这个url需不需要被处理，怎么处理通配符？

        //3 如果路径不需要被处理，那么就直接放
        if(checkUrl(currentUrl)){
            filterChain.doFilter(request,response);
            return;
        }
        //4 需要被处理的话就去判断登陆状态
        if(request.getSession().getAttribute("employee") != null) {
//            log.info("用户已登陆，ID为： {}",request.getSession().getAttribute("employee"));
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee"));
            filterChain.doFilter(request, response);
            return;
        }
        //4.2 手机端判断是否登陆
        if(request.getSession().getAttribute("user") != null){
//            log.info("用户已登陆，ID为： {}",request.getSession().getAttribute("employee"));
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("user"));
            filterChain.doFilter(request,response);
            return;
        }
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
        //5 如果未登陆就返回登陆界面
//        filterChain.doFilter(request,response);
    }

    /**
     * 检查url是否可以直接放行
     * @param currentUrl
     * @return
     */
    public boolean checkUrl(String currentUrl){
        for(String url:urls){
            if(PATH_MATCHER.match(url,currentUrl)) return true;
        }
        return false;
    }
}
