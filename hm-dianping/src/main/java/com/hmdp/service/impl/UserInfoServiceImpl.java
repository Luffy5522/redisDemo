package com.hmdp.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.UserInfo;
import com.hmdp.mapper.UserInfoMapper;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserInfoService;
import com.hmdp.utils.RegexPatterns;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

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

    @Autowired
    UserMapper userMapper;

    /**
     * @param phone: 输入的手机号
     * @param session: 携带cookie的session
     * @return Result
     * @author Luffy5522
     * @description 发送验证码,并进行保存
     * @date 2023/3/1 20:47
     */
    @Override
    public Result sendCode(String phone, HttpSession session) {
        log.info("准备发送验证码");

        // 1.校验手机号
        boolean matches = RegexPatterns.PHONE_REGEX.matches(phone);
        if (!matches) {
            return Result.fail("手机号格式错误,请重新输入");
        }

        // 2.生成一组六位数的验证码
        String code = RandomUtil.randomNumbers(6);

        // 3.保存验证码到session
        session.setAttribute("code",code);

        // 4.发送验证码
        log.info("验证码为:{}",code);

        // 5.结束
        return Result.ok();
    }
}
