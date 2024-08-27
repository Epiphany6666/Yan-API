package com.luoyan;

import com.luoyan.yanapiclientsdk.utils.SignUtils;
import com.luoyan.yanapicommon.model.entity.InterfaceInfo;
import com.luoyan.yanapicommon.model.entity.User;
import com.luoyan.yanapicommon.service.InnerInterfaceInfoService;
import com.luoyan.yanapicommon.service.InnerUserInterfaceInfoService;
import com.luoyan.yanapicommon.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    private static final String INTERFACE_HOST = "http://localhost:8123";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 请求日志
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求唯一标识：" + request.getId());
        String path = INTERFACE_HOST + request.getPath().value();
        log.info("请求路径：" + path);
        String method = request.getMethod().toString();
        log.info("请求方法：" + method);
        log.info("请求参数：" + request.getQueryParams());
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址：" + sourceAddress);
        log.info("请求来源地址：" + request.getRemoteAddress());

        // 拿到响应对象
        ServerHttpResponse response = exchange.getResponse();
        // 2. 访问控制，黑白名单
//        if (!IP_WHITE_LIST.contains(sourceAddress)) {
//            response.setStatusCode(HttpStatus.FORBIDDEN);
//            return response.setComplete();
//        }

        // 4. 用户鉴权（判断 ak, sk 是否合法）
        HttpHeaders headers = request.getHeaders();
        // 从请求头中获取参数
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");

        // todo 实际情况应该是去数据库中查是否已分配给用户
        User invokeUser = null;
        try {
            // 调用内部服务，根据访问密钥获取用户信息
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            // 捕获异常，记录日志
            log.error("getInvokeUser error", e);
        }

        if (invokeUser == null) {
            // 如果用户信息为空，处理未授权情况并返回响应
            return handleNoAuth(response);
        }

//        if (!"luoyan".equals(accessKey)) {
//            return handleNoAuth(response);
//        }
//        if (!accessKey.equals("luoyan")) {
//            throw new RuntimeException("无权限");
//        }

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
//        String serverSign = SignUtils.genSign(body, "abcdefgh");
//        // 如果生成的签名不一致，则抛出异常，并提示“无权限”
//        if (!sign.equals(serverSign)) {
//            throw new RuntimeException("无权限");
//        }
        // 从获取到的用户信息中获取用户的秘钥
        String secretKey = invokeUser.getSecretKey();
        // 使用获取的秘钥对请求体进行签名
        String serverSign = SignUtils.genSign(body, secretKey);
        // 检查请求中的签名是否为空，或者是否与服务器生成的签名不一致
        if (sign == null || !sign.equals(serverSign)) {
            // 如果签名为空或者签名不一致，返回处理未授权的响应
            return handleNoAuth(response);
        }

        // 4. 请求的模拟接口是否存在，以及请求方法是否匹配
        // 初始化一个 InterfaceInfo 对象，用于存储查询结果
        InterfaceInfo interfaceInfo = null;

        try {
            // 尝试从内部接口信息服务获取指定路径和方法的接口信息
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        } catch (Exception e) {
            // 如果获取接口信息时出现异常，记录错误日志
            log.error("getInterfaceInfo error", e);
        }

        // 检查是否成功获取到接口信息
        if (interfaceInfo == null) {
            // 如果未获取到接口信息，返回处理未授权的响应
            return handleNoAuth(response);
        }

        // todo 是否有调用次数

        // 5. 请求的 Mock 接口是否存在？

        // 6. 请求参数，调用 Mock 接口校验
        // 7. 响应日志
        // 8. 调用次数，接口调用最大次数 + 1
        // 调用成功之后要输入一个响应日志
        // 9. 调用限流，返回一个规范的错误码
        return handleResponse(exchange, chain, interfaceInfo.getId(), invokeUser.getId());
    }

    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            // 获取原始的响应对象
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 获取数据缓冲工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 获取响应的状态码
            HttpStatus statusCode = originalResponse.getStatusCode();

            // 判断状态码是否为200 OK(按道理来说,现在没有调用,是拿不到响应码的,对这个保持怀疑 沉思.jpg)
            if (statusCode == HttpStatus.OK) {
                // 创建一个装饰后的响应对象(开始穿装备，增强能力)
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {

                    // 重写writeWith方法，用于处理响应体的数据
                    // 这段方法就是只要当我们的模拟接口调用完成之后,等它返回结果，
                    // 就会调用writeWith方法,我们就能根据响应结果做一些自己的处理
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        // Flux是reactor包中的，也是响应式编程里的一个对象
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        // 判断响应体是否是Flux类型
                        if (body instanceof Flux) {
                            // 拿到真正的body
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 返回一个处理后的响应体
                            // (这里就理解为它在拼接字符串,它把缓冲区的数据取出来，一点一点拼接好)
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                // todo 从数据库中查询模拟接口是否存在，以及请求方法是否匹配（还可以校验请求参数）
                                try {
                                    // 调用内部用户接口信息服务，记录接口调用次数
                                    innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                } catch (Exception e) {
                                    // 捕获异常并记录错误信息
                                    log.error("invokeCount error", e);
                                }
                                // 读取响应体的内容并转换为字节数组
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 构建日志
                                StringBuilder sb2 = new StringBuilder(200);
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                //rspArgs.add(requestUrl);
                                String data = new String(content, StandardCharsets.UTF_8);//data
                                sb2.append(data);
                                // 打印日志
                                log.info(sb2.toString(), rspArgs.toArray());//log.info("<-- {} {}\n", originalResponse.getStatusCode(), data);
                                // 将处理后的内容重新包装成DataBuffer并返回
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            log.error("网关处理异常", getStatusCode());
                        }
                        // 将body拿到写到响应里
                        return super.writeWith(body);
                    }
                };
                // 对于200 OK的请求,将装饰后的响应对象传递给下一个过滤器链,并继续处理(设置repsonse对象为装饰过的)
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            // 对于非200 OK的请求，直接返回，进行降级处理
            return chain.filter(exchange);
        } catch (Exception e) {
            // 处理异常情况，记录错误日志
            log.error("gateway log exception.\n" + e);
            return chain.filter(exchange);
        }
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