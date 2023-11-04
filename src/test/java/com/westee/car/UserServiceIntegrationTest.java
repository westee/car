package com.westee.car;

import com.fasterxml.jackson.core.type.TypeReference;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.westee.car.controller.AuthController;
import com.westee.car.data.LoginResponse;
import com.westee.car.entity.MyResponse;
import com.westee.car.generate.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CarApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
class UserServiceIntegrationTest extends AbstractIntegrationTest {

    private final String TEL = "13011111111";
    private final String CORRECT_PASSWORD = "123456";
    private final String INCORRECT_PASSWORD = "1234567";

    @Test
    void insertUserOk() {
        User user = createUser();
        Response response = doPostRequest("/auth/register", user);
        ResponseBody body = response.body();
        MyResponse<LoginResponse> myResponse;
        try {
            String string = body.string();
            myResponse = mapper.readValue(string, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(HttpStatus.OK.value(), response.code());

        assertEquals("ok", myResponse.message());
        assertEquals(1, myResponse.data().getUser().getId());
        assertNotNull(myResponse.data().getTokenInfo());
        assertNotNull(myResponse.data().getUser());
    }

    @Test
    void insertUserDuplicate() {
        User user = createUser();
        doPostRequest("/auth/register", user);
        Response response = doPostRequest("/auth/register", user);
        ResponseBody body = response.body();
        MyResponse<LoginResponse> myResponse;
        try {
            String string = body.string();
            myResponse = mapper.readValue(string, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.code());
        assertEquals("用户已注册", myResponse.message());
    }

    @Test
    void loginWhenIncorrect() {
        User user = createUser();
        doPostRequest("/auth/register", user);

        AuthController.TelAndPassword telAndPassword = new AuthController.TelAndPassword(TEL, INCORRECT_PASSWORD);
        Response response = doPostRequest("/auth/login", telAndPassword);
        ResponseBody body = response.body();
        MyResponse<LoginResponse> myResponse;
        try {
            String string = body.string();
            myResponse = mapper.readValue(string, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.code());
        assertEquals("账号密码不匹配", myResponse.message());
    }

    @Test
    void loginWhenCorrect() {
        User user = createUser();
        doPostRequest("/auth/register", user);

        AuthController.TelAndPassword telAndPassword = new AuthController.TelAndPassword(TEL, CORRECT_PASSWORD);
        Response response = doPostRequest("/auth/login", telAndPassword);
        ResponseBody body = response.body();
        MyResponse<LoginResponse> myResponse;
        try {
            String string = body.string();
            myResponse = mapper.readValue(string, new TypeReference<>() {

            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(HttpStatus.OK.value(), response.code());
        assertEquals("ok", myResponse.message());
        assertEquals(1, myResponse.data().getUser().getId());
        assertNotNull(myResponse.data().getTokenInfo());
        assertNotNull(myResponse.data().getUser());
    }

    @Test
    void loginWhenNotRegister() {
        AuthController.TelAndPassword telAndPassword = new AuthController.TelAndPassword(TEL, CORRECT_PASSWORD);
        Response response = doPostRequest("/auth/login", telAndPassword);
        ResponseBody body = response.body();
        MyResponse<LoginResponse> myResponse;
        try {
            String string = body.string();
            myResponse = mapper.readValue(string, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.code());
        assertEquals("账号密码不匹配", myResponse.message());
    }

    @Test
    void checkLoginStatusBeforeAndAfter() {
        HashMap<String, String> param = new HashMap<>();
        param.put("loginId", "1");
        doGetRequest("/auth/logout", param);
        AuthController.TelAndPassword telAndPassword = new AuthController.TelAndPassword(TEL, CORRECT_PASSWORD);
        Response response = doGetRequest("/auth/is_login", param);
        ResponseBody body = response.body();
        MyResponse<Boolean> myResponse;
        try {
            String string = body.string();
            myResponse = mapper.readValue(string, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(HttpStatus.OK.value(), response.code());
        assertEquals(false, myResponse.data());

        // 1.登录后保存saTokenInfo中的id
        doPostRequest("/auth/register", telAndPassword);

        // 2.请求is_login返回true
        response = doGetRequest("/auth/is_login", param);
        body = response.body();
        try {
            String string = body.string();
            myResponse = mapper.readValue(string, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 返回true
        assertEquals(HttpStatus.OK.value(), response.code());
        assertEquals(true, myResponse.data());
    }

    User createUser() {
        User user = new User();
        user.setTel(TEL);
        user.setPassword(CORRECT_PASSWORD);
        return user;
    }

}