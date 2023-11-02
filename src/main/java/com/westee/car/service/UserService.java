package com.westee.car.service;

import cn.dev33.satoken.secure.BCrypt;
import com.westee.car.exceptions.MyHttpException;
import com.westee.car.generate.User;
import com.westee.car.generate.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User insertUser(User user) {
        try {
            String hashpw = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashpw);
            Date date = new Date();
            user.setUpdatedAt(date);
            user.setCreatedAt(date);
            userMapper.insert(user);
            return user;
        } catch (DuplicateKeyException e) {
            throw MyHttpException.badRequest("用户已注册");
        }
    }
}
