package com.hmdp.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;
import com.hmdp.entity.UserInfo;
import com.hmdp.mapper.UserInfoMapper;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserInfoService;
import com.hmdp.utils.RegexUtils;
import com.hmdp.utils.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

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

        // 3.保存验证码和手机到session
        session.setAttribute(SystemConstants.code, code);
        session.setAttribute(SystemConstants.phone, phone);

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

        if (!code.equals(session.getAttribute(SystemConstants.code)) ||
                !phone.equals(session.getAttribute(SystemConstants.phone))) {
            return Result.fail("验证码错误");
        }


        // 2.根据手机号查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(wrapper);

        // 3.若用户不存在
        if (user == null) {
            return register(loginForm, user);
        }

        // 4.保存用户到session
        session.setAttribute(SystemConstants.user, user);
        return Result.ok("登录成功");
    }

    public Result register(LoginFormDTO loginFormDTO, User user) {
        log.info("进行注册");
        BeanUtils.copyProperties(loginFormDTO, user);

        user.setNickName(SystemConstants.USER_NICK_NAME_PREFIX + RandomUtil.randomNumbers(8));
        user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        return Result.ok("注册成功");


    }
}
