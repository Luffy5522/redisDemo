package com.hmdp.Interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.hmdp.constants.RedisConstants;
import com.hmdp.dto.UserDTO;
import com.hmdp.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author Luffy5522
 * @date: 2023/3/2 9:46
 * @description: 登录校验拦截器
 */
@Slf4j
public class RefreshInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public RefreshInterceptor() {
    }

    public RefreshInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    // 在controller操作之前进行拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.请求并携带token
        String token = request.getHeader("authorization");

        if (StrUtil.isBlank(token)) {
            return true;
        }

        String tokenKey = RedisConstants.LOGIN_USER_KEY + token;

        // 2.获取redis中的用户
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(tokenKey);

        if (entries.isEmpty()) {
            // 返回401状态码
            response.setStatus(401);
            return true;
        }

        // 3.将查询到的Hash数据转为UserDTo数据
        UserDTO userDTO = BeanUtil.fillBeanWithMap(entries, new UserDTO(), true);


        // 5.存在,保存用户信息到ThreadLocal
        UserHolder.saveUser(userDTO);

        // 6.更新登录有效期

        stringRedisTemplate.expire(tokenKey, RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
        return true;
    }

}
