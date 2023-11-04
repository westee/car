package com.westee.car.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.westee.car.data.LoginResponse;
import com.westee.car.entity.MyResponse;
import com.westee.car.generate.User;
import com.westee.car.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/")
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("login")
    public MyResponse<LoginResponse> doLogin(@RequestBody TelAndPassword telAndPassword) {
        return MyResponse.ok(userService.login(telAndPassword));
    }

    @PostMapping("register")
    public MyResponse<LoginResponse> doRegister(@RequestBody User requestUser) {
        return MyResponse.ok(userService.insertUser(requestUser));
    }

    @RequestMapping("is_login")
    public MyResponse<Boolean> isLogin(int loginId) {
        return MyResponse.ok(StpUtil.isLogin(loginId));
    }

    @RequestMapping("token_info")
    public SaResult tokenInfo() {
        return SaResult.data(StpUtil.getTokenInfo());
    }

    @RequestMapping("logout")
    public MyResponse<String> logout(int loginId) {
        return MyResponse.ok(userService.doLogout(loginId));
    }

    public static class TelAndPassword {
        public String tel;
        public String password;

        public TelAndPassword(String tel, String password) {
            this.tel = tel;
            this.password = password;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
