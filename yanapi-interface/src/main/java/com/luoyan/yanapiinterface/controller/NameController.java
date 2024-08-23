package com.luoyan.yanapiinterface.controller;
import com.luoyan.yanapiclientsdk.model.User;
import com.luoyan.yanapiclientsdk.utils.SignUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 名称 API
 */
@RestController
@RequestMapping("/name")
public class NameController {
    @GetMapping("/get")
    public String getNameByGet(String name) {
        return "GET 你的名字是" + name;
    }

    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name) {
        return "POST 你的名字是" + name;
    }

    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request) {
        // 从请求头中获取名为 "accessKey" 的值
        String accessKey = request.getHeader("accessKey");
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        String body = request.getHeader("body");

        // todo 实际情况应该是去数据库中查看是否已分配给用户
        if (!accessKey.equals("luoyan")) {
            throw new RuntimeException("无权限");
        }

        // 直接校验如果随机机大于1万，则抛出异常，并提示“无权限”
        if (Long.parseLong(nonce) > 10000) {
            throw new RuntimeException("无权限");
        }

        // todo 时间和当前时间不能超过5分钟
        // if (timestamp) {}

        // todo 实际情况中是从数据库中查出 secretKey
        String serverSign = SignUtils.genSign(body, "abcdefgh");
        // 如果生成的签名不一致，则抛出异常，并提示“无权限”
        if (!sign.equals(serverSign)) {
            throw new RuntimeException("无权限");
        }

        String result = "POST 用户名是" + user.getUsername();
        return result;

    }
}