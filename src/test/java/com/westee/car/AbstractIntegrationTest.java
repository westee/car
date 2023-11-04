package com.westee.car;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.Map;

public class AbstractIntegrationTest {

    @Value("${spring.datasource.url}")
    private String databaseUrl;
    @Value("${spring.datasource.username}")
    private String databaseUsername;
    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Autowired
    Environment environment;
    private static final OkHttpClient client = new OkHttpClient();

    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void initDataBase() {
        ClassicConfiguration conf = new ClassicConfiguration();
        conf.setCleanDisabled(false);
        conf.setDataSource(databaseUrl, databaseUsername, databasePassword);
        Flyway flyway = new Flyway(conf);
        flyway.clean();
        flyway.migrate();
    }

    public String getUrl(String apiName) {
        // 获取集成测试的端口号
        return "http://localhost:" + environment.getProperty("local.server.port") + apiName;
    }

    public Response doGetRequest(String url, Object requstBody) {
        return doHttpRequest(getUrl(url), "GET", requstBody);
    }

    public Response doPostRequest(String url, Object requstBody) {
        return doHttpRequest(getUrl(url), "POST", requstBody);
    }

    public Response doHttpRequest(String url, String method, Object requestBody) {

        Request.Builder requestBuilder = new Request.Builder();

        if (method.equalsIgnoreCase("GET")) {
            if (requestBody != null) {
                String getUrl = url + "?" + mapToQueryString((Map<String, String>) requestBody);
                requestBuilder.url(getUrl);
            }
        } else if (method.equalsIgnoreCase("PATCH")) {
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());
            requestBuilder = requestBuilder.url(url).patch(body);
        } else if (method.equalsIgnoreCase("DELETE")) {
            requestBuilder = requestBuilder.url(url).delete();
        } else if (method.equalsIgnoreCase("POST")) {
            String s;
            try {
                s = mapper.writeValueAsString(requestBody);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), s);
            requestBuilder = requestBuilder.url(url).post(body);
        }

        Request request = requestBuilder.build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response;
//        String responseBody = null;
//        try {
//            return response.body().string();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    public String mapToQueryString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!sb.isEmpty()) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }

}
