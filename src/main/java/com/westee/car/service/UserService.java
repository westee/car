package com.westee.car.service;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.westee.car.controller.AuthController;
import com.westee.car.data.LoginResponse;
import com.westee.car.exceptions.MyHttpException;
import com.westee.car.generate.User;
import com.westee.car.generate.UserExample;
import com.westee.car.generate.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserService {
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public LoginResponse insertUser(User user) {
        try {
            String hashpw = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashpw);
            Date date = new Date();
            user.setUpdatedAt(date);
            user.setCreatedAt(date);
            userMapper.insert(user);
            StpUtil.login(user.getId());
            SaTokenInfo saTokenInfo = StpUtil.getTokenInfo();
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setUser(user);
            loginResponse.setTokenInfo(saTokenInfo);
            return loginResponse;
        } catch (DuplicateKeyException e) {
            throw MyHttpException.badRequest("用户已注册");
        }
    }

    public LoginResponse login(AuthController.TelAndPassword telAndPassword) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andTelEqualTo(telAndPassword.getTel());
        List<User> users = userMapper.selectByExample(userExample);
        if (!users.isEmpty() && BCrypt.checkpw(telAndPassword.getPassword(), users.get(0).getPassword())) {
            User user = users.get(0);
            StpUtil.login(user.getId());
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setUser(user);
            loginResponse.setTokenInfo(StpUtil.getTokenInfo());
            return loginResponse;
        }
        throw MyHttpException.badRequest("账号密码不匹配");
    }

    public String doLogout(int loginId) {
        StpUtil.logout(loginId);
        return "退出登录";
    }
}
