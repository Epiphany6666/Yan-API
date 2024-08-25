package com.luoyan.yanapiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.luoyan.yanapiclientsdk.model.User;

import java.util.HashMap;
import java.util.Map;

import static com.luoyan.yanapiclientsdk.utils.SignUtils.genSign;


/**
 * 调用第三方接口的客户端
 */
public class YanApiClient {

    private static final String GATEWAY_HOST = "http://localhost:8090";

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
        String result = HttpUtil.get(GATEWAY_HOST + "/api/name/", paramMap);
        System.out.println(result);
        return result;
    }

    public String getNameByPost(String name) {
        // 可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.post(GATEWAY_HOST + "/api/name/", paramMap);
        System.out.println(result);
        return result;
    }

    /**
     * 获取请求头的哈希映射
     * @param body 请求体内容
     * @return 包含请求头参数的哈希映射
     */
    private Map<String, String> getHeaderMap(String body) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        // 注意：不能直接发送密钥
        // hashMap.put("secretKey", secretKey);
        // 生成随机数(生成一个包含4个随机数字的字符串)
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        // 请求体内容
        hashMap.put("body", body);
        // 当前时间戳
        // System.currentTimeMillis()返回当前时间的毫秒数。通过除以1000，可以将毫秒数转换为秒数，以得到当前时间戳的秒级表示
        // String.valueOf()方法用于将数值转换为字符串。在这里，将计算得到的时间戳（以秒为单位）转换为字符串
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        // 生成签名
        hashMap.put("sign", genSign(body, secretKey));
        return hashMap;
    }

    /**
     * 通过POST请求获取用户名
     * @param user 用户对象
     * @return 从服务器获取的用户名
     */
    public String getUserNameByPost(User user) {
        // 将用户对象转换为JSON字符串
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                // 添加请求头
                .addHeaders(getHeaderMap(json))
                // 设置请求体
                .body(json)
                // 发送POST请求
                .execute();
        // 打印响应状态码
        System.out.println(httpResponse.getStatus());
        // 打印响应体内容
        String result = httpResponse.body();
        System.out.println(result);
        return result;
    }
}