package com.hmdp.Interceptor;

import com.hmdp.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author Luffy5522
 * @date: 2023/3/2 9:46
 * @description: 登录校验拦截器
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {


    public LoginInterceptor() {
    }


    // 在controller操作之前进行拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断是否需要进行拦截
        if (UserHolder.getUser() == null) {
            response.setStatus(401);
            // 拦截
            return false;
        }

        // 方行
        return true;
    }


    // 在controller操作之后进行拦截
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    // 业务流程执行之后
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
