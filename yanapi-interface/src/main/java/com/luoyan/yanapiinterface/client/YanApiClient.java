package com.luoyan.client;

import cn.hutool.core.lang.Console;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.luoyan.yanapiinterface.model.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * 调用第三方接口的客户端
 */
public class YanApiClient {

    private String accessKey;

    private String secretKey;

    public YanApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name) {
        // 可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.get("http://localhost:8123/api/name/", paramMap);
        System.out.println(result);
        return result;
    }

    public String getNameByPost(@RequestParam String name) {
        // 可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.post("http://localhost:8123/api/name/", paramMap);
        System.out.println(result);
        return result;
    }

    // 创建一个私有方法，用于构造请求头
    private Map<String, String> getHeaderMap() {
        // 创建一个新的 HashMap 对象
        Map<String, String> hashMap = new HashMap<>();
        // 将 "accessKey" 和其对应的值放入 map 中
        hashMap.put("accessKey", accessKey);
        // 将 "secretKey" 和其对应的值放入 map 中
        hashMap.put("secretKey", secretKey);
        // 返回构造的请求头 map
        return hashMap;
    }

    public String getUserNameByPost(@RequestBody User user) {
        HttpResponse httpResponse = HttpRequest.post("http://localhost:8123/api/name/user")
                .addHeaders(getHeaderMap())
                .body(JSONUtil.toJsonStr(user))
                .execute();
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        System.out.println(result);
        return result;
    }
}