package com.luoyan;

import com.luoyan.client.YanApiClient;
import com.luoyan.yanapiinterface.model.User;

public class Main {
    public static void main(String[] args) {
        YanApiClient yanApiClient = new YanApiClient();
        String result1 = yanApiClient.getNameByGet("洛言");
        String result2 = yanApiClient.getNameByPost("洛言");
        User user = new User();
        user.setUsername("洛言言");
        String result3 = yanApiClient.getUserNameByPost(user);
        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
    }
}
