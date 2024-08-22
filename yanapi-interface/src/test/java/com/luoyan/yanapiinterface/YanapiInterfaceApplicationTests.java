package com.luoyan.yanapiinterface;

import com.luoyan.yanapiclientsdk.client.YanApiClient;
import com.luoyan.yanapiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class YanapiInterfaceApplicationTests {

    @Autowired
    private YanApiClient yanApiClient;

    @Test
    void contextLoads() {
        String result1 = yanApiClient.getNameByGet("luoyan");
        String result2 = yanApiClient.getNameByPost("luoyan");
        User user = new User();
        user.setUsername("luoyan");
        String result3 = yanApiClient.getUserNameByPost(user);
        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
    }

}
