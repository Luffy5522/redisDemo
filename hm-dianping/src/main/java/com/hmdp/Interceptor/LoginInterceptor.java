package com.hmdp.Interceptor;

import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.utils.SystemConstants;
import com.hmdp.utils.UserHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Author Luffy5522
 * @date: 2023/3/2 9:46
 * @description: 登录校验拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {

    // 在controller操作之前进行拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1.获取session
        HttpSession session = request.getSession();

        // 2.获取session中的用户
        UserDTO user = (UserDTO) session.getAttribute(SystemConstants.user);

        // 3.判断用户是否存在
        if (user == null) {
            // 4.不存在,拦截
            // 返回401状态码
            response.setStatus(401);
        }

        // 5.存在,保存用户信息到ThreadLocal
        UserHolder.saveUser(user);
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
