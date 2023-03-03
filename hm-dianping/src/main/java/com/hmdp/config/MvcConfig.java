package com.hmdp.config;

import com.hmdp.Interceptor.LoginInterceptor;
import com.hmdp.Interceptor.RefreshInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @Author Luffy5522
 * @date: 2023/3/2 10:04
 * @description: Mvc相关的配置
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 添加登录拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 拦截所有请求
        registry.addInterceptor(new RefreshInterceptor(stringRedisTemplate)).order(0);

        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                        // 排除不需要拦截的路径
                        "/shop/**",
                        "/voucher/**",
                        "/shop-type",
                        "/upload/**",
                        "/blog/hot",
                        "/user/code",
                        "/user/login"
                ).order(1);


    }
}

