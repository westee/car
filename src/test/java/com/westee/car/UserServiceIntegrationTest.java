package com.westee.car;

import com.westee.car.exceptions.MyHttpException;
import com.westee.car.generate.User;
import com.westee.car.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CarApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
class UserServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void insertUserShouldInsertUserWhenValidUserProvided() {
        // 创建一个示例用户
        User user = new User();
        user.setTel("13011111111");
        user.setPassword("password");

        // 调用insertUser方法
        User result = userService.insertUser(user);

        // 验证返回的用户对象是否与输入的用户对象相同
        assertEquals(user, result);
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getTel(), result.getTel());
    }

    @Test
    void insertUserShouldThrowExceptionWhenDuplicateUserProvided() {
        // 创建一个示例用户
        User user = new User();
        user.setTel("13011111111");
        user.setPassword("password");
        userService.insertUser(user);
        // 调用insertUser方法，预期会抛出MyHttpException异常
        assertThrows(MyHttpException.class, () -> userService.insertUser(user));
    }
}