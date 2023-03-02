package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.UserInfo;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-24
 */
public interface IUserInfoService extends IService<UserInfo> {
    // 发送验证码,并保存
    Result sendCode(String phone, HttpSession session);
    // 登录功能
    Result login(LoginFormDTO loginForm, HttpSession session);
}
