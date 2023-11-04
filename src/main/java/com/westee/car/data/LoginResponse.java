package com.westee.car.data;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.westee.car.generate.User;

public class LoginResponse {
    private SaTokenInfo tokenInfo;
    private User user;

    public SaTokenInfo getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(SaTokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
