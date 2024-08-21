package com.luoyan.yanapiinterface.controller;

import com.luoyan.yanapiinterface.model.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 名称 API
 */
@RestController
@RequestMapping("/name")
public class NameController {
    @GetMapping("/")
    public String getNameByGet(String name) {
        return "GET 你的名字是" + name;
    }

    @PostMapping("/")
    public String getNameByPost(@RequestParam String name) {
        return "POST 你的名字是" + name;
    }

    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request) {
        // 从请求头中获取名为 "accessKey" 的值
        String accessKey = request.getHeader("accessKey");
        // 从请求头中获取名为 "secretKey" 的值
        String secretKey = request.getHeader("secretKey");
        // 如果 accessKey 不等于 "luoyan" 或者 secretKey 不等于 "abcdefgh"
        if (!accessKey.equals("luoyan") || !secretKey.equals("abcdefgh")){
            // 抛出一个运行时异常，表示权限不足
            throw new RuntimeException("无权限");
        }
        // 如果权限校验通过，返回 "POST 用户名字是" + 用户名
        return "POST 用户名字是" + user.getUsername();
    }
}