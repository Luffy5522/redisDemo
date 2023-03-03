package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.constants.RedisConstants;
import com.hmdp.constants.SystemConstants;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.entity.UserInfo;
import com.hmdp.mapper.UserInfoMapper;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserInfoService;
import com.hmdp.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-24
 */
@Service
@Slf4j
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    @Resource
    UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @param phone:   输入的手机号
     * @param session: 携带cookie的session
     * @return Result
     * @author Luffy5522
     * @description 发送验证码, 并进行保存
     * @date 2023/3/1 20:47
     */
    @Override
    public Result sendCode(String phone, HttpSession session) {
        log.info("准备发送验证码");

        // 1.校验手机号
        boolean phoneInvalid = RegexUtils.isPhoneInvalid(phone);
        if (phoneInvalid) {
            return Result.fail("手机格式错误");
        }

        // 2.生成一组六位数的验证码
        String code = RandomUtil.randomNumbers(6);

        // 3.保存验证码和手机到redis
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_CODE_KEY + phone,
                code, RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);


        // 4.发送验证码
        log.info("验证码为:{}", code);

        // 5.结束
        return Result.ok();
    }

    /**
     * @author Luffy5522
     * @description 实现登录校验
     * @date 2023/3/1 21:10
     */
    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {

        log.info("进行登录校验");

        // 1.判断手机和验证码是否一致
        String code = loginForm.getCode();
        String phone = loginForm.getPhone();

        // 从redis中获取验证码
        String s = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + phone);
        if (s == null || !s.equals(code)) {
            // 手机号和验证码不匹配
            log.info("验证码错误");
            return Result.fail("验证码错误");
        }


        // 2.根据手机号查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(wrapper);

        // 3.若用户不存在
        if (user == null) {
            // 创建新用户,并将用户保存到数据库
            user = register(loginForm);
        }
        assert user == null;

        // 4.生成随机的token
        String token = UUID.randomUUID().toString();

        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString())
        );

        // 存储
        stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_USER_KEY + token, stringObjectMap);

        // 设置token有效期
        stringRedisTemplate.expire(RedisConstants.LOGIN_USER_KEY + token,
                RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
        return Result.ok("登录成功");
    }

    public User register(LoginFormDTO loginFormDTO) {
        User user = new User();
        log.info("进行注册");
        BeanUtils.copyProperties(loginFormDTO, user);

        user.setNickName(SystemConstants.USER_NICK_NAME_PREFIX + RandomUtil.randomNumbers(8));
        user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("注册成功");

        return user;
    }
}
