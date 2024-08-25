package com.luoyan.yanapigateway;

import com.luoyan.yanapiclientsdk.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 请求日志
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求唯一标识：" + request.getId());
        log.info("请求路径：" + request.getPath().value());
        log.info("请求方法：" + request.getMethod());
        log.info("请求参数：" + request.getQueryParams());
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址：" + sourceAddress);
        log.info("请求来源地址：" + request.getRemoteAddress());

        // 拿到响应对象
        ServerHttpResponse response = exchange.getResponse();
        // 2. 访问控制，黑白名单
        if (!IP_WHITE_LIST.contains(sourceAddress)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }

        // 4. 用户鉴权（判断 ak, sk 是否合法）
        HttpHeaders headers = request.getHeaders();
        // 从请求头中获取参数
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");

        // todo 实际情况应该是去数据库中查是否已分配给用户
        if (!accessKey.equals("luoyan")) {
            throw new RuntimeException("无权限");
        }

        // 直接校验如果随机数大于1万，则抛出异常，并提示“无权限”
        if (Long.parseLong(nonce) > 10000) {
            throw new RuntimeException("无权限");
        }

        // 首先,获取当前时间的时间戳,以秒为单位
        // System.currentTimeMillis()返回当前时间的毫秒数，除以1000后得到当前时间的秒数。
        Long currentTime = System.currentTimeMillis() / 1000;
        // 定义一个常量FIVE_MINUTES,表示五分钟的时间间隔(乘以60,将分钟转换为秒,得到五分钟的时间间隔)。
        final Long FIVE_MINUTES = 60 * 5L;
        // 判断当前时间与传入的时间戳是否相差五分钟或以上
        // Long.parseLong(timestamp)将传入的时间戳转换成长整型
        // 然后计算当前时间与传入时间戳之间的差值(以秒为单位),如果差值大于等于五分钟,则返回true,否则返回false
        if ((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            // 如果时间戳与当前时间相差五分钟或以上，调用handleNoAuth(response)方法进行处理
            return handleNoAuth(response);
        }

        // todo 实际情况是从数据库中查出 secretKey
        String serverSign = SignUtils.genSign(body, "abcdefgh");
        // 如果生成的签名不一致，则抛出异常，并提示“无权限”
        if (!sign.equals(serverSign)) {
            throw new RuntimeException("无权限");
        }

        // 5. 请求的 Mock 接口是否存在？
        // todo 从数据库中查询模拟接口是否存在，以及请求方法是否匹配（还可以校验请求参数）
        if (response.getStatusCode() == HttpStatus.OK) {

        } else {
            // 8.调用失败，返回一个规范的错误码
            return handleInvokeError(response);
        }
        // 6. 请求参数，调用 Mock 接口校验
        // 7. 响应日志
        // 8. 调用次数，接口调用最大次数 + 1
        Mono<Void> filter = chain.filter(exchange);
        // 调用成功之后要输入一个响应日志
        log.info("响应: " + response.getStatusCode());
        // 9. 调用限流，返回一个规范的错误码
        log.info("custom global filter");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}